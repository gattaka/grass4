package cz.gattserver.grass.hw.model;

import java.util.List;
import java.util.Set;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.hw.interfaces.HWTypeTokenTO;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;

public interface HWTypeRepositoryCustom {

	List<HWTypeTO> getHWTypes(HWTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	long countHWTypes(HWTypeTO filter);

    Set<HWTypeTokenTO> findOrderByName();

    HWTypeTO findByIdAndMap(Long id);

    Set<HWTypeTokenTO> findByItemId(Long itemId);

    Set<Long> findTypeIdsByItemId(Long itemId);

    List<Long> findHWTypeIds(HWTypeTO filterTO, OrderSpecifier<?>[] order);
}