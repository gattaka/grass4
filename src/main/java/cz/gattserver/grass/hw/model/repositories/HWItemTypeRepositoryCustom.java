package cz.gattserver.grass.hw.model.repositories;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass.hw.model.domain.HWItemType;

public interface HWItemTypeRepositoryCustom {

	List<HWItemTypeTO> getHWItemTypes(HWItemTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	long countHWItemTypes(HWItemTypeTO filter);
}
