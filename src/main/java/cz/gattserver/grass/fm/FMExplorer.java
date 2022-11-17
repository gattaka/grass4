package cz.gattserver.grass.fm;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.fm.config.FMConfiguration;
import cz.gattserver.grass.fm.interfaces.FMItemTO;
import cz.gattserver.grass.fm.service.FMService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FMExplorer {

	private Logger logger = LoggerFactory.getLogger(FMExplorer.class);

	@Autowired
	private FMService fmService;

	@Autowired
	private FileSystemService fileSystemService;

	/**
	 * Filesystem, pod kterým {@link FMExplorer} momentálně operuje
	 */
	private FileSystem fileSystem;

	/**
	 * Cesta ke kořeni FM úložiště
	 */
	private Path rootPath;

	/**
	 * Plná cesta od systémového kořene
	 */
	private Path currentAbsolutePath;

	/**
	 * {@link FMExplorer} začne v adresáři, který je podle konfigurace jako jeho
	 * root (omezení).
	 * 
	 * @param fileSystem
	 *            {@link FileSystem}, ve kterém se bude {@link FMExplorer}
	 *            pohybovat
	 */
	public FMExplorer(FileSystem fileSystem) {
		Validate.notNull(fileSystem, "Filesystem nesmí být null");

		this.fileSystem = fileSystem;
		loadRootDirFromConfiguration();

		currentAbsolutePath = rootPath;

		SpringContextHelper.inject(this);
	}

	private void loadRootDirFromConfiguration() {
		FMConfiguration configuration = loadConfiguration();
		String rootDir = configuration.getRootDir();
		rootPath = fileSystem.getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new IllegalStateException("Kořenový adresář FM modulu musí existovat");
		rootPath = rootPath.normalize();
	}

	private FMConfiguration loadConfiguration() {
		ConfigurationService configurationService = SpringContextHelper.getContext()
				.getBean(ConfigurationService.class);
		FMConfiguration c = new FMConfiguration();
		configurationService.loadConfiguration(c);
		return c;
	}

	/**
	 * Změní aktuální adresář na adresář dle cesty od kořenového adresáře FM.
	 * 
	 * @param path
	 *            cesta k adresáři od kořenového adresáře FM
	 * @return výsledek operace
	 */
	public FileProcessState goToDir(String path) {
		return goToDir(rootPath.resolve(path).normalize());
	}

	/**
	 * Změní aktuální adresář na adresář dle cesty od aktuálního adresáře.
	 * 
	 * @param path
	 *            cesta k adresáři z aktuálního adresáře
	 * @return výsledek operace
	 */
	public FileProcessState goToDirFromCurrentDir(String path) {
		return goToDir(currentAbsolutePath.resolve(path).normalize());
	}

	private FileProcessState goToDir(Path path) {
		if (!isValid(path))
			return FileProcessState.NOT_VALID;
		if (!Files.exists(path))
			return FileProcessState.MISSING;
		if (!Files.isDirectory(path))
			return FileProcessState.DIRECTORY_REQUIRED;
		currentAbsolutePath = path;
		return FileProcessState.SUCCESS;
	}

	/**
	 * Ověří, že požadovaný adresář nepodtéká kořenový adresář
	 * 
	 * @throws IOException
	 *             pokud testovaný soubor neexistuje
	 */
	public boolean isValid(Path adeptPath) {
		return adeptPath.normalize().startsWith(rootPath);
	}

	/**
	 * Vytvoř nový adresář v aktuálním adresáři
	 * 
	 * @param path
	 *            cesta k souboru z aktuálního adresáře
	 * @return výsledek operace
	 */
	public FileProcessState createNewDir(String path) {
		Path newPath = currentAbsolutePath.resolve(path).normalize();
		try {
			if (!isValid(newPath))
				return FileProcessState.NOT_VALID;
			if (Files.exists(newPath))
				return FileProcessState.ALREADY_EXISTS;
			fileSystemService.createDirectoryWithPerms(newPath);
			return FileProcessState.SUCCESS;
		} catch (IOException e) {
			logger.error("Nezdařilo se vytvořit nový adresář {}", newPath.toString(), e);
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	/**
	 * Vrátí počet položek pro výpis obsahu aktuálního adresáře. Započítává i
	 * odkaz ".." na nadřazený adresář.
	 * 
	 * @return
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

	/**
	 * Vrátí {@link Stream} absolutních {@link Path} a na začátku ".." odkaz na
	 * nadřazený adresář.
	 * 
	 * @param offset
	 *            offset pro stránkování
	 * @param limit
	 *            velikost stránky
	 * @param list
	 * @return
	 */
	public Stream<FMItemTO> listing(String filterName, int offset, int limit, List<QuerySortOrder> list) {
		try (Stream<FMItemTO> streamParent = currentAbsolutePath.equals(rootPath) ? Stream.empty()
				: Stream.of(currentAbsolutePath.resolve("..")).map(e -> FMUtils.mapPathToItem(e, currentAbsolutePath));
				Stream<FMItemTO> streamFiles = Files.list(currentAbsolutePath)
						.filter(p -> p.getFileName().toString().toLowerCase()
								.contains(filterName == null ? "" : filterName.toLowerCase()))
						.map(e -> FMUtils.mapPathToItem(e, currentAbsolutePath))
						.sorted((to1, to2) -> FMUtils.sortFile(to1, to2, list))) {
			// musí se přehodit, aby nezůstal viset původní stream, na kterém
			// jsou vytvořené zámky na soubory
			return Stream.concat(streamParent, streamFiles).skip(offset).limit(limit).collect(Collectors.toList())
					.stream();
		} catch (IOException e) {
			throw new IllegalStateException("Nezdařilo se získat list souborů", e);
		}
	}

	/**
	 * Uloží nahraný soubor
	 * 
	 * @param in
	 *            vstupní proud dat
	 * @param path
	 *            cesta k souboru z aktuálního adresáře pod kterou bude soubor
	 *            uložen
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
			logger.error("Nezdařilo se vytvořit soubor {}", path, e);
			return FileProcessState.SYSTEM_ERROR;
		}
		return FileProcessState.SUCCESS;
	}

	/**
	 * Smaže soubor. Nelze smazat FM root.
	 * 
	 * @param path
	 *            cesta k souboru z aktuálního adresáře
	 * @return výsledek operace
	 */
	public FileProcessState deleteFile(String path) {
		Path pathToDelete = currentAbsolutePath.resolve(path).normalize();
		try {
			if (!isValid(pathToDelete) || rootPath.equals(pathToDelete))
				return FileProcessState.NOT_VALID;
			if (!Files.exists(pathToDelete))
				return FileProcessState.MISSING;
			FileSystemUtils.deleteRecursively(pathToDelete);
			return FileProcessState.SUCCESS;
		} catch (IOException e) {
			logger.error("Nezdařilo se smazat soubor {}", path, e);
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	/**
	 * Přejmenuje soubor. Nelze přejmenovat FM root.
	 * 
	 * @param path
	 *            cesta ke stávajícímu souboru z aktuálního adresáře
	 * @param newPath
	 *            cesta k novému souboru z aktuálního adresáře -- umožňuje
	 *            použít ".." a "/" pro přesun
	 */
	public FileProcessState renameFile(String path, String newPath) {
		Path currentPath = currentAbsolutePath.resolve(path).normalize();
		Path renamedPath = currentPath.getParent().resolve(newPath).normalize();
		try {
			if (!isValid(currentPath) || !isValid(renamedPath) || rootPath.equals(currentPath))
				return FileProcessState.NOT_VALID;
			if (!Files.exists(currentPath))
				return FileProcessState.MISSING;
			if (Files.exists(renamedPath))
				return FileProcessState.ALREADY_EXISTS;
			Files.move(currentPath, renamedPath);
			return FileProcessState.SUCCESS;
		} catch (IOException e) {
			logger.error("Nezdařilo se přejmenovat soubor {} na {}", path, newPath, e);
			return FileProcessState.SYSTEM_ERROR;
		}
	}

	/**
	 * Připraví dle aktuální cesty (od FM kořene) díly, ze kterých lze sestavit
	 * breadcrumb navigaci.
	 * 
	 * @return
	 */
	public List<FMItemTO> getBreadcrumbChunks() {
		Path next = currentAbsolutePath;
		List<FMItemTO> chunks = new ArrayList<>();
		do {
			String fileURLFromRoot = getPathFromRoot(next);
			chunks.add(new FMItemTO().setName(next.equals(rootPath) ? "/" : next.getFileName().toString())
					.setPathFromFMRoot(fileURLFromRoot));
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
	 * @param contextRootURL
	 *            kořenové url aplikace, například
	 *            <code>http://testweb/grass</code>
	 * @param path
	 *            cesta k souboru z aktuálního adresáře
	 * @return link k souboru
	 */
	public String getDownloadLink(String contextRootURL, String path) {
		StringBuilder sb = new StringBuilder();
		sb.append(contextRootURL);
		if (!contextRootURL.endsWith("/"))
			sb.append("/");
		sb.append(FMConfiguration.FM_PATH);
		for (Path part : getCurrentRelativePath()) {
			sb.append("/");
			sb.append(part.toString());
		}
		if (!sb.toString().endsWith("/"))
			sb.append("/");
		sb.append(path);
		return sb.toString();
	}

	/**
	 * Získá URL pro aktuální stav FM. Pokud je tedy například FM v adresáři
	 * <code>alfa</code>, contextRoot aplikace je
	 * <code>http://testweb/grass</code> a URL cesta k FM modulu je
	 * <code>fm-modul</code>, pak výsledné URL bude <br/>
	 * 
	 * <code>http://testweb/grass/fm-modul/alfa</code>
	 * 
	 * @param contextRootURL
	 *            kořenové url aplikace, například
	 *            <code>http://testweb/grass</code>
	 * @param modulePageName
	 *            URL cesta k FM modulu, například <code>fm-modul</code>
	 * @return výsledné URL k aktuálnímu adresáři
	 */
	public String getCurrentURL(String contextRootURL, String modulePageName) {
		StringBuilder sb = new StringBuilder();
		sb.append(contextRootURL);
		sb.append("/");
		sb.append(modulePageName);
		for (Path part : getCurrentRelativePath()) {
			sb.append("/");
			try {
				sb.append(URLEncoder.encode(part.toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param contextRootURL
	 *            kořenové url aplikace, například
	 *            <code>http://testweb/grass</code>
	 * @param modulePageName
	 *            URL cesta k FM modulu, například <code>fm-modul</code>
	 * @param uri
	 *            URL, dle kterého se má získat adresář, kam se mám přepnout
	 * @return výsledek operace
	 */
	public FileProcessState goToDirByURL(String contextRootURL, String modulePageName, String uri) {
		// Odparsuj počátek http://host//context-root/fm a získej
		// lokální cestu v rámci FM modulu
		int start = uri.indexOf(contextRootURL);
		String fmPath = uri.substring(start + contextRootURL.length() + 1 + modulePageName.length());
		if (fmPath.isEmpty() || fmPath.startsWith("/")) {
			if (fmPath.startsWith("/"))
				fmPath = fmPath.substring(1);
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
			files.add(currentAbsolutePath.resolve(to.getName()));
		fmService.zipFiles(files);
	}

}
