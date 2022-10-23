package cz.gattserver.grass.campgames.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.campgames.interfaces.CampgameFileTO;
import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.interfaces.CampgameFilterTO;
import cz.gattserver.grass.campgames.interfaces.CampgameOverviewTO;
import cz.gattserver.grass.campgames.interfaces.CampgameKeywordTO;

public interface CampgamesService {

	/*
	 * Images
	 */

	CampgameFileTO saveImagesFile(InputStream in, String fileName, CampgameTO item) throws IOException;

	List<CampgameFileTO> getCampgameImagesFiles(Long id);

	long getCampgameImagesFilesCount(Long id);

	Path getCampgameImagesFilePath(Long id, String name);

	InputStream getCampgameImagesFileInputStream(Long id, String name);

	boolean deleteCampgameImagesFile(Long id, String name);

	/*
	 * Item types
	 */

	/**
	 * Uloží nebo aktualizuje klíčové slovo
	 * 
	 * @param to
	 *            to položky
	 * @return id uložené položky
	 */
	Long saveCampgameKeyword(CampgameKeywordTO to);

	List<String> getAllCampgameKeywordNames();

	Set<CampgameKeywordTO> getAllCampgameKeywords();

	CampgameKeywordTO getCampgameKeyword(Long fixTypeId);

	void deleteCampgameKeyword(Long id);

	/*
	 * Items
	 */

	Long saveCampgame(CampgameTO to);

	int countCampgames(CampgameFilterTO filter);

	List<CampgameOverviewTO> getAllCampgames();

	List<CampgameOverviewTO> getCampgames(CampgameFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order);

	List<CampgameOverviewTO> getCampgameByKeywords(Collection<String> types);

	CampgameTO getCampgame(Long itemId);

	void deleteCampgame(Long id);

}
