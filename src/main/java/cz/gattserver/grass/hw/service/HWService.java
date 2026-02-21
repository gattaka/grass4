package cz.gattserver.grass.hw.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
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

    List<HWItemFileTO> findHWItemImagesMiniFiles(Long id);

    long findHWItemImagesMiniFilesCount(Long id);

    Path findHWItemImagesFilePath(Long id, String name);

    InputStream findHWItemImagesMiniFileInputStream(Long id, String name);

    boolean deleteHWItemImagesFile(Long id, String name);

    /*
     * 3D files
     */

    void processMiniatures();

    void savePrint3dFile(InputStream in, String fileName, Long id) throws IOException;

    List<HWItemFileTO> findHWItemPrint3dFiles(Long id);

    long findHWItemPrint3dFilesCount(Long id);

    Path findHWItemPrint3dFilePath(Long id, String name);

    boolean deleteHWItemPrint3dFile(Long id, String name);

    /*
     * Documents
     */

    void saveDocumentsFile(InputStream in, String fileName, Long id) throws IOException;

    List<HWItemFileTO> findHWItemDocumentsFiles(Long id);

    long findHWItemDocumentsFilesCount(Long id);

    Path findHWItemDocumentsFilePath(Long id, String name);

    InputStream findHWItemDocumentsFileInputStream(Long id, String name);

    boolean deleteHWItemDocumentsFile(Long id, String name);

    /*
     * Icons
     */

    void createHWItemIcon(InputStream inputStream, String fileName, Long id);

    Path findHWItemIconFile(Long id) throws IOException;

    Path findHWItemIconMiniFile(Long id) throws IOException;

    InputStream findHWItemIconFileInputStream(Long id);

    InputStream findHWItemIconMiniFileInputStream(Long id);

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

    Set<HWTypeTokenTO> findAllHWTypes();

    HWTypeTO findHWType(Long id);

    List<Long> findHWTypeIds(HWTypeTO filterTO, OrderSpecifier<?>[] order);

    List<HWTypeTO> findHWTypes(HWTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order);

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

    List<HWItemOverviewTO> findAllHWItems();

    List<HWItemOverviewTO> findHWItems(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order);

    List<Long> findHWItemIds(HWFilterTO filter, OrderSpecifier<?>[] order);

    HWItemTO findHWItem(Long itemId);

    List<HWItemOverviewTO> findAllParts(Long usedInItemId);

    /**
     * Získá všechny předměty, kromě předmětu jehož id je předáno jako parametr
     *
     * @param itemId id předmětu, který má být vyloučen z přehledu
     * @return HW předměty
     */
    List<HWItemOverviewTO> findHWItemsAvailableForPart(Long itemId);

    void deleteHWItem(Long id);

    /*
     * Service notes
     */

    void saveHWItemRecord(HWItemTO hwItemTO, HWItemRecordTO itemRecordTO);

    void deleteHWItemRecord(Long id);
}