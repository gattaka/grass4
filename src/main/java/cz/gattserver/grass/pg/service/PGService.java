package cz.gattserver.grass.pg.service;

import cz.gattserver.grass.core.exception.UnauthorizedAccessException;
import cz.gattserver.grass.pg.config.PGConfiguration;
import cz.gattserver.grass.pg.events.impl.PGProcessProgressEvent;
import cz.gattserver.grass.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass.pg.events.impl.PGProcessStartEvent;
import cz.gattserver.grass.pg.interfaces.*;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PGService {

	/**
	 * Smaže galerii
	 * 
	 * @param photogalleryId
	 *            galerie ke smazání
	 * @return {@code true} pokud se zdařilo smazat jinak {@code false}
	 * @return <code>false</code> pokud se nezdaŁří smazat některé soubory
	 *         (smaže tak aspoň datové struktury)
	 */
	boolean deletePhotogallery(long photogalleryId);

	/**
	 * Upraví galerii. Zpracování je prováděno v samostatném vlákně. Pro
	 * komunikaci zpět jsou použity událost {@link PGProcessStartEvent},
	 * {@link PGProcessProgressEvent} a {@link PGProcessResultEvent}
	 * 
	 * @param photogalleryId
	 *            id původní galerie
	 * @param payloadTO
	 *            obsah galerie
	 * @param date
	 *            datum vytvoření galerie, může být i <code>null</code>, pak
	 *            bude použito aktuální datum
	 */
	void modifyPhotogallery(UUID operationId, long photogalleryId, PhotogalleryPayloadTO payloadTO, LocalDateTime date);

	/**
	 * Uloží galerii. Zpracování je prováděno v samostatném vlákně. Pro
	 * komunikaci zpět jsou použity událost {@link PGProcessStartEvent},
	 * {@link PGProcessProgressEvent} a {@link PGProcessResultEvent}
	 * 
	 * @param payloadTO
	 *            obsah galerie
	 * @param nodeId
	 *            kategorie do které se vkládá
	 * @param authorId
	 *            uživatel, který galerii vytvořil
	 * @param date
	 *            datum vytvoření galerie, může být i <code>null</code>, pak
	 *            bude použito aktuální datum
	 */
	void savePhotogallery(UUID operationId, PhotogalleryPayloadTO payloadTO, long nodeId, long authorId,
			LocalDateTime date);

	/**
	 * Získá galerii dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return TO galerie
	 */
	PhotogalleryTO getPhotogalleryForDetail(Long id);

	/**
	 * Vytvoří nový adresář pro fotogalerii
	 * 
	 * @throws IOException
	 */
	String createGalleryDir() throws IOException;

	/**
	 * Získá objekt konfigurace
	 */
	PGConfiguration loadConfiguration();

	/**
	 * Uloží konfiguraci
	 */
	void storeConfiguration(PGConfiguration configuration);

	/**
	 * Získá počet galerií pro použití REST
	 * 
	 * @param userId
	 *            id přihlášeného uživatele, může být i <code>null</code>
	 * @param filter
	 *            název galerie (s *)
	 */
	int countAllPhotogalleriesForREST(Long userId, String filter);

	/**
	 * Získá všechny galerie a namapuje je pro použití REST
	 * 
	 * @param userId
	 *            id přihlášeného uživatele, může být i <code>null</code>
	 * @param filter
	 *            název galerie (s *)
	 * @param pageable
	 *            stránkování
	 */
	List<PhotogalleryRESTOverviewTO> getAllPhotogalleriesForREST(Long userId, String filter, Pageable pageable);

	/**
	 * Získá detail fotogalerie pro REST
	 * 
	 * @param id
	 *            idetifikátor galerie
	 * @return {@link UnauthorizedAccessException}
	 */
	PhotogalleryRESTTO getPhotogalleryForREST(Long id) throws UnauthorizedAccessException;

	/**
	 * Získá fotografii dle galerie pro REST
	 * 
	 * @param id
	 *            idetifikátor galerie
	 * @param fileName
	 *            jméno fotografie
	 * @param version
	 *            o jakou velikost fotky jde
	 * @return {@link UnauthorizedAccessException}
	 */
	Path getPhotoForREST(Long id, String fileName, PhotoVersion version) throws UnauthorizedAccessException;

	/**
	 * Zazipuje galerii
	 * 
	 * @param galleryDir
	 */
	void zipGallery(String galleryDir);

	/**
	 * Smaže vybrané soubory z fotogalerie.
	 * 
	 * @param selected
	 *            vybrané soubory
	 * @param galleryDir
	 *            adresář galerie
	 * @return kolekci všech položek, které se podařilo úspěšně smazat
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	List<PhotogalleryViewItemTO> deleteFiles(Set<PhotogalleryViewItemTO> selected, String galleryDir);

	/**
	 * Smaže soubor z fotogalerie.
	 * 
	 * @param itemTO
	 *            soubor
	 * @param galleryDir
	 *            adresář galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	void deleteFile(PhotogalleryViewItemTO itemTO, String galleryDir);

	/**
	 * Získá obrázek z galerie. Nemusí jít o existující galerii, proto je
	 * předáván pouze její adresář.
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @param file
	 *            jméno souboru, který má být předáván
	 * @return soubor obrázku
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný soubor podtéká kořen galerie
	 */
	Path getFullImage(String galleryDir, String file);

	/**
	 * Nahraje soubory do galerie
	 * 
	 * @param in
	 *            vstupní proud dat
	 * @param fileName
	 *            jméno souboru
	 * @param galleryDir
	 *            adresář galerie
	 * @throws IOException
	 *             pokud se nezdařilo uložení souboru
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	void uploadFile(InputStream in, String fileName, String galleryDir) throws IOException;

	/**
	 * Získá list souborů dle galerie
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @return list souborů
	 * @throws IOException
	 *             pokud se nezdařilo číst přehled adresáře galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	List<PhotogalleryViewItemTO> getItems(String galleryDir) throws IOException;

	/**
	 * Získá počet položek k zobrazení přehledu (miniatury obrázků a náhledy
	 * videí)
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @return počet položek
	 * @throws IOException
	 *             pokud se nezdařilo číst přehled adresáře galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	int getViewItemsCount(String galleryDir) throws IOException;

	/**
	 * Získá položky k zobrazení přehledu (miniatury obrázků a náhledy videí)
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @param skip
	 *            počet přeskočených položek (stránkování)
	 * @param limit
	 *            počet položek (stránkování)
	 * @return položky
	 * @throws IOException
	 *             pokud se nezdařilo číst přehled adresáře galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	List<PhotogalleryViewItemTO> getViewItems(String galleryDir, int skip, int limit) throws IOException;

	/**
	 * Ověří, že galerie existuje a má patřičné podadresáře
	 * 
	 * @param galleryDir
	 *            adresář galerie
	 * @return <code>true</code>, pokud je galerie v pořádku a připravena k
	 *         zobrazení
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG
	 */
	boolean checkGallery(String galleryDir);

	/**
	 * Smaže vygenerovaný zip soubor galerie
	 * 
	 * @param zipFile
	 *            zip soubor
	 */
	void deleteZipFile(Path zipFile);

	/**
	 * Smaže rozpracovanou galerii, která ještě nebyla uložena do DB
	 * 
	 * @param galleryDir
	 * @throws IOException
	 *             pokud se nezdařilo číst přehled adresáře galerie
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář galerií -- chyba nastavení
	 *             modulu PG
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu PG adresář galerie
	 */
	void deleteDraftGallery(String galleryDir) throws IOException;

	/**
	 * Získá fotogalerii dle jména adresáře, ke kterému galerie patří
	 * 
	 * @param directory
	 *            jméno adresáře
	 * @return overview objekt galerie
	 */
	PhotogalleryRESTOverviewTO getPhotogalleryByDirectory(String directory);

}
