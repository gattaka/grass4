package cz.gattserver.grass.pg.service.impl;

import cz.gattserver.common.util.ReferenceHolder;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.exception.UnauthorizedAccessException;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.modules.PGModule;
import cz.gattserver.grass.pg.config.PGConfiguration;
import cz.gattserver.grass.pg.events.impl.*;
import cz.gattserver.grass.pg.interfaces.*;
import cz.gattserver.grass.pg.model.domain.Photogallery;
import cz.gattserver.grass.pg.model.repositories.PhotogalleryRepository;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.pg.util.DecodeAndCaptureFrames;
import cz.gattserver.grass.pg.util.PGUtils;
import cz.gattserver.grass.pg.util.PhotogalleryMapper;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.core.services.SecurityService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

@Transactional
@Service
public class PGServiceImpl implements PGService {

	private static Logger logger = LoggerFactory.getLogger(PGServiceImpl.class);

	@Autowired
	private ContentNodeService contentNodeFacade;

	@Autowired
	private PhotogalleryMapper photogalleriesMapper;

	@Autowired
	private SecurityService securityFacade;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private PhotogalleryRepository photogalleryRepository;

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private EventBus eventBus;

	private enum GalleryFileType {
		MAIN_FILE, PREVIEW, SLIDESHOW, MINIATURE,
	}

	@Override
	public PGConfiguration loadConfiguration() {
		PGConfiguration configuration = new PGConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	@Override
	public void storeConfiguration(PGConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

	private void deleteFileRecursively(Path file) throws IOException {
		if (Files.isDirectory(file)) {
			try (Stream<Path> stream = Files.list(file)) {
				Iterator<Path> it = stream.iterator();
				while (it.hasNext())
					deleteFileRecursively(it.next());
			}
		}
		Files.delete(file);
	}

	@Override
	public boolean deletePhotogallery(long photogalleryId) {
		String path = photogalleryRepository.findPhotogalleryPathById(photogalleryId);
		Path galleryDir = getGalleryPath(path);

		photogalleryRepository.deleteById(photogalleryId);
		contentNodeFacade.deleteByContentId(PGModule.ID, photogalleryId);

		// musí se řešit return stavem, protože exception by způsobilo rollback
		// transakce, což nechci
		try {
			deleteFileRecursively(galleryDir);
			return true;
		} catch (Exception e) {
			logger.error("Nezdařilo se smazat některé soubory galerie: " + photogalleryId, e);
			return false;
		}
	}

	private void createVideoMinature(Path file, Path outputFile) {
		String videoName = file.getFileName().toString();
		String previewName = outputFile.getFileName().toString();
		logger.info("Bylo nalezeno video {}", videoName);
		logger.info("Bylo zahájeno zpracování náhledu videa {}", videoName);
		try {
			BufferedImage image = new DecodeAndCaptureFrames().decodeAndCaptureFrames(file);
			logger.info("Zpracování náhledu videa {} byla úspěšně dokončeno", videoName);
			PGUtils.resizeVideoPreviewImage(image, outputFile);
			logger.info("Náhled videa {} byl úspěšně uložen", previewName);
		} catch (Exception e) {
			PGUtils.createErrorPreview(outputFile);
			logger.info("Chybový náhled videa {} byl úspěšně uložen", previewName);
		}
	}

	private void createImageMinature(Path file, Path outputFile) {
		String imageName = outputFile.getFileName().toString();
		try {
			PGUtils.resizeImage(file, outputFile);
			logger.info("Náhled obrázku {} byl úspěšně uložen", imageName);
		} catch (Exception e) {
			logger.error("Vytváření náhledu obrázku {} se nezdařilo", imageName, e);
		}
	}

	private void processMiniatureImages(Photogallery photogallery, boolean reprocess) throws IOException {
		PGConfiguration configuration = loadConfiguration();
		String miniaturesDir = configuration.getMiniaturesDir();
		String previewsDir = configuration.getPreviewsDir();
		Path galleryDir = getGalleryPath(photogallery.getPhotogalleryPath());

		int total = 0;
		try (Stream<Path> stream = Files.list(galleryDir)) {
			total = (int) stream.count();
		}

		int progress = 1;

		Path miniDirFile = galleryDir.resolve(miniaturesDir);
		Path prevDirFile = galleryDir.resolve(previewsDir);

		if (reprocess) {
			if (Files.exists(miniDirFile))
				deleteFileRecursively(miniDirFile);
			if (Files.exists(prevDirFile))
				deleteFileRecursively(prevDirFile);
		}

		if (!Files.exists(miniDirFile))
			fileSystemService.createDirectoriesWithPerms(miniDirFile);

		if (!Files.exists(prevDirFile))
			fileSystemService.createDirectoriesWithPerms(prevDirFile);

		try (Stream<Path> stream = Files.list(galleryDir).sorted(getComparator())) {
			Iterator<Path> it = stream.iterator();
			while (it.hasNext()) {
				Path file = it.next();

				// pokud bych miniaturizoval adresář přeskoč
				if (Files.isDirectory(file))
					continue;

				eventBus.publish(new PGProcessProgressEvent("Zpracování miniatur " + progress + "/" + total));
				progress++;

				boolean videoExt = PGUtils.isVideo(file);
				boolean rasterImageExt = PGUtils.isRasterImage(file);
				boolean vectorImageExt = PGUtils.isVectorImage(file);

				if (videoExt) {
					Path outputFile = prevDirFile.resolve(file.getFileName().toString() + ".png");
					if (!Files.exists(outputFile))
						createVideoMinature(file, outputFile);
				} else if (rasterImageExt) {
					Path outputFile = miniDirFile.resolve(file.getFileName().toString());
					if (!Files.exists(outputFile))
						createImageMinature(file, outputFile);
				} else if (vectorImageExt) {
					Path outputFile = miniDirFile.resolve(file.getFileName().toString() + ".png");
					if (!Files.exists(outputFile))
						createImageMinature(file, outputFile);
				}
			}
		}
	}

	private void processSlideshowImages(Photogallery photogallery, boolean reprocess) throws IOException {
		PGConfiguration configuration = loadConfiguration();
		String slideshowDir = configuration.getSlideshowDir();
		Path galleryDir = getGalleryPath(photogallery.getPhotogalleryPath());

		int total = 0;
		try (Stream<Path> stream = Files.list(galleryDir)) {
			total = (int) stream.count();
		}

		int progress = 1;

		Path slideshowDirFile = galleryDir.resolve(slideshowDir);

		if (reprocess && Files.exists(slideshowDirFile))
			deleteFileRecursively(slideshowDirFile);

		if (!Files.exists(slideshowDirFile))
			Files.createDirectories(slideshowDirFile);

		try (Stream<Path> stream = Files.list(galleryDir).sorted(getComparator())) {
			Iterator<Path> it = stream.iterator();
			while (it.hasNext()) {
				Path file = it.next();
				Path outputFile = slideshowDirFile.resolve(file.getFileName().toString());

				if (Files.exists(outputFile) || Files.isDirectory(file) || !PGUtils.isRasterImage(file)
						|| PGUtils.isVectorImage(file))
					continue;

				eventBus.publish(new PGProcessProgressEvent("Zpracování slideshow " + progress + "/" + total));
				progress++;

				// vytvoř slideshow verzi
				BufferedImage image = PGUtils.getImageFromFile(file);
				if (image.getWidth() > PGUtils.SLIDESHOW_WIDTH || image.getHeight() > PGUtils.SLIDESHOW_HEIGHT) {
					try {
						PGUtils.resizeImage(file, outputFile, PGUtils.SLIDESHOW_WIDTH, PGUtils.SLIDESHOW_HEIGHT);
					} catch (Exception e) {
						logger.error("Při zpracování slideshow pro '{}' došlo k chybě", file.getFileName().toString(),
								e);
					}
				}
			}
		}
	}

	@Override
	@Async
	@Transactional(propagation = Propagation.NEVER)
	public void modifyPhotogallery(
			UUID operationId, long photogalleryId, PhotogalleryPayloadTO payloadTO,
			LocalDateTime date) {
		innerSavePhotogallery(operationId, payloadTO, photogalleryId, null, null, date);
	}

	@Override
	@Async
	@Transactional(propagation = Propagation.NEVER)
	public void savePhotogallery(
			UUID operationId, PhotogalleryPayloadTO payloadTO, long nodeId, long authorId,
			LocalDateTime date) {
		innerSavePhotogallery(operationId, payloadTO, null, nodeId, authorId, date);
	}

	private void publishPGProcessFailure(UUID operationId) {
		eventBus.publish(new PGProcessResultEvent(operationId, false, "Nezdařilo se uložit galerii"));
	}

	@Transactional(propagation = Propagation.REQUIRED)
	protected Photogallery transactionSavePhotogallery(
			UUID operationId, String galleryDir,
			PhotogalleryPayloadTO payloadTO, Long existingId, Long nodeId, Long authorId, LocalDateTime date) {
		logger.info("modifyPhotogallery thread: " + Thread.currentThread().threadId());

		Photogallery photogallery = existingId == null ? new Photogallery()
				: photogalleryRepository.findById(existingId).orElse(null);

		// nasetuj do ní vše potřebné
		photogallery.setPhotogalleryPath(galleryDir);

		// ulož ho a nasetuj jeho id
		photogallery = photogalleryRepository.save(photogallery);
		if (photogallery == null) {
			publishPGProcessFailure(operationId);
			return null;
		}

		if (existingId == null) {
			// vytvoř odpovídající content node
			Long contentNodeId = contentNodeFacade.save(PGModule.ID, photogallery.getId(), payloadTO.getName(),
					payloadTO.getTags(), payloadTO.isPublicated(), nodeId, authorId, false, date, null);

			// ulož do článku referenci na jeho contentnode
			ContentNode contentNode = new ContentNode();
			contentNode.setId(contentNodeId);
			photogallery.setContentNode(contentNode);
			if (photogalleryRepository.save(photogallery) == null) {
				publishPGProcessFailure(operationId);
				return null;
			}
		} else {
			contentNodeFacade.modify(photogallery.getContentNode().getId(), payloadTO.getName(), payloadTO.getTags(),
					payloadTO.isPublicated(), date);
		}

		eventBus.publish(new PGProcessProgressEvent("Uložení obsahu galerie"));

		return photogallery;
	}

	@Transactional(propagation = Propagation.NEVER)
	protected void innerSavePhotogallery(
			UUID operationId, PhotogalleryPayloadTO payloadTO, Long existingId, Long nodeId,
			Long authorId, LocalDateTime date) {
		String galleryDir = payloadTO.getGalleryDir();
		Path galleryPath = getGalleryPath(galleryDir);
		try (Stream<Path> stream = Files.list(galleryPath).sorted(getComparator())) {
			// Počet kroků = miniatury + detaily + uložení
			int total = (int) stream.count();
			eventBus.publish(new PGProcessStartEvent(2 * total + 1));

			Photogallery photogallery = transactionSavePhotogallery(operationId, galleryDir, payloadTO, existingId,
					nodeId, authorId, date);

			// vytvoř miniatury
			processMiniatureImages(photogallery, payloadTO.isReprocess());

			// vytvoř detaily
			processSlideshowImages(photogallery, payloadTO.isReprocess());

			eventBus.publish(new PGProcessResultEvent(operationId, photogallery.getId()));
		} catch (Exception e) {
			publishPGProcessFailure(operationId);
			logger.error("Nezdařilo se uložit galerii", e);
			return;
		}
	}

	@Override
	public String createGalleryDir() throws IOException {
		PGConfiguration configuration = loadConfiguration();
		String dirRoot = configuration.getRootDir();
		Path dirRootFile = fileSystemService.getFileSystem().getPath(dirRoot);
		long systime = System.currentTimeMillis();
		Path tmpDirFile = dirRootFile.resolve("pgGal_" + systime);
		fileSystemService.createDirectoriesWithPerms(tmpDirFile);
		return tmpDirFile.getFileName().toString();
	}

	@Override
	public PhotogalleryTO getPhotogalleryForDetail(Long id) {
		Validate.notNull(id, "Id galerie nesmí být null");
		Photogallery photogallery = photogalleryRepository.findById(id).orElse(null);
		if (photogallery == null)
			return null;
		return photogalleriesMapper.mapPhotogalleryForDetail(photogallery);
	}

	/**
	 * Získá {@link Path} dle jména adresáře galerie
	 *
	 * @param galleryDir jméno adresáře galerie
	 * @return {@link Path} objekt galerie
	 * @throws IllegalStateException    pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *                                  modulu PG
	 * @throws IllegalArgumentException pokud předaný adresář podtéká kořen modulu PG
	 */
	private Path getGalleryPath(String galleryDir) {
		PGConfiguration configuration = loadConfiguration();
		String rootDir = configuration.getRootDir();
		Path rootPath = fileSystemService.getFileSystem().getPath(rootDir);
		if (!Files.exists(rootPath)) {
			IllegalStateException ise = new IllegalStateException("Kořenový adresář PG modulu musí existovat");
			logger.error("Nezdařilo se získat kořenový adresář galerií", ise);
			throw ise;
		}
		rootPath = rootPath.normalize();
		Path galleryPath = rootPath.resolve(galleryDir);
		if (!galleryPath.normalize().startsWith(rootPath)) {
			IllegalArgumentException ise = new IllegalArgumentException("Podtečení kořenového adresáře galerií");
			logger.error("Nezdařilo se získat kořenový adresář galerií", ise);
			throw ise;
		}
		return galleryPath;
	}

	/**
	 * Pokusí se smazat soubor z galerie
	 *
	 * @param file       jméno souboru
	 * @param galleryDir adresář galerie
	 * @param fileType   typ souboru
	 * @return <code>true</code>, pokud se odstranění zdaří, jinak
	 * <code>false</code>
	 */
	private boolean tryDeleteGalleryFile(String file, Path galleryDir, GalleryFileType fileType) {
		Path subFile = null;
		switch (fileType) {
			case MINIATURE:
				if (file.endsWith(".svg"))
					file += ".png";
				subFile = galleryDir.resolve(loadConfiguration().getMiniaturesDir()).resolve(file);
				break;
			case PREVIEW:
				subFile = galleryDir.resolve(loadConfiguration().getPreviewsDir()).resolve(file + ".png");
				break;
			case SLIDESHOW:
				subFile = galleryDir.resolve(loadConfiguration().getSlideshowDir()).resolve(file);
				break;
			case MAIN_FILE:
			default:
				subFile = galleryDir.resolve(file);
				break;
		}
		if (Files.exists(subFile)) {
			try {
				Files.delete(subFile);
				return true;
			} catch (Exception e) {
				logger.error("Nezdařilo se smazat soubor {}", subFile, e);
				return false;
			}
		} else {
			logger.info("Nezdařilo se najít soubor {}", subFile);
			return true;
		}
	}

	@Override
	public int countAllPhotogalleriesForREST(Long userId, String filter) {
		filter = QuerydslUtil.transformSimpleLikeFilter(filter);
		return userId != null ? photogalleryRepository.countByUserAccess(userId, filter)
				: photogalleryRepository.countByAnonAccess(filter);
	}

	@Override
	public List<PhotogalleryRESTOverviewTO> getAllPhotogalleriesForREST(
			Long userId, String filter,
			Pageable pageable) {
		filter = QuerydslUtil.transformSimpleLikeFilter(filter);
		return photogalleriesMapper.mapPhotogalleryForRESTOverviewCollection(
				userId != null ? photogalleryRepository.findByUserAccess(userId, filter, pageable)
						: photogalleryRepository.findByAnonAccess(filter, pageable));
	}

	@Override
	public PhotogalleryRESTOverviewTO getPhotogalleryByDirectory(String directory) {
		Photogallery gallery = photogalleryRepository.findByDirectory(directory);
		return photogalleriesMapper.mapPhotogalleryForRESTOverview(gallery);
	}

	@Override
	public PhotogalleryRESTTO getPhotogalleryForREST(Long id) throws UnauthorizedAccessException {
		Photogallery gallery = photogalleryRepository.findById(id).orElse(null);
		if (gallery == null)
			return null;

		UserInfoTO user = securityFacade.getCurrentUser();
		if (gallery.getContentNode().getPublicated() || user.isAdmin()
				|| gallery.getContentNode().getAuthor().getId().equals(user.getId())) {

			PGConfiguration configuration = loadConfiguration();
			Path file = fileSystemService.getFileSystem().getPath(configuration.getRootDir(),
					gallery.getPhotogalleryPath());
			if (Files.exists(file)) {
				Set<String> files = new HashSet<>();
				try (Stream<Path> stream = Files.list(file).sorted(getComparator())) {
					stream.filter(f -> !Files.isDirectory(f)).forEach(f -> files.add(f.getFileName().toString()));
				} catch (IOException e) {
					throw new IllegalStateException(
							"Nelze získat přehled souborů z '" + file.getFileName().toString() + "'");
				}
				return new PhotogalleryRESTTO(gallery.getId(), gallery.getContentNode().getName(),
						gallery.getContentNode().getCreationDate(), gallery.getContentNode().getLastModificationDate(),
						gallery.getContentNode().getAuthor().getName(), files);
			} else {
				return null;
			}
		}
		throw new UnauthorizedAccessException();
	}

	@Override
	public Path getPhotoForREST(Long id, String fileName, PhotoVersion photoVersion)
			throws UnauthorizedAccessException {
		Photogallery gallery = photogalleryRepository.findById(id).orElse(null);
		if (gallery == null)
			return null;

		UserInfoTO user = securityFacade.getCurrentUser();
		if (gallery.getContentNode().getPublicated() || user.isAdmin()
				|| gallery.getContentNode().getAuthor().getId().equals(user.getId())) {
			PGConfiguration configuration = loadConfiguration();
			Path rootPath = loadRootDirFromConfiguration(configuration);
			Path galleryPath = rootPath.resolve(gallery.getPhotogalleryPath());
			Path miniaturesPath = galleryPath.resolve(configuration.getMiniaturesDir());
			Path slideshowPath = galleryPath.resolve(configuration.getSlideshowDir());
			Path file;
			switch (photoVersion) {
				case MINI:
					file = miniaturesPath.resolve(fileName);
					break;
				case SLIDESHOW:
					file = slideshowPath.resolve(fileName);
					break;
				default:
				case FULL:
					file = galleryPath.resolve(fileName);
					break;
			}
			if (Files.exists(file)) {
				return file;
			} else {
				switch (photoVersion) {
					case FULL:
						// nenašel jsem ani plnou velikost? Problém!
						return null;
					case SLIDESHOW:
					case MINI:
						file = galleryPath.resolve(fileName);
						if (Files.exists(file))
							return file;
						break;
				}
			}
		}

		throw new UnauthorizedAccessException();
	}

	private Path loadRootDirFromConfiguration(PGConfiguration configuration) {
		String rootDir = configuration.getRootDir();
		Path rootPath = fileSystemService.getFileSystem().getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new GrassPageException(500, "Kořenový adresář PG modulu musí existovat");
		rootPath = rootPath.normalize();
		return rootPath;
	}

	@Async
	@Override
	public void zipGallery(String galleryDir) {
		Path galleryPath = getGalleryPath(galleryDir);

		logger.info("zipPhotogallery thread: " + Thread.currentThread().threadId());

		final ReferenceHolder<Integer> total = new ReferenceHolder<>();
		final ReferenceHolder<Integer> progress = new ReferenceHolder<>();

		try (Stream<Path> stream = Files.list(galleryPath)) {
			total.setValue((int) stream.count());
			eventBus.publish(new PGZipProcessStartEvent(total.getValue() + 1));
		} catch (Exception e) {
			String msg = "Nezdařilo se získat počet souborů ke komprimaci";
			eventBus.publish(new PGZipProcessResultEvent(msg, e));
			logger.error(msg, e);
			return;
		}

		progress.setValue(1);

		String zipFileName = "grassPGTmpFile-" + new Date().getTime() + "-" + galleryDir + ".zip";
		try {
			Path zipFile = fileSystemService.createTmpDir("grassPGTmpFolder").resolve(zipFileName);
			FileSystem zipFileSystem = null;
			try {
				zipFileSystem = fileSystemService.newZipFileSystem(zipFile, true);
				performZip(galleryPath, zipFileSystem, progress, total);
				logger.info("Zipování galerie dokončeno");
				// musí se zavřít před zasláním event, takže nelze použít try-with-resources
				zipFileSystem.close();
				eventBus.publish(new PGZipProcessResultEvent(zipFile));
			} catch (Exception e) {
				String msg = "Nezdařilo se vytvořit ZIP galerie";
				if (zipFileSystem != null)
					zipFileSystem.close();
				eventBus.publish(new PGZipProcessResultEvent(msg, e));
				logger.error(msg, e);
			}
		} catch (Exception e) {
			String msg = "Nezdařilo se vytvořit dočasný adresář pro ZIP galerie";
			eventBus.publish(new PGZipProcessResultEvent(msg, e));
			logger.error(msg, e);
		}
	}

	private void performZip(
			Path galleryPath, FileSystem zipFileSystem, ReferenceHolder<Integer> progress,
			ReferenceHolder<Integer> total) throws IOException {
		final Path root = zipFileSystem.getRootDirectories().iterator().next();
		try (Stream<Path> stream = Files.list(galleryPath)) {
			Iterator<Path> it = stream.iterator();
			while (it.hasNext()) {
				Path src = it.next();
				String msg;

				// Přidávám jenom soubory fotek a videí, miniatury/náhledy a
				// slideshow nechci
				if (!Files.isDirectory(src)) {
					Path dest = root.resolve(src.getFileName().toString());
					Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
					msg = "Přidávám '" + src.getFileName() + "' do ZIPu";
				} else {
					msg = "Ignoruji '" + src.getFileName();
				}
				msg += " " + progress.getValue() + "/" + total.getValue();
				logger.info(msg);
				eventBus.publish(new PGZipProcessProgressEvent(msg));
				progress.setValue(progress.getValue() + 1);
			}
		}
	}

	@Override
	public List<PhotogalleryViewItemTO> deleteFiles(Set<PhotogalleryViewItemTO> selected, String galleryDir) {
		Path galleryPath = getGalleryPath(galleryDir);
		List<PhotogalleryViewItemTO> removed = new ArrayList<>();
		for (PhotogalleryViewItemTO itemTO : selected) {
			deleteFile(itemTO, galleryPath);
			removed.add(itemTO);
		}
		return removed;
	}

	@Override
	public void deleteFile(PhotogalleryViewItemTO itemTO, String galleryDir) {
		Path galleryPath = getGalleryPath(galleryDir);
		deleteFile(itemTO, galleryPath);
	}

	public void deleteFile(PhotogalleryViewItemTO itemTO, Path galleryPath) {
		String file = itemTO.getName();
		tryDeleteGalleryFile(file, galleryPath, GalleryFileType.MINIATURE);
		tryDeleteGalleryFile(file, galleryPath, GalleryFileType.SLIDESHOW);
		tryDeleteGalleryFile(file, galleryPath, GalleryFileType.PREVIEW);
		tryDeleteGalleryFile(file, galleryPath, GalleryFileType.MAIN_FILE);
	}

	@Override
	public Path getFullImage(String galleryDir, String file) {
		Path galleryPath = getGalleryPath(galleryDir);
		Path filePath = galleryPath.resolve(file);
		if (!filePath.normalize().startsWith(galleryPath))
			throw new IllegalArgumentException("Podtečení adresáře galerie");
		return filePath;
	}

	@Override
	public void uploadFile(InputStream in, String fileName, String galleryDir) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		Path filePath = galleryPath.resolve(fileName);
		if (!filePath.normalize().startsWith(galleryPath))
			throw new IllegalArgumentException("Podtečení adresáře galerie");
		Files.copy(in, filePath);
		fileSystemService.grantPermissions(filePath);
	}

	@Override
	public List<PhotogalleryViewItemTO> getItems(String galleryDir) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		List<PhotogalleryViewItemTO> items = new ArrayList<>();
		try (Stream<Path> stream = Files.list(galleryPath).sorted(getComparator())) {
			stream.filter(file -> !Files.isDirectory(file)).forEach(file -> {
				PhotogalleryViewItemTO itemTO = new PhotogalleryViewItemTO();
				itemTO.setName(file.getFileName().toString());
				items.add(itemTO);
			});

		}
		return items;
	}

	@Override
	public int getViewItemsCount(String galleryDir) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		try (Stream<Path> stream = Files.list(galleryPath)) {
			return (int) stream.filter(file -> !Files.isDirectory(file)).count();
		}
	}

	private Comparator<Path> getComparator() {
		Comparator<Path> nameComparator = Comparator.comparing(p -> p.getFileName().toString());
		Comparator<Path> comparator = (p1, p2) -> {
			try {
				LocalDateTime ldt1 = Files.getLastModifiedTime(p1).toInstant().atZone(ZoneId.systemDefault())
						.toLocalDateTime();
				LocalDateTime ldt2 = Files.getLastModifiedTime(p2).toInstant().atZone(ZoneId.systemDefault())
						.toLocalDateTime();
				return ldt1.compareTo(ldt2);
			} catch (IOException e) {
				// nezdařilo se načíst datum ze soborů (to je už dost zvláštní,
				// ale budiž)
			}
			return nameComparator.compare(p1, p2);
		};
		return comparator;
	}

	private boolean filterOtherFiles(Path file) {
		return filterOtherFiles(file.getFileName().toString());
	}

	private boolean filterOtherFiles(String fileName) {
		String f = fileName.toLowerCase();
		return f.endsWith(".xcf") || f.endsWith(".otf") || f.endsWith(".ttf");
	}

	@Override
	public List<PhotogalleryViewItemTO> getViewItems(String galleryDir, int skip, int limit) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		PGConfiguration configuration = loadConfiguration();
		Path miniaturesDir = galleryPath.resolve(configuration.getMiniaturesDir());
		Path previewDir = galleryPath.resolve(configuration.getPreviewsDir());
		List<PhotogalleryViewItemTO> list = new ArrayList<>();

		try (Stream<Path> miniaturesStream = Files.list(miniaturesDir).sorted(getComparator());
			 Stream<Path> previewsStream = Files.list(previewDir).sorted(getComparator());
			 Stream<Path> otherStream =
					 Files.list(galleryPath).filter(this::filterOtherFiles)) {
			Stream.concat(miniaturesStream, Stream.concat(previewsStream, otherStream)).skip(skip).limit(limit).forEach(file -> {
				PhotogalleryViewItemTO itemTO = new PhotogalleryViewItemTO();
				String fileName = file.getFileName().toString();
				if (file.startsWith(previewDir)) {
					itemTO.setType(PhotogalleryItemType.VIDEO);
					// u videa je potřeba useknout příponu preview obrázku
					// '.png', aby zůstala původní video přípona
					itemTO.setName(fileName.substring(0, fileName.length() - 4));
				} else {
					itemTO.setType(PhotogalleryItemType.IMAGE);
					if (fileName.toLowerCase().endsWith(".svg.png")) {
						// u SVG je potřeba useknout příponu preview obrázku
						// '.png', aby zůstala původní video přípona
						itemTO.setName(fileName.substring(0, fileName.length() - 4));
					} else {
						itemTO.setName(fileName);
					}
				}
				itemTO.setFile(file);
				list.add(itemTO);
			});
		}
		return list;
	}

	@Override
	public PhotogalleryViewItemTO getSlideshowItem(String galleryDir, int index) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		PGConfiguration configuration = loadConfiguration();
		Path miniaturesDir = galleryPath.resolve(configuration.getMiniaturesDir());
		Path previewDir = galleryPath.resolve(configuration.getPreviewsDir());
		Path slideshowDir = galleryPath.resolve(configuration.getSlideshowDir());
		List<PhotogalleryViewItemTO> list = new ArrayList<>();
		try (Stream<Path> miniaturesStream = Files.list(miniaturesDir).sorted(getComparator());
			 Stream<Path> previewsStream = Files.list(previewDir).sorted(getComparator());
			 Stream<Path> otherStream =
					 Files.list(galleryPath).filter(this::filterOtherFiles)) {
			Stream.concat(miniaturesStream, Stream.concat(previewsStream, otherStream)).skip(index).limit(1).forEach(file -> {
				PhotogalleryViewItemTO itemTO = new PhotogalleryViewItemTO();
				String fileName = file.getFileName().toString();
				itemTO.setExifInfoTO(PGUtils.readMetadata(previewDir.getParent().resolve(fileName)));
				if (file.startsWith(previewDir)) {
					itemTO.setType(PhotogalleryItemType.VIDEO);
					// u videa je potřeba useknout příponu preview obrázku
					// '.png', aby zůstala původní video přípona
					itemTO.setName(fileName.substring(0, fileName.length() - 4));
					itemTO.setFile(galleryPath.resolve(itemTO.getName()));
				} else {
					itemTO.setType(PhotogalleryItemType.IMAGE);
					itemTO.setName(fileName);
					itemTO.setFile(slideshowDir.resolve(fileName));
					if (fileName.endsWith(".svg.png")) {
						// U vektorů je potřeba uříznout .png příponu, protože
						// originál je vektor, který se na slideshow dá rovnou
						// použít
						itemTO.setName(fileName.substring(0, fileName.length() - 4));
						itemTO.setFile(galleryPath.resolve(itemTO.getName()));
					} else if (!Files.exists(itemTO.getFile())) {
						// možná byl tak malý, že nebylo potřeba vytvářet
						// slideshow velikost a stačí použít přímo původní
						// soubor obrázku
						itemTO.setFile(galleryPath.resolve(fileName));
					}
				}
				list.add(itemTO);
			});
		}
		return list.get(0);
	}

	@Override
	public boolean checkGallery(String galleryDir) {
		Path galleryPath = getGalleryPath(galleryDir);
		PGConfiguration conf = loadConfiguration();
		return Files.exists(galleryPath) && (Files.exists(galleryPath.resolve(conf.getMiniaturesDir()))
				|| Files.exists(galleryPath.resolve(conf.getPreviewsDir())));
	}

	@Override
	public void deleteZipFile(Path zipFile) {
		try {
			Files.delete(zipFile);
		} catch (IOException e) {
			logger.error("Nezdařilo se smazat ZIP soubor {}", zipFile.getFileName().toString());
		}
	}

	@Override
	public void deleteDraftGallery(String galleryDir) throws IOException {
		Path galleryPath = getGalleryPath(galleryDir);
		Files.walkFileTree(galleryPath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				try {
					Files.delete(file);
				} catch (Exception e) {
					logger.error("Nezdařilo se smazat soubor zrušené rozpracované galerie {}",
							file.getFileName().toString(), e);
				}
				return FileVisitResult.CONTINUE;
			}
		});
		Files.delete(galleryPath);
	}
}
