package cz.gattserver.grass.hw.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.hw.interfaces.*;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;

public interface HWService {

    /*
     * Images
     */

    void saveImagesFile(InputStream in, String fileName, HWItemTO item) throws IOException;

    List<HWItemFileTO> getHWItemImagesMiniFiles(Long id);

    long getHWItemImagesMiniFilesCount(Long id);

    Path getHWItemImagesFilePath(Long id, String name);

    InputStream getHWItemImagesMiniFileInputStream(Long id, String name);

    boolean deleteHWItemImagesFile(Long id, String name);

    /*
     * 3D files
     */

    void processMiniatures();

    void savePrint3dFile(InputStream in, String fileName, Long id) throws IOException;

    List<HWItemFileTO> getHWItemPrint3dFiles(Long id);

    long getHWItemPrint3dFilesCount(Long id);

    Path getHWItemPrint3dFilePath(Long id, String name);

    InputStream getHWItemPrint3dFileInputStream(Long id, String name);

    boolean deleteHWItemPrint3dFile(Long id, String name);

    /*
     * Documents
     */

    void saveDocumentsFile(InputStream in, String fileName, Long id) throws IOException;

    List<HWItemFileTO> getHWItemDocumentsFiles(Long id);

    long getHWItemDocumentsFilesCount(Long id);

    Path getHWItemDocumentsFilePath(Long id, String name);

    InputStream getHWItemDocumentsFileInputStream(Long id, String name);

    boolean deleteHWItemDocumentsFile(Long id, String name);

    /*
     * Icons
     */

    void createHWItemIcon(InputStream inputStream, String fileName, Long id);

    Path getHWItemIconFile(Long id) throws IOException;

    Path getHWItemIconMiniFile(Long id) throws IOException;

    InputStream getHWItemIconFileInputStream(Long id);

    InputStream getHWItemIconMiniFileInputStream(Long id);

    boolean deleteHWItemIconFile(Long id);

    /*
     * Item types
     */

    /**
     * Uloží nebo aktualizuje typ hw položky
     *
     * @param hwTypeTO to položky
     * @return id uložené položky
     */
    Long saveHWType(HWTypeTO hwTypeTO);

    Set<HWTypeBasicTO> getAllHWTypes();

    HWTypeTO getHWType(Long fixTypeId);

    List<Long> getHWTypeIds(HWTypeTO filterTO, OrderSpecifier<?>[] order);

    List<HWTypeTO> getHWTypes(HWTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order);

    int countHWTypes(HWTypeTO filter);

    void deleteHWType(Long id);

    /*
     * Items
     */

    /**
     * Vytvoří kopii základu položky (mimo záznamů a součástí)
     *
     * @param itemId id předmětu, který má být vyloučen z přehledu
     */
    Long copyHWItem(Long itemId);

    Long saveHWItem(HWItemTO hwItemDTO);

    int countHWItems(HWFilterTO filter);

    List<HWItemOverviewTO> getAllHWItems();

    List<HWItemOverviewTO> getHWItems(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order);

    List<Long> getHWItemIds(HWFilterTO filter, OrderSpecifier<?>[] order);

    HWItemTO getHWItem(Long itemId);

    HWItemOverviewTO getHWOverviewItem(Long itemId);

    List<HWItemOverviewTO> getAllParts(Long usedInItemId);

    /**
     * Získá všechny předměty, kromě předmětu jehož id je předáno jako parametr
     *
     * @param itemId id předmětu, který má být vyloučen z přehledu
     * @return HW předměty
     */
    List<HWItemOverviewTO> getHWItemsAvailableForPart(Long itemId);

    void deleteHWItem(Long id);

    /*
     * Service notes
     */

    void saveHWItemRecord(HWItemTO hwItemTO, HWItemRecordTO itemRecordTO);

    void deleteHWItemRecord(Long id);
}