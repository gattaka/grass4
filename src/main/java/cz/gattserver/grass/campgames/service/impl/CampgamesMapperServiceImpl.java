package cz.gattserver.grass.campgames.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.service.CampgamesMapperService;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.campgames.interfaces.CampgameOverviewTO;
import cz.gattserver.grass.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass.campgames.model.domain.Campgame;
import cz.gattserver.grass.campgames.model.domain.CampgameKeyword;

@Component
public class CampgamesMapperServiceImpl implements CampgamesMapperService {

	public CampgameKeywordTO mapCampgameKeyword(CampgameKeyword e) {
		if (e == null)
			return null;

		CampgameKeywordTO dto = new CampgameKeywordTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		return dto;
	}

	public CampgameKeyword mapCampgameKeyword(CampgameKeywordTO dto) {
		if (dto == null)
			return null;

		CampgameKeyword e = new CampgameKeyword();
		e.setId(dto.getId());
		e.setName(dto.getName());
		return e;
	}

	public Set<CampgameKeywordTO> mapCampgameKeywords(Collection<CampgameKeyword> list) {
		Set<CampgameKeywordTO> dtos = new LinkedHashSet<>();
		for (CampgameKeyword e : list)
			dtos.add(mapCampgameKeyword(e));
		return dtos;
	}

	public CampgameTO mapCampgame(Campgame e) {
		if (e == null)
			return null;

		CampgameTO dto = new CampgameTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setDescription(e.getDescription());
		dto.setOrigin(e.getOrigin());
		dto.setPlayers(e.getPlayers());
		dto.setPlayTime(e.getPlayTime());
		dto.setPreparationTime(e.getPreparationTime());
		Set<String> keywords = new HashSet<>();
		for (CampgameKeyword typeTO : e.getKeywords())
			keywords.add(typeTO.getName());
		dto.setKeywords(keywords);
		return dto;
	}

	public CampgameOverviewTO mapCampgameOverview(Campgame e) {
		if (e == null)
			return null;

		CampgameOverviewTO dto = new CampgameOverviewTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setPlayers(e.getPlayers());
		dto.setPlayTime(e.getPlayTime());
		dto.setPreparationTime(e.getPreparationTime());
		return dto;
	}

	public List<CampgameOverviewTO> mapCampgames(Collection<Campgame> list) {
		List<CampgameOverviewTO> dtos = new ArrayList<>();
		for (Campgame e : list)
			dtos.add(mapCampgameOverview(e));
		return dtos;
	}

}
