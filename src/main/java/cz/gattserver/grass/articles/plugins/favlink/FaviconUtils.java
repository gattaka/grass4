package cz.gattserver.grass.articles.plugins.favlink;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass.core.services.FileSystemService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author gatt
 */
public class FaviconUtils {

	private static final Logger logger = LoggerFactory.getLogger(FaviconUtils.class);

	private FaviconUtils() {
	}

	private static InputStream getResponseReader(String address) {
		// na některých stránkách se následující agenti projeví jako timout
		// (stránky je potichu odmítnou, zřejmě jako bot request):
		// Mozilla
		// Mozilla/5.0
		String agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)";

		URL url = null;
		InputStream is = null;
		try {
			// musí se odstranit, protože například právě pro VAADIN je tento
			// lokální krok příčinou, proč se vrátí
			// DOCUMENT response s neplatnou session, namísto adresovaného
			// souboru favicony
			address = address.replace("/./", "/");
			url = new URL(address).toURI().normalize().toURL();
			URLConnection uc = url.openConnection();
			if (uc != null) {
				if (uc instanceof HttpURLConnection) {
					// HttpURLConnection
					HttpURLConnection hc = (HttpURLConnection) uc;
					hc.setInstanceFollowRedirects(true);

					// bez agenta to často hodí 403 Forbidden, protože si myslí,
					// že jsem asi bot ... (což vlastně jsem)
					hc.setRequestProperty("User-Agent", agent);
					hc.setRequestProperty("Accept-Encoding", "gzip");
					logger.info("Favicon URL: " + uc.getURL());
					int timeout = 2000; // 2s
					hc.setConnectTimeout(timeout);
					hc.setReadTimeout(timeout);
					hc.connect();

					Map<String, List<String>> map = uc.getHeaderFields();
					for (Map.Entry<String, List<String>> entry : map.entrySet())
						logger.info(entry.getKey() + " : " + entry.getValue());

					// Zjisti, zda bude potřeba manuální redirect (URLConnection
					// to umí samo, dokud se nepřechází mezi
					// HTTP a HTTPS, pak to nechává na manuální obsluze)
					int responseCode = hc.getResponseCode();
					if (responseCode >= 300 && responseCode < 400) {
						String location = hc.getHeaderField("Location");
						hc = (HttpURLConnection) (new URL(location).openConnection());
						hc.setInstanceFollowRedirects(false);
						hc.setRequestProperty("User-Agent", agent);
						hc.connect();
						responseCode = hc.getResponseCode();
					}
					if (responseCode < 200 || responseCode >= 300) {
						logger.info("ERR: responseCode = " + responseCode);
						return null;
					}

					if (hc.getContentType() != null
							&& (hc.getContentType().contains("text") || hc.getContentType().contains("html"))) {
						logger.info("ERR: ContentType = " + hc.getContentType());
						return null;
					}

					logger.info("Favicon connected URL: " + hc.getURL());
					if (hc.getContentLength() == 0) {
						logger.info("ERR: ContentLength = 0 ");
						return null;
					}

					is = hc.getInputStream();
					if (is != null) {
						logger.info("InputStream je OK");
					} else {
						logger.info("ERR: InputStream je null!");
					}

					if (!(is instanceof GZIPInputStream)) {
						List<String> encodings = map.get("Content-Encoding");
						if (encodings != null) {
							for (String enc : encodings) {
								if ("gzip".equals(enc)) {
									is = new GZIPInputStream(is);
									break;
								}
							}
						}
					}

					return is;
				}
			} else {
				logger.info("ERR: URL openConnection selhal");
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// hm...
				}
			}
		}
		return null;
	}

	/**
	 * Stáhne obsah dle adresy a uloží ho jako předaný {@link Path}
	 * 
	 * @param targetFile
	 *            cílový soubor, do kterého bude obsah uložen
	 * @param address
	 *            adresa obsahu, který bude stažen
	 * @return <code>true</code> pokud byl obsah úspěšně stažen a uložen do
	 *         souboru nebo <code>false</code>, pokud se ho nepovedlo stáhnout.
	 *         V případě, že chyba nastala až při ukládání je vyhozen
	 *         {@link ParserException}
	 */
	public static boolean downloadFile(Path targetFile, String address) {
		Validate.notNull(targetFile, "'targetFile' nesmí být null");
		Validate.notBlank(address, "'address' nesmí být null");
		logger.info("Zkouším stáhnout a uložit favicon adresy {} jako {}", address, targetFile);
		InputStream stream = getResponseReader(address);
		if (stream != null) {
			try {
				Files.copy(stream, targetFile);
				FileSystemService fss = SpringContextHelper.getBean(FileSystemService.class);
				fss.grantPermissions(targetFile);
				long size = Files.size(targetFile);
				if (size == 0) {
					logger.info("Favicon má velikost 0B, mažu soubor a označuju download jako neúspěšný");
					Files.delete(targetFile);
					return false;
				}
				logger.info("Favicon uložen a má velikost " + size + "B");
				return true;
			} catch (IOException e) {
				throw new ParserException("Nezdařilo se uložit staženou favicon", e);
			}
		}
		return false;
	}

	/**
	 * Vytvoří adresu, na které bude dostupný favicon soubor z cache
	 * 
	 * @param contextRoot
	 *            kořenové URL, od kterého se budou vytváře interní linky
	 *            aplikace
	 * @param faviconFilename
	 *            název favicon souboru, ke kterému je adresa vytvářena
	 * @return URL adresa k favicon souboru z cache
	 */
	public static String createCachedFaviconAddress(String contextRoot, String faviconFilename) {
		Validate.notNull(contextRoot, "contextRoot nesmí být null");
		Validate.notBlank(faviconFilename, "faviconFilename nesmí být prázdný");
		return contextRoot + "/" + FavlinkConfiguration.IMAGE_PATH_ALIAS + "/" + faviconFilename;
	}

	/**
	 * Vytvoří adresu, na které bude dostupný výchozí favicon soubor
	 * 
	 * @param contextRoot
	 *            kořenové URL, od kterého se budou vytváře interní linky
	 *            aplikace
	 * @return URL adresa k favicon souboru
	 */
	public static String createDefaultFaviconAddress(String contextRoot) {
		return contextRoot + "/articles/favlink/default.ico";
	}

	/**
	 * Získá {@link URL} z řetězce adresy nebo vyhodí {@link ParserException}.
	 * 
	 * @param pageAddress
	 *            webová adresa, která má být zpracována
	 * @return {@link URL} objekt dle adresy
	 */
	public static URL getPageURL(String pageAddress) {
		try {
			return new URL(pageAddress);
		} catch (MalformedURLException e) {
			throw new ParserException("Nezdařilo se vytěžit název domény ze zadané adresy", e);
		}
	}

	/**
	 * Vrátí jméno souboru favicony, dle adresy stránky, jejíž favicon se
	 * získává a adresy favicony. Bere tak v potaz příponu souboru.
	 * 
	 * @param pageURL
	 *            adresa stránky, jejíž favicon hledám (z ní získá základ názvu
	 *            souboru)-- například http://test.domain.com/neco/nekde
	 * @param faviconAddress
	 *            adresa souboru favicony, kterou jsem našel (z ní získá příponu
	 *            souboru favicony) -- například
	 *            http://test.domain.com/imgs/fav.ico
	 * @return název souboru favicony -- například test.domain.com.ico
	 */
	public static String getFaviconFilename(URL pageURL, String faviconAddress) {
		String faviconRootFilename = FaviconUtils.createFaviconRootFilename(pageURL);
		String extension = faviconAddress.substring(faviconAddress.lastIndexOf('.'), faviconAddress.length());
		// Odstraní případné ?param=value apod. za .pripona textem
		extension = extension.replaceAll("[^\\.A-Za-z0-9].+", "");
		return faviconRootFilename + extension;
	}

	/**
	 * Vytvoří název souboru ikony dle předaného URL odkazu, ke kterému favicon
	 * hledám.
	 * 
	 * @param pageURL
	 *            odkaz, ke kterému hledám favicon
	 * @return název favicon souboru bez přípony (tu ještě neznám)
	 */
	public static String createFaviconRootFilename(URL pageURL) {
		return pageURL.getHost();
	}

}
