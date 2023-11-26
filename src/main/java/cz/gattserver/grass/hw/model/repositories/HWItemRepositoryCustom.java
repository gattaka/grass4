package cz.gattserver.grass.hw.model.repositories;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.model.domain.HWItem;

public interface HWItemRepositoryCustom {

	long countHWItems(HWFilterTO filter);

	List<HWItem> getHWItems(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	List<Long> getHWItemIds(HWFilterTO filter, OrderSpecifier<?>[] order);

}