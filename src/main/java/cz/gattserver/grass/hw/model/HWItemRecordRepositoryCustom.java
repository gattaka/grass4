package cz.gattserver.grass.hw.model;

import cz.gattserver.grass.hw.interfaces.HWItemRecordTO;

import java.util.List;

public interface HWItemRecordRepositoryCustom {

    List<HWItemRecordTO> findByItemId(Long itemId);
}