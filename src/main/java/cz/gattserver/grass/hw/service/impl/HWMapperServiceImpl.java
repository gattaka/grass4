package cz.gattserver.grass.hw.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import cz.gattserver.common.util.DateUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass.hw.interfaces.HWServiceNoteTO;
import cz.gattserver.grass.hw.model.domain.HWItem;
import cz.gattserver.grass.hw.model.domain.HWItemType;
import cz.gattserver.grass.hw.model.domain.HWServiceNote;
import cz.gattserver.grass.hw.service.HWMapperService;

@Component
public class HWMapperServiceImpl implements HWMapperService {

	public HWItemTypeTO mapHWItemType(HWItemType e) {
		if (e == null)
			return null;

		HWItemTypeTO dto = new HWItemTypeTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		return dto;
	}

	public HWItemType mapHWItem(HWItemTypeTO dto) {
		if (dto == null)
			return null;

		HWItemType e = new HWItemType();
		e.setId(dto.getId());
		e.setName(dto.getName());
		return e;
	}

	public Set<HWItemTypeTO> mapHWItemTypes(Collection<HWItemType> list) {
		Set<HWItemTypeTO> dtos = new LinkedHashSet<>();
		for (HWItemType e : list)
			dtos.add(mapHWItemType(e));
		return dtos;
	}

	public HWServiceNoteTO mapServiceNote(HWServiceNote e) {
		if (e == null)
			return null;

		HWServiceNoteTO dto = new HWServiceNoteTO();
		dto.setId(e.getId());
		dto.setDate(DateUtils.toLocalDate(e.getDate()));
		dto.setDescription(e.getDescription());
		dto.setState(e.getState());
		dto.setUsedInName(e.getUsage());
		return dto;
	}

	public List<HWServiceNoteTO> mapServiceNotes(Collection<HWServiceNote> list) {
		List<HWServiceNoteTO> dtos = new ArrayList<>();
		for (HWServiceNote e : list)
			dtos.add(mapServiceNote(e));
		return dtos;
	}

	public HWItemTO mapHWItem(HWItem e) {
		if (e == null)
			return null;

		HWItemTO dto = new HWItemTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setUsedIn(mapHWItemOverview(e.getUsedIn()));
		dto.setUsedInName(e.getUsedIn() == null ? null : e.getUsedIn().getName());
		dto.setPrice(e.getPrice());
		dto.setDescription(e.getDescription());
		dto.setPurchaseDate(DateUtils.toLocalDate(e.getPurchaseDate()));
		dto.setServiceNotes(mapServiceNotes(e.getServiceNotes()));
		dto.setSupervizedFor(e.getSupervizedFor());
		dto.setState(e.getState());
		dto.setPublicItem(e.getPublicItem());
		Set<String> types = new HashSet<>();
		for (HWItemType typeTO : e.getTypes())
			types.add(typeTO.getName());
		dto.setTypes(types);
		dto.setWarrantyYears(e.getWarrantyYears());
		return dto;
	}

	public HWItemOverviewTO mapHWItemOverview(HWItem e) {
		if (e == null)
			return null;

		HWItemOverviewTO dto = new HWItemOverviewTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setUsedInName(e.getUsedIn() == null ? null : e.getUsedIn().getName());
		dto.setSupervizedFor(e.getSupervizedFor());
		dto.setPrice(e.getPrice());
		dto.setPurchaseDate(DateUtils.toLocalDate(e.getPurchaseDate()));
		dto.setState(e.getState());
		dto.setPublicItem(e.getPublicItem());
		return dto;
	}

	public List<HWItemOverviewTO> mapHWItems(Collection<HWItem> list) {
		List<HWItemOverviewTO> dtos = new ArrayList<>();
		for (HWItem e : list)
			dtos.add(mapHWItemOverview(e));
		return dtos;
	}

}
