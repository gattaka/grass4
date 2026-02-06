package cz.gattserver.grass.hw.model;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.hw.interfaces.HWTypeTO;

public interface HWTypeRepositoryCustom {

	List<HWTypeTO> getHWItemTypes(HWTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	long countHWItemTypes(HWTypeTO filter);
}
