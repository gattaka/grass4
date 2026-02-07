package cz.gattserver.grass.hw.model;

import java.util.Collection;
import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTO;

public interface HWItemRepositoryCustom {

	long countHWItems(HWFilterTO filter);

    HWItemTO findByIdAndMapForDetail(Long id);

    HWItemOverviewTO findByIdAndMap(Long id);

	List<HWItemOverviewTO> findAndMap(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	List<Long> getHWItemIds(HWFilterTO filter, OrderSpecifier<?>[] order);

    List<HWItemOverviewTO> findByTypesId(Long id);

    List<HWItemOverviewTO> findByUsedInId(Long id);

    List<HWItemOverviewTO> getHWItemsByTypes(Collection<String> types);

    List<HWItemOverviewTO> findAllExcept(Long itemId);

    List<HWItemOverviewTO> findAndMap();
}