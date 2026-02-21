package cz.gattserver.grass.print3d.service;

import cz.gattserver.grass.print3d.config.Print3dConfiguration;
import cz.gattserver.grass.print3d.interfaces.Print3dCreateTO;
import cz.gattserver.grass.print3d.interfaces.Print3dTO;
import cz.gattserver.grass.print3d.interfaces.Print3dViewItemTO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface Print3dService {

    /**
     * Smaže projekt
     *
     * @param projectId projekt ke smazání
     * @return {@code true} pokud se zdařilo smazat jinak {@code false} -- smaže
     * tak aspoň datové struktury)
     */
    boolean deleteProject(long projectId);

    /**
     * Upraví projekt.
     *
     * @param projectId id projektu
     * @param payloadTO obsah projektu
     */
    void modifyProject(long projectId, Print3dCreateTO payloadTO);

    /**
     * Uloží projekt.
     *
     * @param payloadTO obsah projektu
     * @param nodeId    kategorie do které se vkládá
     * @param authorId  uživatel, který projekt vytvořil
     * @return id projektu
     */
    Long saveProject(Print3dCreateTO payloadTO, long nodeId, long authorId);

    /**
     * Získá projekt dle jeho identifikátoru
     *
     * @param id identifikátor
     * @return TO projektu
     */
    Print3dTO getProjectForDetail(Long id);

    /**
     * Vytvoří nový adresář pro projekt
     *
     * @throws IOException při systémové chybě
     */
    String createProjectDir() throws IOException;

    /**
     * Získá objekt konfigurace
     */
    Print3dConfiguration loadConfiguration();

    /**
     * Uloží konfiguraci
     */
    void storeConfiguration(Print3dConfiguration configuration);

    /**
     * Zazipuje projekt
     *
     * @param projectDir adresář projektu
     */
    void zipProject(String projectDir);

    /**
     * Smaže soubor z projektu.
     *
     * @param item       soubor
     * @param projectDir adresář projektu
     * @throws IllegalStateException    pokud neexistuje kořenový adresář projektů -- chyba nastavení
     *                                  modulu Print3d
     * @throws IllegalArgumentException pokud předaný adresář podtéká kořen modulu Print3d
     */
    void deleteFile(Print3dViewItemTO item, String projectDir);

    /**
     * Nahraje soubory do projektu
     *
     * @param in         vstupní proud dat
     * @param fileName   jméno souboru
     * @param projectDir adresář projektu
     * @return {@link Path} k souboru
     * @throws IOException pokud se nezdařilo uložení souboru
     */
    Path uploadFile(InputStream in, String fileName, String projectDir) throws IOException;

    Print3dViewItemTO constructViewItemTO(String name, Path file);

    /**
     * Získá list souborů dle projektu
     *
     * @param projectDir adresář projektu
     * @return list souborů
     */
    List<Print3dViewItemTO> getItems(String projectDir);

    /**
     * Smaže vygenerovaný zip soubor
     *
     * @param zipFile zip soubor
     */
    void deleteZipFile(Path zipFile);

    /**
     * Ověří, že projekt existuje
     *
     * @param projectDir adresář projektu
     * @return <code>true</code>, pokud je projekt v pořádku a připravena k
     * zobrazení
     * @throws IllegalStateException    pokud neexistuje kořenový adresář projektů -- chyba nastavení
     *                                  modulu Print3d
     * @throws IllegalArgumentException pokud předaný adresář podtéká kořen modulu Print3d
     */
    boolean checkProject(String projectDir);

}
