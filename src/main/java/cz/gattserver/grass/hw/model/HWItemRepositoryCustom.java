package cz.gattserver.grass.hw.model;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.hw.interfaces.HWFilterTO;

public interface HWItemRepositoryCustom {

	long countHWItems(HWFilterTO filter);

	List<HWItem> getHWItems(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	List<Long> getHWItemIds(HWFilterTO filter, OrderSpecifier<?>[] order);

}