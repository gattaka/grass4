package cz.gattserver.grass.articles.plugins.favlink;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Cache již stažených ikon
 * 
 * @author Hynek
 *
 */
public class FaviconCache {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private FileSystemService fileSystemService;

	private Path cacheDir;

	public FaviconCache() {
		SpringContextHelper.inject(this);
		cacheDir = getCacheDirectoryPath();
	}

	public String getFavicon(String pageAddress) {
		URL pageURL = FaviconUtils.getPageURL(pageAddress);
		String faviconRootFilename = FaviconUtils.createFaviconRootFilename(pageURL);

		try {
			Optional<Path> result = Files.list(cacheDir)
					.filter(p -> p.getFileName().toString().matches(faviconRootFilename + "\\.[^.]+")).findFirst();
			if (result.isPresent())
				return result.get().getFileName().toString();
		} catch (IOException e) {
			throw new ParserException("Nezdařilo se prohledat favicony z adresáře favicon", e);
		}

		return null;
	}

	/**
	 * Pokusí se stáhnout a uložit faviconu dle adresy stránky a adresy favicon
	 * souboru a vrátit jméno souboru, pod kterým byla favicona uložena
	 * 
	 * @param pageURL
	 *            adresa stránky jejíž favicon hledám
	 * @param faviconAddress
	 *            adresa souboru favicony
	 * @return název staženého souboru favicony, nebo <code>null</code>, pokud
	 *         se stažení nezdařilo (soubor na adrese neexistuje apod.)
	 */
	public String downloadAndSaveFavicon(URL pageURL, String faviconAddress) {

		String filename = FaviconUtils.getFaviconFilename(pageURL, faviconAddress);
		if (FaviconUtils.downloadFile(cacheDir.resolve(filename), faviconAddress)) {
			return filename;
		} else {
			return null;
		}
	}

	/**
	 * Zjistí dle aktuální konfigurace výstupní adresář
	 */
	public Path getCacheDirectoryPath() {
		FavlinkConfiguration configuration = new FavlinkConfiguration();
		configurationService.loadConfiguration(configuration);

		// existuje cesta cache?
		Path path = fileSystemService.getFileSystem().getPath(configuration.getOutputPath());
		if (Files.exists(path)) {
			if (!Files.isDirectory(path))
				throw new ParserException("Favicon cache soubor není adresář");
		} else {
			try {
				fileSystemService.createDirectoriesWithPerms(path);
			} catch (Exception e) {
				throw new ParserException("Vytváření favicon cache adresáře se nezdařilo", e);
			}
		}

		return path;
	}

}
