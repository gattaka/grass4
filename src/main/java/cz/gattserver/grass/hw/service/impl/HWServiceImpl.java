package cz.gattserver.grass.hw.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import cz.gattserver.grass.core.exception.GrassException;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.hw.interfaces.*;
import cz.gattserver.grass.hw.model.*;
import cz.gattserver.grass.pg.util.PGUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.model.HWType;
import cz.gattserver.grass.hw.service.HWMapperService;
import cz.gattserver.grass.hw.service.HWService;

@Transactional
@Component
public class HWServiceImpl implements HWService {

    private static final Logger logger = LoggerFactory.getLogger(HWServiceImpl.class);

    private static final int MINIATURE_SIZE = 200;

    private static final String ILLEGAL_PATH_IMGS_ERR = "Podtečení adresáře grafických příloh";
    private static final String ILLEGAL_PATH_DOCS_ERR = "Podtečení adresáře dokumentací";
    private static final String ILLEGAL_PATH_PRINT_3D_ERR = "Podtečení adresáře 3d modelů";

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private HWItemRepository hwItemRepository;

    @Autowired
    private HWTypeRepository hwTypeRepository;

    @Autowired
    private HWItemRecordRepository hwItemRecordRepository;

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
     * Získá {@link Path} dle jména adresáře HW položky
     *
     * @param id id HW položky
     * @return {@link Path} adresář galerie
     * @throws IllegalStateException    pokud neexistuje kořenový adresář HW -- chyba nastavení
     *                                  modulu HW
     * @throws IllegalArgumentException pokud předaný adresář podtéká kořen modulu HW
     */
    private Path getHWPath(Long id) {
        Validate.notNull(id, "ID HW položky nesmí být null");
        HWConfiguration configuration = loadConfiguration();
        String rootDir = configuration.getRootDir();
        Path rootPath = fileSystemService.getFileSystem().getPath(rootDir);
        if (!Files.exists(rootPath)) throw new IllegalStateException("Kořenový adresář HW modulu musí existovat");
        rootPath = rootPath.normalize();
        Path hwPath = rootPath.resolve(String.valueOf(id));
        if (!hwPath.normalize().startsWith(rootPath))
            throw new IllegalArgumentException("Podtečení kořenového adresáře galerií");
        return hwPath;
    }

    private Path getHWItemDocumentsPath(Long id) throws IOException {
        HWConfiguration configuration = loadConfiguration();
        Path hwPath = getHWPath(id);
        Path file = hwPath.resolve(configuration.getDocumentsDir());
        if (!Files.exists(file)) fileSystemService.createDirectoriesWithPerms(file);
        return file;
    }

    private Path getHWItemPrint3dPath(Long id) throws IOException {
        HWConfiguration configuration = loadConfiguration();
        Path hwPath = getHWPath(id);
        Path file = hwPath.resolve(configuration.getPrint3dDir());
        if (!Files.exists(file)) fileSystemService.createDirectoriesWithPerms(file);
        return file;
    }

    private Path getHWItemImagesPath(Long id) throws IOException {
        HWConfiguration configuration = loadConfiguration();
        Path hwPath = getHWPath(id);
        Path file = hwPath.resolve(configuration.getImagesDir());
        if (!Files.exists(file)) fileSystemService.createDirectoriesWithPerms(file);
        return file;
    }

    private Path getHWItemImagesMiniPath(Long id) throws IOException {
        HWConfiguration configuration = loadConfiguration();
        Path hwPath = getHWPath(id);
        Path file = hwPath.resolve(configuration.getImagesMiniDir());
        if (!Files.exists(file)) fileSystemService.createDirectoriesWithPerms(file);
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

    private void createMiniature(Path imagePath, Path imageMiniPath) throws IOException {
        PGUtils.resizeImage(imagePath, imageMiniPath, MINIATURE_SIZE, MINIATURE_SIZE);
    }

    /*
     * Images
     */

    @Override
    public void saveImagesFile(InputStream in, String fileName, HWItemTO item) throws IOException {
        Path imagesPath = getHWItemImagesPath(item.getId());
        Path imagePath = imagesPath.resolve(fileName);
        if (!imagePath.normalize().startsWith(imagesPath)) throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
        Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
        fileSystemService.grantPermissions(imagePath);

        Path imagesMiniPath = getHWItemImagesMiniPath(item.getId());
        Path imageMiniPath = imagesMiniPath.resolve(fileName);

        String imageName = imageMiniPath.getFileName().toString();
        try {
            createMiniature(imagePath, imageMiniPath);
            logger.info("Náhled obrázku {} byl úspěšně uložen", imageName);
        } catch (Exception e) {
            logger.error("Vytváření náhledu obrázku {} se nezdařilo", imageName, e);
        }
    }

    @Override
    public List<HWItemFileTO> getHWItemImagesMiniFiles(Long id) {
        Path imagesPath;
        try {
            imagesPath = getHWItemImagesMiniPath(id);
            List<HWItemFileTO> list = new ArrayList<>();
            try (Stream<Path> stream = Files.list(imagesPath)) {
                stream.forEach(p -> list.add(mapPathToItem(p)));
            }
            return list;
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat přehled grafických příloh HW položky.", e);
        }
    }

    @Override
    public long getHWItemImagesMiniFilesCount(Long id) {
        Path imagesPath;
        try {
            imagesPath = getHWItemImagesMiniPath(id);
            try (Stream<Path> stream = Files.list(imagesPath)) {
                return stream.count();
            }
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat přehled grafických příloh HW položky.", e);
        }
    }

    @Override
    public Path getHWItemImagesFilePath(Long id, String name) {
        try {
            Path images = getHWItemImagesPath(id);
            Path image = images.resolve(name);
            if (!image.normalize().startsWith(images)) throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
            return image;
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat grafickou přílohu HW položky.", e);
        }
    }

    @Override
    public InputStream getHWItemImagesMiniFileInputStream(Long id, String name) {
        try {
            Path images = getHWItemImagesMiniPath(id);
            Path image = images.resolve(name);
            if (!image.normalize().startsWith(images)) throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
            return Files.newInputStream(image);
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat grafickou přílohu HW položky.", e);
        }
    }

    @Override
    public boolean deleteHWItemImagesFile(Long id, String fileName) {
        try {
            Path images = getHWItemImagesPath(id);
            Path image = images.resolve(fileName);
            if (!image.normalize().startsWith(images)) throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
            Files.deleteIfExists(image);

            Path imagesMiniPath = getHWItemImagesMiniPath(id);
            Path imageMiniPath = imagesMiniPath.resolve(fileName);
            Files.deleteIfExists(imageMiniPath);

            return true;
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se smazat grafickou přílohu HW položky.", e);
        }
    }

    @Override
    public void processMiniatures() {
        List<Long> ids = hwItemRepository.findAllIds();
        for (Long id : ids) {
            // icon
            try {
                Path iconPath = getHWItemIconFile(id);
                if (iconPath != null) {
                    Path iconMiniPath = iconPath.getParent()
                            .resolve(iconPath.getFileName().toString().replace("icon", "icon_mini"));
                    createMiniature(iconPath, iconMiniPath);
                }
            } catch (Exception e) {
                throw new GrassException("Nezdařilo se vytvořit miniaturu ikony HW položky " + id + ".", e);
            }
            // images
            try {
                Path imagesPath = getHWItemImagesPath(id);
                if (imagesPath != null) {
                    Path imagesMiniPath = getHWItemImagesMiniPath(id);
                    try (Stream<Path> s = Files.walk(imagesPath)) {
                        s.forEach(p -> {
                            if (Files.isDirectory(p)) return;
                            String imageName = p.getFileName().toString();
                            Path imageMiniPath = imagesMiniPath.resolve(imageName);
                            try {
                                createMiniature(p, imageMiniPath);
                            } catch (Exception e) {
                                throw new GrassException(
                                        "Nezdařilo se vytvořit miniaturu grafické přílohu " + imageName +
                                                " HW položky " + id + ".", e);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                throw new GrassException("Nezdařilo se vytvořit miniatury grafických přílohu HW položky " + id + ".",
                        e);
            }
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
            throw new GrassException("Nezdařilo se získat přehled 3d modelů HW položky", e);
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
            throw new GrassException("Nezdařilo se získat přehled 3d modelů HW položky", e);
        }
    }

    @Override
    public Path getHWItemPrint3dFilePath(Long id, String name) {
        Path models;
        try {
            models = getHWItemPrint3dPath(id);
            Path model = models.resolve(name);
            if (!model.normalize().startsWith(models)) throw new IllegalArgumentException(ILLEGAL_PATH_PRINT_3D_ERR);
            return model;
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat soubor 3d modelu HW položky", e);
        }
    }

    @Override
    public InputStream getHWItemPrint3dFileInputStream(Long id, String name) {
        try {
            return Files.newInputStream(getHWItemPrint3dFilePath(id, name));
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat soubor 3d modelu HW položky", e);
        }
    }

    @Override
    public boolean deleteHWItemPrint3dFile(Long id, String name) {
        Path models;
        try {
            models = getHWItemPrint3dPath(id);
            Path model = models.resolve(name);
            if (!model.normalize().startsWith(models)) throw new IllegalArgumentException(ILLEGAL_PATH_PRINT_3D_ERR);
            return Files.deleteIfExists(model);
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se smazat soubor 3d modelu HW položky.", e);
        }
    }

    /*
     * Documents
     */

    @Override
    public void saveDocumentsFile(InputStream in, String fileName, Long id) throws IOException {
        Path docsPath = getHWItemDocumentsPath(id);
        Path docPath = docsPath.resolve(fileName);
        if (!docPath.normalize().startsWith(docsPath)) throw new IllegalArgumentException(ILLEGAL_PATH_DOCS_ERR);
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
            throw new GrassException("Nezdařilo se získat přehled dokumentací HW položky", e);
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
            throw new GrassException("Nezdařilo se získat přehled dokumentací HW položky", e);
        }
    }

    @Override
    public Path getHWItemDocumentsFilePath(Long id, String name) {
        Path docs;
        try {
            docs = getHWItemDocumentsPath(id);
            Path doc = docs.resolve(name);
            if (!doc.normalize().startsWith(docs)) throw new IllegalArgumentException(ILLEGAL_PATH_DOCS_ERR);
            return doc;
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat soubor dokumentace HW položky", e);
        }
    }

    @Override
    public InputStream getHWItemDocumentsFileInputStream(Long id, String name) {
        try {
            return Files.newInputStream(getHWItemDocumentsFilePath(id, name));
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat soubor dokumentace HW položky", e);
        }
    }

    @Override
    public boolean deleteHWItemDocumentsFile(Long id, String name) {
        Path docs;
        try {
            docs = getHWItemDocumentsPath(id);
            Path doc = docs.resolve(name);
            if (!doc.normalize().startsWith(docs)) throw new IllegalArgumentException(ILLEGAL_PATH_DOCS_ERR);
            return Files.deleteIfExists(doc);
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se smazat soubor dokumentace HW položky.", e);
        }
    }

    /*
     * Icons
     */

    @Override
    public void createHWItemIcon(InputStream inputStream, String fileName, Long id) {
        String[] parts = fileName.split("\\.");
        String extension = parts.length >= 1 ? parts[parts.length - 1] : "";

        Path hwItemDir;
        try {
            hwItemDir = getHWPath(id);
            Path imagePath = hwItemDir.resolve("icon." + extension);
            IOUtils.copy(inputStream, Files.newOutputStream(imagePath));

            Path imageMiniPath = hwItemDir.resolve("icon_mini." + extension);
            createMiniature(imagePath, imageMiniPath);
        } catch (IOException e) {
            throw new GrassException("Nezdařila se příprava pro uložení ikony HW položky.", e);
        }
    }

    private Path getHWItemIconFile(Long id, boolean mini) throws IOException {
        Path hwPath = getHWPath(id);
        if (!Files.exists(hwPath)) return null;
        try (Stream<Path> stream = Files.list(hwPath)) {
            String regex = "icon";
            if (mini) regex += "_mini";
            regex += "\\.[^\\.]*";
            String finalRegex = regex;
            return stream.filter(p -> p.getFileName().toString().matches(finalRegex)).findFirst().orElse(null);
        }
    }

    @Override
    public Path getHWItemIconFile(Long id) throws IOException {
        return getHWItemIconFile(id, false);
    }

    @Override
    public Path getHWItemIconMiniFile(Long id) throws IOException {
        return getHWItemIconFile(id, true);
    }

    @Override
    public InputStream getHWItemIconFileInputStream(Long id) {
        try {
            Path path = getHWItemIconFile(id);
            return path != null ? Files.newInputStream(path) : null;
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat ikonu HW položky.", e);
        }
    }

    @Override
    public InputStream getHWItemIconMiniFileInputStream(Long id) {
        try {
            Path path = getHWItemIconMiniFile(id);
            return path != null ? Files.newInputStream(path) : null;
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se získat ikonu HW položky.", e);
        }
    }

    @Override
    public boolean deleteHWItemIconFile(Long id) {
        try {
            Path image = getHWItemIconFile(id);
            if (image != null) return Files.deleteIfExists(image);

            Path imageMini = getHWItemIconMiniFile(id);
            if (imageMini != null) return Files.deleteIfExists(imageMini);
            return false;
        } catch (IOException e) {
            throw new GrassException("Nezdařilo se smazat miniaturu ikonu HW položky.", e);
        }
    }

    /*
     * Item types
     */

    @Override
    public Long saveHWType(HWTypeTO hwTypeTO) {
        HWType type = hwMapper.mapHWItem(hwTypeTO);
        type = hwTypeRepository.save(type);
        return type.getId();
    }

    @Override
    public Set<HWTypeTO> getAllHWTypes() {
        return hwTypeRepository.findOrderByName();
    }

    @Override
    public HWTypeTO getHWType(Long fixTypeId) {
        return hwTypeRepository.findByIdAndMap(fixTypeId);
    }

    @Override
    public void deleteHWType(Long id) {
        // TODO usage check
        hwTypeRepository.deleteById(id);
    }

    @Override
    public List<HWTypeTO> getHWTypes(HWTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
        return hwTypeRepository.getHWTypes(filter, offset, limit, order);
    }

    @Override
    public int countHWTypes(HWTypeTO filter) {
        return (int) hwTypeRepository.countHWTypes(filter);
    }

    /*
     * Items
     */

    @Override
    public Long copyHWItem(Long origId) {
        HWItemTO item = getHWItem(origId);
        // jde o novou položku, takže prázdné id, žádné záznamy
        item.setId(null);
        item.setServiceNotes(null);
        // TODO
//		item.setUsedIn(null);
        item.setUsedInName(null);
        // zkopíruj přílohy

        Long copyId = saveHWItem(item);

        try {
            Path origPath = getHWPath(origId);
            if (Files.exists(origPath)) {
                Path copyPath = getHWPath(copyId);
                Files.walkFileTree(origPath, new SimpleFileVisitor<>() {
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
            throw new GrassException("Nezdařilo se vytvořit kopii souborů HW položky", e);
        }

        return copyId;
    }

    @Override
    public Long saveHWItem(HWItemTO hwItemDTO) {
        HWItem item;
        if (hwItemDTO.getId() == null) item = new HWItem();
        else item = hwItemRepository.findById(hwItemDTO.getId()).orElse(null);
        item.setName(hwItemDTO.getName());
        item.setPurchaseDate(hwItemDTO.getPurchaseDate());
        logger.info("Price: " + hwItemDTO.getPrice());
        item.setPrice(hwItemDTO.getPrice());
        item.setState(hwItemDTO.getState());
        item.setDescription(hwItemDTO.getDescription());
        item.setSupervizedFor(hwItemDTO.getSupervizedFor());
        item.setPublicItem(hwItemDTO.getPublicItem());
        item.setWarrantyYears(hwItemDTO.getWarrantyYears());
        // TODO
//		if (hwItemDTO.getUsedIn() != null) {
//			HWItem usedIn = hwItemRepository.findById(hwItemDTO.getUsedIn().getId()).orElse(null);
//			item.setUsedIn(usedIn);
//		} else {
//			item.setUsedIn(null);
//		}
//		if (hwItemDTO.getTypes() != null) {
//			item.setTypes(new HashSet<>());
//			for (String typeName : hwItemDTO.getTypes()) {
//				HWType type = hwTypeRepository.findByName(typeName);
//				if (type == null) {
//					type = new HWType(typeName);
//					type = hwTypeRepository.save(type);
//				}
//				item.getTypes().add(type);
//			}
//		}
        return hwItemRepository.save(item).getId();
    }

    @Override
    public int countHWItems(HWFilterTO filter) {
        return (int) hwItemRepository.countHWItems(filter);
    }

    @Override
    public List<HWItemOverviewTO> getAllHWItems() {
        return hwItemRepository.findAndMap();
    }

    @Override
    public List<HWItemOverviewTO> getHWItems(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
        return hwItemRepository.findAndMap(filter, offset, limit, order));
    }

    @Override
    public List<Long> getHWItemIds(HWFilterTO filter, OrderSpecifier<?>[] order) {
        return hwItemRepository.getHWItemIds(filter, order);
    }

    @Override
    public List<HWItemOverviewTO> getHWItemsByTypes(Collection<String> types) {
        return hwItemRepository.getHWItemsByTypes(types);
    }

    @Override
    public HWItemTO getHWItem(Long itemId) {
        return hwItemRepository.findByIdAndMap(itemId);
    }

    @Override
    public HWItemOverviewTO getHWOverviewItem(Long itemId) {
        return hwItemRepository.findByIdAndMap(itemId);
    }

    @Override
    public List<HWItemOverviewTO> getAllParts(Long usedInItemId) {
        return hwItemRepository.findByUsedInId(usedInItemId);
    }

    @Override
    public List<HWItemOverviewTO> getHWItemsAvailableForPart(Long itemId) {
        return hwItemRepository.findAllExcept(itemId);
    }

    @Override
    public void deleteHWItem(Long id) {
        // TODO usedIn DB FK ?
        hwItemRecordRepository.deleteByItemId(id);
        hwItemRepository.deleteById(id);

        Path hwPath = getHWPath(id);
        try (Stream<Path> s = Files.walk(hwPath)) {
            s.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    logger.error("Chyba při mazání souboru HW položky " + id + "]", e);
                }
            });
        } catch (Exception e) {
            logger.warn("Nezdařilo se smazat adresář příloh k HW položce [" + id + "]");
        }
    }

    /*
     * Service notes
     */

    /**
     * Vygeneruje {@link HWItemRecord} o přidání/odebrání HW, uloží a přidá k
     * cílovému HW
     */
    private void saveHWPartMoveHWItemRecord(Long targetItemId, String sourceItemName, LocalDate date, String cause,
                                            boolean added) {
        HWItemOverviewTO targetItem = hwItemRepository.findByIdAndMap(targetItemId);

        StringBuilder builder = new StringBuilder();
        builder.append(added ? "Byl přidán:\n" : "Byl odebrán:\n");
        builder.append(sourceItemName);
        builder.append("\n\n");
        builder.append("Důvod:\n");
        builder.append(cause);

        HWItemRecord record = new HWItemRecord();
        record.setDate(date);
        record.setDescription(builder.toString());
        record.setState(targetItem.getState());
        record.setUsage(targetItem.getUsedInId() == null ? "" : targetItem.getUsedInName());
        record.setHwItemId(targetItem.getId());
        hwItemRecordRepository.save(record);
    }

    @Override
    public void addItemRecord(HWItemRecordTO itemRecordTO, Long id, Long usedInId, String name) {
        HWItemRecord serviceNote = new HWItemRecord();
        serviceNote.setDate(itemRecordTO.getDate());
        serviceNote.setDescription(itemRecordTO.getDescription());
        serviceNote.setState(itemRecordTO.getState());
        serviceNote.setUsage(itemRecordTO.getUsedInName());
        serviceNote = hwItemRecordRepository.save(serviceNote);

        hwItemRepository.updateState(id, serviceNote.getState());

        // HW je někde součástí
        if (itemRecordTO.getUsedInId() != null) {

            // předtím nebyl nikde součástí
            if (usedInId == null) {
                hwItemRepository.updateUsedInId(id, itemRecordTO.getUsedInId());
                saveHWPartMoveHWItemRecord(usedInId,name, itemRecordTO.getDate(),
                        serviceNote.getDescription(), true);
            } else if (usedInId != itemRecordTO.getUsedInId()) {
                // již předtím byl součástí, ale nyní je jinde
                saveHWPartMoveHWItemRecord(usedInId, name, itemRecordTO.getDate(),
                        serviceNote.getDescription(), false);
                hwItemRepository.updateUsedInId(id, itemRecordTO.getUsedInId());
                saveHWPartMoveHWItemRecord(usedInId, name, itemRecordTO.getDate(),
                        serviceNote.getDescription(), true);
            } else {
                // nic se nezměnilo - HW je stále součástí stejného HW
            }

        } else { // HW není nikde součástí

            // už předtím nebyl nikde součástí
            if (usedInId == null) {
                // nic se nezměnilo - HW stále není nikde evidován jako součást
            } else {
                // předtím někde byl
                saveHWPartMoveHWItemRecord(usedInId, name, itemRecordTO.getDate(),
                        serviceNote.getDescription(), false);
                hwItemRepository.updateUsedInId(id, null);
            }
        }
    }

    @Override
    public void modifyServiceNote(HWItemRecordTO serviceNoteDTO) {
        HWItemRecord serviceNote = hwItemRecordRepository.findById(serviceNoteDTO.getId()).orElse(null);
        serviceNote.setDate(serviceNoteDTO.getDate());
        serviceNote.setDescription(serviceNoteDTO.getDescription());
        serviceNote.setState(serviceNoteDTO.getState());
        serviceNote.setUsage(serviceNoteDTO.getUsedInName());
        hwItemRecordRepository.save(serviceNote);
    }

    @Override
    public void deleteServiceNote(Long id) {
        hwItemRecordRepository.deleteById(id);
    }

}
