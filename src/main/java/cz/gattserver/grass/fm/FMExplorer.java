package cz.gattserver.grass.fm;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.fm.config.FMConfiguration;
import cz.gattserver.grass.fm.interfaces.FMItemTO;
import cz.gattserver.grass.fm.service.FMService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
public class FMExplorer {

    /**
     * Filesystem, pod kterým {@link FMExplorer} momentálně operuje
     */
    private final FMService fmService;
    private final FileSystemService fileSystemService;

    /**
     * Cesta ke kořeni FM úložiště
     */
    private final Path rootPath;

    /**
     * Plná cesta od systémového kořene
     */
    private Path currentAbsolutePath;

    /**
     * {@link FMExplorer} začne v adresáři, který je podle konfigurace jako jeho
     * root (omezení).
     *
     * @param fileSystem {@link FileSystem}, ve kterém se bude {@link FMExplorer}
     *                   pohybovat
     */
    public FMExplorer(FileSystem fileSystem) {
        Validate.notNull(fileSystem, "Filesystem nesmí být null");
        this.fmService = SpringContextHelper.getBean(FMService.class);
        this.fileSystemService = SpringContextHelper.getBean(FileSystemService.class);

        FMConfiguration configuration = loadConfiguration();
        String rootDir = configuration.getRootDir();
        rootPath = fileSystem.getPath(rootDir).normalize();
        if (!Files.exists(rootPath)) throw new IllegalStateException("Kořenový adresář FM modulu musí existovat");
        currentAbsolutePath = rootPath;
    }

    private FMConfiguration loadConfiguration() {
        ConfigurationService configurationService =
                SpringContextHelper.getContext().getBean(ConfigurationService.class);
        FMConfiguration c = new FMConfiguration();
        configurationService.loadConfiguration(c);
        return c;
    }

    /**
     * Změní aktuální adresář na adresář dle cesty od kořenového adresáře FM.
     *
     * @param path cesta k adresáři od kořenového adresáře FM
     * @return výsledek operace
     */
    public FileProcessState goToDir(String path) {
        return goToDir(rootPath.resolve(path).normalize());
    }

    /**
     * Změní aktuální adresář na adresář dle cesty od aktuálního adresáře.
     *
     * @param path cesta k adresáři z aktuálního adresáře
     * @return výsledek operace
     */
    public FileProcessState goToDirFromCurrentDir(String path) {
        return goToDir(currentAbsolutePath.resolve(path).normalize());
    }

    private FileProcessState goToDir(Path path) {
        if (isInvalid(path)) return FileProcessState.NOT_VALID;
        if (!Files.exists(path)) return FileProcessState.MISSING;
        if (!Files.isDirectory(path)) return FileProcessState.DIRECTORY_REQUIRED;
        currentAbsolutePath = path;
        return FileProcessState.SUCCESS;
    }

    /**
     * Zjistí, zda požadovaný adresář podtéká kořenový adresář
     */
    public boolean isInvalid(Path adeptPath) {
        return !adeptPath.normalize().startsWith(rootPath);
    }

    /**
     * Vytvoř nový adresář v aktuálním adresáři
     *
     * @param path cesta k souboru z aktuálního adresáře
     * @return výsledek operace
     */
    public FileProcessState createNewDir(String path) {
        Path newPath = currentAbsolutePath.resolve(path).normalize();
        try {
            if (isInvalid(newPath)) return FileProcessState.NOT_VALID;
            if (Files.exists(newPath)) return FileProcessState.ALREADY_EXISTS;
            fileSystemService.createDirectoryWithPerms(newPath);
            return FileProcessState.SUCCESS;
        } catch (IOException e) {
            log.error("Nezdařilo se vytvořit nový adresář {}", newPath, e);
            return FileProcessState.SYSTEM_ERROR;
        }
    }

    /**
     * Vrátí počet položek pro výpis obsahu aktuálního adresáře. Započítává i
     * odkaz ".." na nadřazený adresář.
     */
    public int listCount(String filterName) {
        try (Stream<Path> stream = Files.list(currentAbsolutePath).filter(p -> p.getFileName().toString().toLowerCase()
                .contains(filterName == null ? "" : filterName.toLowerCase()))) {
            // +1 za odkaz na nadřazený adresář
            int parentDirIncrement = currentAbsolutePath.equals(rootPath) ? 0 : 1;
            return (int) stream.count() + parentDirIncrement;
        } catch (IOException e) {
            throw new IllegalStateException("Nezdařilo se získat počet souborů", e);
        }
    }

    private Stream<FMItemTO> createParentStream() {
        return currentAbsolutePath.equals(rootPath) ? Stream.empty() :
                Stream.of(currentAbsolutePath.resolve("..")).map(e -> FMUtils.mapPathToItem(e, currentAbsolutePath));
    }

    /**
     * Vrátí {@link Stream} absolutních {@link Path} a na začátku ".." odkaz na
     * nadřazený adresář.
     */
    public Stream<FMItemTO> listing(String filterName, int offset, int limit, List<QuerySortOrder> list) {
        try (Stream<FMItemTO> streamParent = createParentStream();
             Stream<Path> streamFiles = Files.list(currentAbsolutePath)) {
            Stream<FMItemTO> resultStream = streamFiles.filter(p -> p.getFileName().toString().toLowerCase()
                            .contains(filterName == null ? "" : filterName.toLowerCase()))
                    .map(e -> FMUtils.mapPathToItem(e, currentAbsolutePath))
                    .sorted((to1, to2) -> FMUtils.sortFile(to1, to2, list));
            // musí se přehodit, aby nezůstal viset původní stream, na kterém
            // jsou vytvořené zámky na soubory
            return Stream.concat(streamParent, resultStream).skip(offset).limit(limit).toList().stream();
        } catch (IOException e) {
            throw new IllegalStateException("Nezdařilo se získat list souborů", e);
        }
    }

    /**
     * Uloží nahraný soubor
     *
     * @param in   vstupní proud dat
     * @param path cesta k souboru z aktuálního adresáře pod kterou bude soubor
     *             uložen
     * @return výsledek operace
     */
    public FileProcessState saveFile(InputStream in, String path) {
        Path pathToSaveAs = currentAbsolutePath.resolve(path).normalize();
        try {
            Files.copy(in, pathToSaveAs);
            fileSystemService.grantPermissions(pathToSaveAs);
        } catch (FileAlreadyExistsException f) {
            return FileProcessState.ALREADY_EXISTS;
        } catch (IOException e) {
            log.error("Nezdařilo se vytvořit soubor {}", path, e);
            return FileProcessState.SYSTEM_ERROR;
        }
        return FileProcessState.SUCCESS;
    }

    /**
     * Smaže soubor. Nelze smazat FM root.
     *
     * @param path cesta k souboru z aktuálního adresáře
     * @return výsledek operace
     */
    public FileProcessState deleteFile(String path) {
        Path pathToDelete = currentAbsolutePath.resolve(path).normalize();
        try {
            if (isInvalid(pathToDelete) || rootPath.equals(pathToDelete)) return FileProcessState.NOT_VALID;
            if (!Files.exists(pathToDelete)) return FileProcessState.MISSING;
            FileSystemUtils.deleteRecursively(pathToDelete);
            return FileProcessState.SUCCESS;
        } catch (IOException e) {
            log.error("Nezdařilo se smazat soubor {}", path, e);
            return FileProcessState.SYSTEM_ERROR;
        }
    }

    /**
     * Přejmenuje soubor. Nelze přejmenovat FM root.
     *
     * @param path    cesta ke stávajícímu souboru z aktuálního adresáře
     * @param newPath cesta k novému souboru z aktuálního adresáře -- umožňuje
     *                použít ".." a "/" pro přesun
     */
    public FileProcessState renameFile(String path, String newPath) {
        Path currentPath = currentAbsolutePath.resolve(path).normalize();
        Path renamedPath = currentPath.getParent().resolve(newPath).normalize();
        try {
            if (isInvalid(currentPath) || isInvalid(renamedPath) || rootPath.equals(currentPath))
                return FileProcessState.NOT_VALID;
            if (!Files.exists(currentPath)) return FileProcessState.MISSING;
            if (Files.exists(renamedPath)) return FileProcessState.ALREADY_EXISTS;
            Files.move(currentPath, renamedPath);
            return FileProcessState.SUCCESS;
        } catch (IOException e) {
            log.error("Nezdařilo se přejmenovat soubor {} na {}", path, newPath, e);
            return FileProcessState.SYSTEM_ERROR;
        }
    }

    /**
     * Připraví dle aktuální cesty (od FM kořene) díly, ze kterých lze sestavit
     * breadcrumb navigaci.
     */
    public List<FMItemTO> getBreadcrumbChunks() {
        Path next = currentAbsolutePath;
        List<FMItemTO> chunks = new ArrayList<>();
        do {
            String fileURLFromRoot = getPathFromRoot(next);
            String name = next.equals(rootPath) ? "/" : next.getFileName().toString();
            chunks.add(new FMItemTO(name, fileURLFromRoot));
            next = next.getParent();
            // pokud je můj předek null nebo jsem mimo povolený rozsah, pak
            // je to konec a je to všechno
        } while (next != null && next.startsWith(rootPath));
        return chunks;
    }

    private String getPathFromRoot(Path path) {
        return rootPath.relativize(path).toString();
    }

    private Path getCurrentRelativePath() {
        return rootPath.relativize(currentAbsolutePath);
    }

    /**
     * Získá URL link k souboru v aktuálním adresáři. Neověřuje, zda soubor v
     * aktuálním adresáři opravdu existuje, pouze sestavuje link tak, jako by v
     * něm byl.
     *
     * @param contextRootURL kořenové url aplikace, například
     *                       <code><a href="http://testweb/grass">http://testweb/grass</a></code>
     * @param path           cesta k souboru z aktuálního adresáře
     * @return link k souboru
     */
    public String getDownloadLink(String contextRootURL, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(contextRootURL);
        if (!contextRootURL.endsWith("/")) sb.append("/");
        sb.append(FMConfiguration.FM_PATH);
        for (Path part : getCurrentRelativePath()) {
            sb.append("/");
            sb.append(part.toString());
        }
        if (!sb.toString().endsWith("/")) sb.append("/");
        sb.append(path);
        return sb.toString();
    }

    /**
     * Získá URL pro aktuální stav FM. Pokud je tedy například FM v adresáři
     * <code>alfa</code>, contextRoot aplikace je
     * <code><a href="">http://testweb/grass</a></code> a URL cesta k FM modulu je
     * <code>fm-modul</code>, pak výsledné URL bude <br/>
     *
     * <code>http://testweb/grass/fm-modul/alfa</code>
     *
     * @param contextRootURL kořenové url aplikace, například
     *                       <code>http://testweb/grass</code>
     * @param modulePageName URL cesta k FM modulu, například <code>fm-modul</code>
     * @return výsledné URL k aktuálnímu adresáři
     */
    public String getCurrentURL(String contextRootURL, String modulePageName) {
        StringBuilder sb = new StringBuilder();
        sb.append(contextRootURL);
        sb.append("/");
        sb.append(modulePageName);
        for (Path part : getCurrentRelativePath()) {
            sb.append("/");
            sb.append(URLEncoder.encode(part.toString(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    /**
     *
     * @param contextRootURL kořenové url aplikace, například
     *                       <code><a href="">http://testweb/grass</a></code>
     * @param modulePageName URL cesta k FM modulu, například <code>fm-modul</code>
     * @param url            URL, dle kterého se má získat adresář, kam se mám přepnout
     * @return výsledek operace
     */
    public FileProcessState goToDirByURL(String contextRootURL, String modulePageName, String url) {
        // Odparsuj počátek http://host//context-root/fm a získej
        // lokální cestu v rámci FM modulu
        int start = url.indexOf(contextRootURL);
        String fmPath = url.substring(start + contextRootURL.length() + 1 + modulePageName.length());
        if (fmPath.isEmpty() || fmPath.startsWith("/")) {
            if (fmPath.startsWith("/")) fmPath = fmPath.substring(1);
            return goToDir(fmPath);
        } else {
            // úplně jiná stránka, která akorát začíná na
            // "context-root/fm"
            return FileProcessState.SYSTEM_ERROR;
        }
    }

    public void zipFiles(Set<FMItemTO> items) {
        Set<Path> files = new HashSet<>();
        for (FMItemTO to : items)
            files.add(currentAbsolutePath.resolve(to.name()));
        fmService.zipFiles(files);
    }
}