package cz.gattserver.grass.hw.model;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;

public interface HWItemRepositoryCustom {

    long countHWItems(HWFilterTO filter);

    HWItemTO findByIdAndMapForDetail(Long id);

    List<HWItemOverviewTO> findAndMap(HWFilterTO filter, Integer offset, Integer limit, OrderSpecifier<?>[] order);

    List<Long> getHWItemIds(HWFilterTO filter, OrderSpecifier<?>[] order);

}