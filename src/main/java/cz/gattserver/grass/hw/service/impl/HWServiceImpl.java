package cz.gattserver.grass.hw.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import cz.gattserver.grass.core.exception.GrassException;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.common.util.DateUtils;
import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass.hw.interfaces.HWServiceNoteTO;
import cz.gattserver.grass.hw.model.domain.HWItem;
import cz.gattserver.grass.hw.model.domain.HWItemType;
import cz.gattserver.grass.hw.model.domain.HWServiceNote;
import cz.gattserver.grass.hw.model.repositories.HWItemRepository;
import cz.gattserver.grass.hw.model.repositories.HWItemTypeRepository;
import cz.gattserver.grass.hw.model.repositories.HWServiceNoteRepository;
import cz.gattserver.grass.hw.service.HWMapperService;
import cz.gattserver.grass.hw.service.HWService;

@Transactional
@Component
public class HWServiceImpl implements HWService {

	private static final Logger logger = LoggerFactory.getLogger(HWServiceImpl.class);

	private static final String ILLEGAL_PATH_IMGS_ERR = "Podte??en?? adres????e grafick??ch p????loh";
	private static final String ILLEGAL_PATH_DOCS_ERR = "Podte??en?? adres????e dokumentac??";
	private static final String ILLEGAL_PATH_PRINT_3D_ERR = "Podte??en?? adres????e 3d model??";

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private HWItemRepository hwItemRepository;

	@Autowired
	private HWItemTypeRepository hwItemTypeRepository;

	@Autowired
	private HWServiceNoteRepository serviceNoteRepository;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private HWMapperService hwMapper;

	/*
	 * Config
	 */

	private HWConfiguration loadConfiguration() {
		HWConfiguration configuration = new HWConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	/**
	 * Z??sk?? {@link Path} dle jm??na adres????e HW polo??ky
	 * 
	 * @param id
	 *            id HW polo??ky
	 * @return {@link Path} adres???? galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje ko??enov?? adres???? HW -- chyba nastaven??
	 *             modulu HW
	 * @throws IllegalArgumentException
	 *             pokud p??edan?? adres???? podt??k?? ko??en modulu HW
	 */
	private Path getHWPath(Long id) {
		Validate.notNull(id, "ID HW polo??ky nesm?? b??t null");
		HWConfiguration configuration = loadConfiguration();
		String rootDir = configuration.getRootDir();
		Path rootPath = fileSystemService.getFileSystem().getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new IllegalStateException("Ko??enov?? adres???? HW modulu mus?? existovat");
		rootPath = rootPath.normalize();
		Path hwPath = rootPath.resolve(String.valueOf(id));
		if (!hwPath.normalize().startsWith(rootPath))
			throw new IllegalArgumentException("Podte??en?? ko??enov??ho adres????e galeri??");
		return hwPath;
	}

	private Path getHWItemDocumentsPath(Long id) throws IOException {
		HWConfiguration configuration = loadConfiguration();
		Path hwPath = getHWPath(id);
		Path file = hwPath.resolve(configuration.getDocumentsDir());
		if (!Files.exists(file))
			fileSystemService.createDirectoriesWithPerms(file);
		return file;
	}

	private Path getHWItemPrint3dPath(Long id) throws IOException {
		HWConfiguration configuration = loadConfiguration();
		Path hwPath = getHWPath(id);
		Path file = hwPath.resolve(configuration.getPrint3dDir());
		if (!Files.exists(file))
			fileSystemService.createDirectoriesWithPerms(file);
		return file;
	}

	private Path getHWItemImagesPath(Long id) throws IOException {
		HWConfiguration configuration = loadConfiguration();
		Path hwPath = getHWPath(id);
		Path file = hwPath.resolve(configuration.getImagesDir());
		if (!Files.exists(file))
			fileSystemService.createDirectoriesWithPerms(file);
		return file;
	}

	private HWItemFileTO mapPathToItem(Path path) {
		HWItemFileTO to = new HWItemFileTO().setName(path.getFileName().toString());
		try {
			to.setSize(HumanBytesSizeFormatter.format(Files.size(path), true));
		} catch (IOException e) {
			to.setSize("n/a");
		}
		try {
			to.setLastModified(
					LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault()));
		} catch (IOException e) {
			to.setLastModified(null);
		}
		return to;
	}

	/*
	 * Images
	 */

	@Override
	public void saveImagesFile(InputStream in, String fileName, HWItemTO item) throws IOException {
		Path imagesPath;
		imagesPath = getHWItemImagesPath(item.getId());
		Path imagePath = imagesPath.resolve(fileName);
		if (!imagePath.normalize().startsWith(imagesPath))
			throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
		Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
		fileSystemService.grantPermissions(imagePath);
	}

	@Override
	public List<HWItemFileTO> getHWItemImagesFiles(Long id) {
		Path imagesPath;
		try {
			imagesPath = getHWItemImagesPath(id);
			List<HWItemFileTO> list = new ArrayList<>();
			try (Stream<Path> stream = Files.list(imagesPath)) {
				stream.forEach(p -> list.add(mapPathToItem(p)));
			}
			return list;
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat p??ehled grafick??ch p????loh HW polo??ky.", e);
		}
	}

	@Override
	public long getHWItemImagesFilesCount(Long id) {
		Path imagesPath;
		try {
			imagesPath = getHWItemImagesPath(id);
			try (Stream<Path> stream = Files.list(imagesPath)) {
				return stream.count();
			}
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat p??ehled grafick??ch p????loh HW polo??ky.", e);
		}
	}

	@Override
	public Path getHWItemImagesFilePath(Long id, String name) {
		Path images;
		try {
			images = getHWItemImagesPath(id);
			Path image = images.resolve(name);
			if (!image.normalize().startsWith(images))
				throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
			return image;
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat grafickou p????lohu HW polo??ky.", e);
		}
	}

	@Override
	public InputStream getHWItemImagesFileInputStream(Long id, String name) {
		try {
			return Files.newInputStream(getHWItemImagesFilePath(id, name));
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat grafickou p????lohu HW polo??ky.", e);
		}
	}

	@Override
	public boolean deleteHWItemImagesFile(Long id, String name) {
		Path images;
		try {
			images = getHWItemImagesPath(id);
			Path image = images.resolve(name);
			if (!image.normalize().startsWith(images))
				throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
			return Files.deleteIfExists(image);
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se smazat grafickou p????lohu HW polo??ky.", e);
		}
	}

	/*
	 * 3D files
	 */

	@Override
	public void savePrint3dFile(InputStream in, String fileName, Long id) throws IOException {
		Path modelsPath = getHWItemPrint3dPath(id);
		Path modelPath = modelsPath.resolve(fileName);
		if (!modelPath.normalize().startsWith(modelsPath))
			throw new IllegalArgumentException(ILLEGAL_PATH_PRINT_3D_ERR);
		Files.copy(in, modelPath, StandardCopyOption.REPLACE_EXISTING);
		fileSystemService.grantPermissions(modelPath);
	}

	@Override
	public List<HWItemFileTO> getHWItemPrint3dFiles(Long id) {
		Path modelsPath;
		try {
			modelsPath = getHWItemPrint3dPath(id);
			List<HWItemFileTO> list = new ArrayList<>();
			try (Stream<Path> stream = Files.list(modelsPath)) {
				stream.forEach(p -> list.add(mapPathToItem(p)));
			}
			return list;
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat p??ehled 3d model?? HW polo??ky", e);
		}
	}

	@Override
	public long getHWItemPrint3dFilesCount(Long id) {
		Path modelsPath;
		try {
			modelsPath = getHWItemPrint3dPath(id);
			try (Stream<Path> stream = Files.list(modelsPath)) {
				return stream.count();
			}
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat p??ehled 3d model?? HW polo??ky", e);
		}
	}

	@Override
	public Path getHWItemPrint3dFilePath(Long id, String name) {
		Path models;
		try {
			models = getHWItemPrint3dPath(id);
			Path model = models.resolve(name);
			if (!model.normalize().startsWith(models))
				throw new IllegalArgumentException(ILLEGAL_PATH_PRINT_3D_ERR);
			return model;
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat soubor 3d modelu HW polo??ky", e);
		}
	}

	@Override
	public InputStream getHWItemPrint3dFileInputStream(Long id, String name) {
		try {
			return Files.newInputStream(getHWItemPrint3dFilePath(id, name));
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat soubor 3d modelu HW polo??ky", e);
		}
	}

	@Override
	public boolean deleteHWItemPrint3dFile(Long id, String name) {
		Path models;
		try {
			models = getHWItemPrint3dPath(id);
			Path model = models.resolve(name);
			if (!model.normalize().startsWith(models))
				throw new IllegalArgumentException(ILLEGAL_PATH_PRINT_3D_ERR);
			return Files.deleteIfExists(model);
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se smazat soubor 3d modelu HW polo??ky.", e);
		}
	}

	/*
	 * Documents
	 */

	@Override
	public void saveDocumentsFile(InputStream in, String fileName, Long id) throws IOException {
		Path docsPath = getHWItemDocumentsPath(id);
		Path docPath = docsPath.resolve(fileName);
		if (!docPath.normalize().startsWith(docsPath))
			throw new IllegalArgumentException(ILLEGAL_PATH_DOCS_ERR);
		Files.copy(in, docPath, StandardCopyOption.REPLACE_EXISTING);
		fileSystemService.grantPermissions(docPath);
	}

	@Override
	public List<HWItemFileTO> getHWItemDocumentsFiles(Long id) {
		Path docsPath;
		try {
			docsPath = getHWItemDocumentsPath(id);
			List<HWItemFileTO> list = new ArrayList<>();
			try (Stream<Path> stream = Files.list(docsPath)) {
				stream.forEach(p -> list.add(mapPathToItem(p)));
			}
			return list;
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat p??ehled dokumentac?? HW polo??ky", e);
		}
	}

	@Override
	public long getHWItemDocumentsFilesCount(Long id) {
		Path docsPath;
		try {
			docsPath = getHWItemDocumentsPath(id);
			try (Stream<Path> stream = Files.list(docsPath)) {
				return stream.count();
			}
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat p??ehled dokumentac?? HW polo??ky", e);
		}
	}

	@Override
	public Path getHWItemDocumentsFilePath(Long id, String name) {
		Path docs;
		try {
			docs = getHWItemDocumentsPath(id);
			Path doc = docs.resolve(name);
			if (!doc.normalize().startsWith(docs))
				throw new IllegalArgumentException(ILLEGAL_PATH_DOCS_ERR);
			return doc;
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat soubor dokumentace HW polo??ky", e);
		}
	}

	@Override
	public InputStream getHWItemDocumentsFileInputStream(Long id, String name) {
		try {
			return Files.newInputStream(getHWItemDocumentsFilePath(id, name));
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat soubor dokumentace HW polo??ky", e);
		}
	}

	@Override
	public boolean deleteHWItemDocumentsFile(Long id, String name) {
		Path docs;
		try {
			docs = getHWItemDocumentsPath(id);
			Path doc = docs.resolve(name);
			if (!doc.normalize().startsWith(docs))
				throw new IllegalArgumentException(ILLEGAL_PATH_DOCS_ERR);
			return Files.deleteIfExists(doc);
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se smazat soubor dokumentace HW polo??ky.", e);
		}
	}

	/*
	 * Icons
	 */

	@Override
	public OutputStream createHWItemIconOutputStream(String filename, Long id) {
		String[] parts = filename.split("\\.");
		String extension = parts.length >= 1 ? parts[parts.length - 1] : "";

		Path hwItemDir;
		try {
			hwItemDir = getHWPath(id);
			Path file = hwItemDir.resolve("icon." + extension);
			return Files.newOutputStream(file);
		} catch (IOException e) {
			throw new GrassException("Nezda??ila se p????prava pro ulo??en?? ikony HW polo??ky.", e);
		}
	}

	@Override
	public Path getHWItemIconFile(Long id) throws IOException {
		Path hwPath = getHWPath(id);
		if (!Files.exists(hwPath))
			return null;
		try (Stream<Path> stream = Files.list(hwPath)) {
			return stream.filter(p -> p.getFileName().toString().matches("icon\\.[^\\.]*")).findFirst().orElse(null);
		}
	}

	@Override
	public InputStream getHWItemIconFileInputStream(Long id) {
		Path path;
		try {
			path = getHWItemIconFile(id);
			return path != null ? Files.newInputStream(path) : null;
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se z??skat ikonu HW polo??ky.", e);
		}
	}

	@Override
	public boolean deleteHWItemIconFile(Long id) {
		Path image;
		try {
			image = getHWItemIconFile(id);
			if (image != null)
				return Files.deleteIfExists(image);
			return false;
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se smazat ikonu HW polo??ky.", e);
		}
	}

	/*
	 * Item types
	 */

	@Override
	public Long saveHWType(HWItemTypeTO hwItemTypeTO) {
		HWItemType type = hwMapper.mapHWItem(hwItemTypeTO);
		type = hwItemTypeRepository.save(type);
		return type.getId();
	}

	@Override
	public Set<HWItemTypeTO> getAllHWTypes() {
		List<HWItemType> hwItemTypes = hwItemTypeRepository.findListOrderByName();
		return hwMapper.mapHWItemTypes(hwItemTypes);
	}

	@Override
	public HWItemTypeTO getHWItemType(Long fixTypeId) {
		return hwMapper.mapHWItemType(hwItemTypeRepository.findById(fixTypeId).orElse(null));
	}

	@Override
	public void deleteHWItemType(Long id) {
		HWItemType itemType = hwItemTypeRepository.findById(id).orElse(null);
		List<HWItem> items = hwItemRepository.findByTypesId(itemType.getId());
		for (HWItem item : items) {
			item.getTypes().remove(itemType);
			hwItemRepository.save(item);
		}
		hwItemTypeRepository.delete(itemType);
	}

	@Override
	public Set<HWItemTypeTO> getHWItemTypes(HWItemTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
		return hwMapper.mapHWItemTypes(hwItemTypeRepository.getHWItemTypes(filter, offset, limit, order));
	}

	@Override
	public int countHWItemTypes(HWItemTypeTO filter) {
		return (int) hwItemTypeRepository.countHWItemTypes(filter);
	}

	/*
	 * Items
	 */

	@Override
	public Long copyHWItem(Long origId) {
		HWItemTO item = getHWItem(origId);
		// jde o novou polo??ku, tak??e pr??zdn?? id, ????dn?? z??znamy
		item.setId(null);
		item.setServiceNotes(null);
		item.setUsedIn(null);
		item.setUsedInName(null);
		// zkop??ruj p????lohy

		Long copyId = saveHWItem(item);

		try {
			Path origPath = getHWPath(origId);
			if (Files.exists(origPath)) {
				Path copyPath = getHWPath(copyId);
				Files.walkFileTree(origPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
							throws IOException {
						fileSystemService.createDirectoriesWithPerms(copyPath.resolve(origPath.relativize(dir)));
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
							throws IOException {
						Path target = copyPath.resolve(origPath.relativize(file));
						Files.copy(file, target);
						fileSystemService.grantPermissions(target);
						return FileVisitResult.CONTINUE;
					}
				});
			}
		} catch (IOException e) {
			throw new GrassException("Nezda??ilo se vytvo??it kopii soubor?? HW polo??ky", e);
		}

		return copyId;
	}

	@Override
	public Long saveHWItem(HWItemTO hwItemDTO) {
		HWItem item;
		if (hwItemDTO.getId() == null)
			item = new HWItem();
		else
			item = hwItemRepository.findById(hwItemDTO.getId()).orElse(null);
		item.setName(hwItemDTO.getName());
		item.setPurchaseDate(DateUtils.toDate(hwItemDTO.getPurchaseDate()));
		item.setPrice(hwItemDTO.getPrice());
		item.setState(hwItemDTO.getState());
		item.setDescription(hwItemDTO.getDescription());
		item.setSupervizedFor(hwItemDTO.getSupervizedFor());
		item.setPublicItem(hwItemDTO.getPublicItem());
		if (hwItemDTO.getUsedIn() != null) {
			HWItem usedIn = hwItemRepository.findById(hwItemDTO.getUsedIn().getId()).orElse(null);
			item.setUsedIn(usedIn);
		} else {
			item.setUsedIn(null);
		}
		item.setWarrantyYears(hwItemDTO.getWarrantyYears());
		if (hwItemDTO.getTypes() != null) {
			item.setTypes(new HashSet<HWItemType>());
			for (String typeName : hwItemDTO.getTypes()) {
				HWItemType type = hwItemTypeRepository.findByName(typeName);
				if (type == null) {
					type = new HWItemType(typeName);
					type = hwItemTypeRepository.save(type);
				}
				item.getTypes().add(type);
			}
		}
		return hwItemRepository.save(item).getId();
	}

	@Override
	public int countHWItems(HWFilterTO filter) {
		return (int) hwItemRepository.countHWItems(filter);
	}

	@Override
	public List<HWItemOverviewTO> getAllHWItems() {
		List<HWItem> hwItemTypes = hwItemRepository.findAll();
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public List<HWItemOverviewTO> getHWItems(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
		return hwMapper.mapHWItems(hwItemRepository.getHWItems(filter, offset, limit, order));
	}

	@Override
	public List<HWItemOverviewTO> getHWItemsByTypes(Collection<String> types) {
		List<HWItem> hwItemTypes = hwItemRepository.getHWItemsByTypes(types);
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public HWItemTO getHWItem(Long itemId) {
		return hwMapper.mapHWItem(hwItemRepository.findById(itemId).orElse(null));
	}

	@Override
	public HWItemOverviewTO getHWOverviewItem(Long itemId) {
		return hwMapper.mapHWItemOverview(hwItemRepository.findById(itemId).orElse(null));
	}

	@Override
	public List<HWItemOverviewTO> getAllParts(Long usedInItemId) {
		return hwMapper.mapHWItems(hwItemRepository.findByUsedInId(usedInItemId));
	}

	@Override
	public List<HWItemOverviewTO> getHWItemsAvailableForPart(Long itemId) {
		List<HWItem> hwItemTypes = hwItemRepository.findAllExcept(itemId);
		return hwMapper.mapHWItems(hwItemTypes);
	}

	@Override
	public void deleteHWItem(Long id) {
		HWItem item = hwItemRepository.findById(id).orElse(null);
		for (HWServiceNote note : item.getServiceNotes())
			serviceNoteRepository.delete(note);

		item.setServiceNotes(null);
		hwItemRepository.save(item);

		for (HWItem targetItem : hwItemRepository.findByUsedInId(item.getId())) {
			targetItem.setUsedIn(null);
			hwItemRepository.save(targetItem);
		}

		hwItemRepository.deleteById(item.getId());

		hwItemTypeRepository.cleanOrphansName();

		Path hwPath = getHWPath(item.getId());
		try (Stream<Path> s = Files.walk(hwPath)) {
			s.sorted(Comparator.reverseOrder()).forEach(p -> {
				try {
					Files.delete(p);
				} catch (IOException e) {
					logger.error("Chyba p??i maz??n?? souboru HW polo??ky " + id + "] (" + item.getName() + ")", e);
				}
			});
		} catch (Exception e) {
			logger.warn("Nezda??ilo se smazat adres???? p????loh k HW polo??ce [" + id + "] (" + item.getName() + ")");
		}
	}

	/*
	 * Service notes
	 */

	/**
	 * Vygeneruje {@link HWServiceNote} o p??id??n??/odebr??n?? HW, ulo???? a p??id?? k
	 * c??lov??mu HW
	 * 
	 * @param triggerItem
	 *            HW kter?? je p??id??n/odebr??n
	 * @param triggerNote
	 *            {@link HWServiceNote}, kter?? ud??lost spustil
	 * @param added
	 *            {@code true} pokud byl HW p??id??n
	 */
	private void saveHWPartMoveServiceNote(HWItem triggerItem, HWServiceNote triggerNote, boolean added) {
		HWItem targetItem = hwItemRepository.findById(triggerItem.getUsedIn().getId()).orElse(null);
		HWServiceNote removeNote = new HWServiceNote();
		removeNote.setDate(triggerNote.getDate());

		StringBuilder builder = new StringBuilder();
		builder.append(added ? "Byl p??id??n:" : "Byl odebr??n:").append("\n").append(triggerItem.getName()).append("\n\n")
				.append("D??vod:").append("\n").append(triggerNote.getDescription());
		removeNote.setDescription(builder.toString());
		removeNote.setState(targetItem.getState());
		removeNote.setUsage(targetItem.getUsedIn() == null ? "" : targetItem.getUsedIn().getName());
		HWServiceNote note = serviceNoteRepository.save(removeNote);
		targetItem.getServiceNotes().add(note);
		hwItemRepository.save(targetItem);
	}

	@Override
	public void addServiceNote(HWServiceNoteTO serviceNoteDTO, Long id) {
		HWItem item = hwItemRepository.findById(id).orElse(null);
		HWServiceNote serviceNote = new HWServiceNote();
		serviceNote.setDate(DateUtils.toDate(serviceNoteDTO.getDate()));
		serviceNote.setDescription(serviceNoteDTO.getDescription());
		serviceNote.setState(serviceNoteDTO.getState());
		serviceNote.setUsage(serviceNoteDTO.getUsedInName());
		serviceNote = serviceNoteRepository.save(serviceNote);
		serviceNoteDTO.setId(serviceNote.getId());

		if (item.getServiceNotes() == null)
			item.setServiceNotes(new ArrayList<HWServiceNote>());
		item.getServiceNotes().add(serviceNote);
		item.setState(serviceNote.getState());

		HWItem oldTarget = item.getUsedIn();

		// HW je n??kde sou????st??
		if (serviceNoteDTO.getUsedInId() != null) {

			// c??lov?? HW, kde je nyn?? HW sou????st??
			HWItem targetItem = hwItemRepository.findById(serviceNoteDTO.getUsedInId()).orElse(null);

			// p??edt??m nebyl nikde sou????st??
			if (oldTarget == null) {
				item.setUsedIn(targetItem);
				saveHWPartMoveServiceNote(item, serviceNote, true);
			} else if (oldTarget.getId() != serviceNoteDTO.getUsedInId()) {
				// ji?? p??edt??m byl sou????st??, ale nyn?? je jinde
				saveHWPartMoveServiceNote(item, serviceNote, false);
				item.setUsedIn(targetItem);
				saveHWPartMoveServiceNote(item, serviceNote, true);
			} else {
				// nic se nezm??nilo - HW je st??le sou????st?? stejn??ho HW
			}

		} else { // HW nen?? nikde sou????st??

			// u?? p??edt??m nebyl nikde sou????st??
			if (oldTarget == null) {
				// nic se nezm??nilo - HW st??le nen?? nikde evidov??n jako sou????st
			} else {
				// p??edt??m n??kde byl
				saveHWPartMoveServiceNote(item, serviceNote, false);
				item.setUsedIn(null);
			}
		}

		hwItemRepository.save(item);
	}

	@Override
	public void modifyServiceNote(HWServiceNoteTO serviceNoteDTO) {
		HWServiceNote serviceNote = serviceNoteRepository.findById(serviceNoteDTO.getId()).orElse(null);
		serviceNote.setDate(DateUtils.toDate(serviceNoteDTO.getDate()));
		serviceNote.setDescription(serviceNoteDTO.getDescription());
		serviceNote.setState(serviceNoteDTO.getState());
		serviceNote.setUsage(serviceNoteDTO.getUsedInName());
		serviceNoteRepository.save(serviceNote);
	}

	@Override
	public void deleteServiceNote(HWServiceNoteTO serviceNoteDTO, Long id) {
		HWItem item = hwItemRepository.findById(id).orElse(null);
		HWServiceNote serviceNote = serviceNoteRepository.findById(serviceNoteDTO.getId()).orElse(null);
		item.getServiceNotes().remove(serviceNote);
		hwItemRepository.save(item);
		serviceNoteRepository.delete(serviceNote);
	}

}
