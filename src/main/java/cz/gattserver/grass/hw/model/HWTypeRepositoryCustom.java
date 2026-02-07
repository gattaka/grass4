package cz.gattserver.grass.hw.model;

import java.util.List;
import java.util.Set;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.hw.interfaces.HWTypeTO;

public interface HWTypeRepositoryCustom {

	List<HWTypeTO> getHWTypes(HWTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	long countHWTypes(HWTypeTO filter);

    Set<HWTypeTO> findOrderByName();

    HWTypeTO findByName(String name);

    HWTypeTO findByIdAndMap(Long id);
}
