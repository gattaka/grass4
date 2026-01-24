package cz.gattserver.grass.campgames.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import cz.gattserver.grass.campgames.interfaces.CampgameFileTO;
import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.service.CampgamesMapperService;
import cz.gattserver.grass.core.exception.GrassException;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.campgames.CampgamesConfiguration;
import cz.gattserver.grass.campgames.interfaces.CampgameFilterTO;
import cz.gattserver.grass.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass.campgames.interfaces.CampgameOverviewTO;
import cz.gattserver.grass.campgames.model.domain.Campgame;
import cz.gattserver.grass.campgames.model.domain.CampgameKeyword;
import cz.gattserver.grass.campgames.model.repositories.CampgameKeywordRepository;
import cz.gattserver.grass.campgames.model.repositories.CampgameRepository;
import cz.gattserver.grass.campgames.service.CampgamesService;

@Transactional
@Component
public class CampgamesServiceImpl implements CampgamesService {

	private static final String ILLEGAL_PATH_IMGS_ERR = "Podtečení adresáře grafických příloh";

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private CampgameRepository campgameRepository;

	@Autowired
	private CampgameKeywordRepository campgameKeywordRepository;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private CampgamesMapperService campgamesMapper;

	/*
	 * Config
	 */

	private CampgamesConfiguration loadConfiguration() {
		CampgamesConfiguration configuration = new CampgamesConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	/**
	 * Získá {@link Path} dle jména adresáře hry
	 * 
	 * @param id
	 *            id hry
	 * @return {@link Path} adresář hry
	 * @throws IllegalStateException
	 *             pokud neexistuje kořenový adresář her -- chyba nastavení
	 *             modulu her
	 * @throws IllegalArgumentException
	 *             pokud předaný adresář podtéká kořen modulu her
	 */
	private Path getCampgamePath(Long id) {
		Validate.notNull(id, "ID hry nesmí být null");
		CampgamesConfiguration configuration = loadConfiguration();
		String rootDir = configuration.getRootDir();
		Path rootPath = fileSystemService.getFileSystem().getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new IllegalStateException("Kořenový adresář modulu her musí existovat");
		rootPath = rootPath.normalize();
		Path campgamePath = rootPath.resolve(String.valueOf(id));
		if (!campgamePath.normalize().startsWith(rootPath))
			throw new IllegalArgumentException("Podtečení kořenového adresáře modulu her");
		return campgamePath;
	}

	private Path getCampgameImagesPath(Long id) throws IOException {
		CampgamesConfiguration configuration = loadConfiguration();
		Path campgamePath = getCampgamePath(id);
		Path file = campgamePath.resolve(configuration.getImagesDir());
		if (!Files.exists(file))
			fileSystemService.createDirectoriesWithPerms(file);
		return file;
	}

	/*
	 * Images
	 */

	@Override
	public CampgameFileTO saveImagesFile(InputStream in, String fileName, Long campgameId) throws IOException {
		Path imagesPath = getCampgameImagesPath(campgameId);
		Path imagePath = imagesPath.resolve(fileName);
		if (!imagePath.normalize().startsWith(imagesPath))
			throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
		Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
		fileSystemService.grantPermissions(imagePath);
		return CampgameFileTOMapper.mapPathToItem(imagePath);
	}

	@Override
	public List<CampgameFileTO> getCampgameImagesFiles(Long id) {
		Path imagesPath;
		try {
			imagesPath = getCampgameImagesPath(id);
			List<CampgameFileTO> list = new ArrayList<>();
			try (Stream<Path> stream = Files.list(imagesPath)) {
				stream.forEach(p -> list.add(CampgameFileTOMapper.mapPathToItem(p)));
			}
			return list;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat přehled grafických příloh hry.", e);
		}
	}

	@Override
	public long getCampgameImagesFilesCount(Long id) {
		Path imagesPath;
		try {
			imagesPath = getCampgameImagesPath(id);
			try (Stream<Path> stream = Files.list(imagesPath)) {
				return stream.count();
			}
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat přehled grafických příloh hry.", e);
		}
	}

	@Override
	public Path getCampgameImagesFilePath(Long id, String name) {
		Path images;
		try {
			images = getCampgameImagesPath(id);
			Path image = images.resolve(name);
			if (!image.normalize().startsWith(images))
				throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
			return image;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat grafickou přílohu hry.", e);
		}
	}

	@Override
	public InputStream getCampgameImagesFileInputStream(Long id, String name) {
		try {
			return Files.newInputStream(getCampgameImagesFilePath(id, name));
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat grafickou přílohu hry.", e);
		}
	}

	@Override
	public boolean deleteCampgameImagesFile(Long id, String name) {
		Path images;
		try {
			images = getCampgameImagesPath(id);
			Path image = images.resolve(name);
			if (!image.normalize().startsWith(images))
				throw new IllegalArgumentException(ILLEGAL_PATH_IMGS_ERR);
			return Files.deleteIfExists(image);
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se smazat grafickou přílohu hry.", e);
		}
	}

	/*
	 * Keywords
	 */

	@Override
	public Long saveCampgameKeyword(CampgameKeywordTO campgameKeywordTO) {
		CampgameKeyword type = campgamesMapper.mapCampgameKeyword(campgameKeywordTO);
		type = campgameKeywordRepository.save(type);
		return type.getId();
	}

	@Override
	public List<String> getAllCampgameKeywordNames() {
		return campgameKeywordRepository.findNames();
	}

	@Override
	public Set<CampgameKeywordTO> getAllCampgameKeywords() {
		List<CampgameKeyword> keywords = campgameKeywordRepository.findListOrderByName();
		return campgamesMapper.mapCampgameKeywords(keywords);
	}

	@Override
	public CampgameKeywordTO getCampgameKeyword(Long id) {
		return campgamesMapper.mapCampgameKeyword(campgameKeywordRepository.findById(id).orElse(null));
	}

	@Override
	public void deleteCampgameKeyword(Long id) {
		CampgameKeyword keyword = campgameKeywordRepository.findById(id).orElse(null);
		List<Campgame> games = campgameRepository.findByKeywordsId(keyword.getId());
		for (Campgame game : games) {
			game.getKeywords().remove(keyword);
			campgameRepository.save(game);
		}
		campgameKeywordRepository.delete(keyword);
	}

	/*
	 * Items
	 */

	@Override
	public Long saveCampgame(CampgameTO gameTO) {
		Campgame item;
		if (gameTO.getId() == null)
			item = new Campgame();
		else
			item = campgameRepository.findById(gameTO.getId()).orElse(null);
		item.setName(gameTO.getName());
		item.setDescription(gameTO.getDescription());
		item.setOrigin(gameTO.getOrigin());
		item.setPlayers(gameTO.getPlayers());
		item.setPlayTime(gameTO.getPlayTime());
		item.setPreparationTime(gameTO.getPreparationTime());
		if (gameTO.getKeywords() != null) {
			item.setKeywords(new HashSet<CampgameKeyword>());
			for (String typeName : gameTO.getKeywords()) {
				CampgameKeyword type = campgameKeywordRepository.findByName(typeName);
				if (type == null) {
					type = new CampgameKeyword(typeName);
					type = campgameKeywordRepository.save(type);
				}
				item.getKeywords().add(type);
			}
		}
		return campgameRepository.save(item).getId();
	}

	@Override
	public int countCampgames(CampgameFilterTO filter) {
		return (int) campgameRepository.countCampgames(filter);
	}

	@Override
	public List<CampgameOverviewTO> getAllCampgames() {
		List<Campgame> games = campgameRepository.findAll();
		return campgamesMapper.mapCampgames(games);
	}

	@Override
	public List<CampgameOverviewTO> getCampgames(CampgameFilterTO filter, int offset, int limit,
			OrderSpecifier<?>[] order) {
		return campgamesMapper.mapCampgames(campgameRepository.getCampgames(filter, offset, limit, order));
	}

	@Override
	public List<CampgameOverviewTO> getCampgameByKeywords(Collection<String> keywords) {
		List<Campgame> campgameKeywords = campgameRepository.getCampgamesByKeywords(keywords);
		return campgamesMapper.mapCampgames(campgameKeywords);
	}

	@Override
	public CampgameTO getCampgame(Long id) {
		return campgamesMapper.mapCampgame(campgameRepository.findById(id).orElse(null));
	}

	@Override
	public void deleteCampgame(Long id) {
		Campgame item = campgameRepository.findById(id).orElse(null);

		// TODO smazat images

		campgameRepository.deleteById(item.getId());
	}

}
