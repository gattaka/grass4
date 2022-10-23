package cz.gattserver.grass.core.services.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.services.CoreMapperService;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.model.domain.ContentTag;
import cz.gattserver.grass.core.model.repositories.ContentNodeRepository;
import cz.gattserver.grass.core.model.repositories.ContentTagRepository;

@Transactional
@Service
public class ContentTagServiceImpl implements ContentTagService {

	@Autowired
	private CoreMapperService mapper;

	@Autowired
	private ContentTagRepository contentTagRepository;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	@Override
	public Set<ContentTagOverviewTO> getTagsForOverviewOrderedByName() {
		List<ContentTag> contentTags = contentTagRepository.findAllOrderByNameCaseInsensitive();
		return mapper.mapContentTagCollectionForOverview(contentTags);
	}

	@Override
	public ContentTagOverviewTO getTagById(long id) {
		return mapper.mapContentTagForOverview(contentTagRepository.findById(id).orElse(null));
	}

	@Override
	public ContentTagOverviewTO getTagByName(String name) {
		Validate.notBlank(name, "Název hledaného tagu nemůže být prázdný");
		return mapper.mapContentTagForOverview(contentTagRepository.findByName(name));
	}

	@Override
	public void saveTags(Collection<String> tags, long contentNodeId) {
		saveTags(tags, contentNodeRepository.findById(contentNodeId).orElse(null));
	}

	@Override
	public void saveTags(Collection<String> tags, ContentNode contentNode) {
		Validate.notNull(contentNode, "'contentNode' nemůže být null");
		// tagy, které které jsou použity/vytvořeny
		Set<ContentTag> tagsEntities = new HashSet<>();
		if (tags != null) {
			for (String tag : tags) {
				// existuje už takový tag ?
				ContentTag contentTag = contentTagRepository.findByName(tag);
				if (contentTag == null) {
					contentTag = new ContentTag();
					contentTag.setName(tag);
					contentTagRepository.save(contentTag);
				}
				tagsEntities.add(contentTag);
			}
		}

		// Nahraď stávající kolekci tagů novou kolekcí
		contentNode.setContentTags(tagsEntities);
		contentNodeRepository.save(contentNode);

		// Vyčisti DB od nepoužívaných tagů
		contentTagRepository.deleteUnusedTags();
	}

	@Override
	public int getTagContentsCount(long tagId) {
		return contentTagRepository.countContentTagContents(tagId);
	}

	@Override
	public Map<Long, Integer> getTagsContentsCountsMap() {
		Map<Long, Integer> map = new LinkedHashMap<>();
		for (Object[] to : contentTagRepository.countContentTagsContents())
			map.put(((BigInteger) to[0]).longValue(), ((BigInteger) to[1]).intValue());
		return map;
	}

	@Override
	public List<Integer> getTagsContentsCountsGroups() {
		List<Integer> list = new ArrayList<>();
		contentTagRepository.findContentNodesCountsGroups().forEach(i -> list.add(((BigInteger) i).intValue()));
		return list;
	}

	@Override
	public List<ContentTagsCloudItemTO> createTagsCloud(int maxFontSize, int minFontSize) {
		// Pro škálování je potřeba znát počty obsahů ze všech tagů
		Map<Long, Integer> countsMap = getTagsContentsCountsMap();
		if (countsMap.isEmpty())
			return new ArrayList<>();

		// Skupiny počtů -- je potřeba vědět, jaké součty existují, aby se dle
		// nich nastavily velikosti písma. Nemusí existovat všechny skupiny,
		// například žádný tag nemusí mít přesně 12 obsahů, takže je zbytečné
		// pro 12 počítat velikost, další velikostí v pořadí počtů může být
		// třeba až 17
		List<Integer> countsGroups = getTagsContentsCountsGroups();

		// Rozděl rozmezí velikosti fontů na tolik dílů, kolik je skupin - 1
		// protože poslední skupina má rovnou nejnižší velikost fontu
		int scale = maxFontSize - minFontSize;
		int parts = countsGroups.size() - 1;
		int fontSizeStep = parts == 0 ? 1 : scale / parts;
		if (fontSizeStep == 0)
			fontSizeStep = 1;

		// Údaj o poslední příčce a velikosti, která jí odpovídala
		int lastCountGroup = countsGroups.get(0);
		int lastFontSize = minFontSize;

		// Potřebuju aby bylo možné nějak zavolat svůj počet obsahů a zpátky se
		// vrátila velikost fontu, reps. kategorie velikosti.
		Map<Integer, Integer> fontSizeByCountsGroupMap = new HashMap<>();
		for (Entry<Long, Integer> entry : countsMap.entrySet()) {
			// Spočítej jeho fontsize - pokud jsem vyšší, pak přihoď velikost
			// dle vypočteného přírůstku a ulož můj stav aby ostatní věděli,
			// jestli mají zvyšovat nebo zůstat, protože mají stejnou velikost
			int tagContentsCount = entry.getValue();
			if (tagContentsCount > lastCountGroup) {
				lastCountGroup = tagContentsCount;
				if (lastFontSize + fontSizeStep <= maxFontSize)
					lastFontSize += fontSizeStep;
			}

			int size = tagContentsCount;
			fontSizeByCountsGroupMap.put(size, lastFontSize);
		}

		List<ContentTagsCloudItemTO> itemslist = new ArrayList<>();

		// Vytáhni si tagy seřazené dle jména a dokonči vytváření datové sady
		// pro tags cloud
		Set<ContentTagOverviewTO> tags = getTagsForOverviewOrderedByName();
		for (ContentTagOverviewTO tag : tags) {
			ContentTagsCloudItemTO item = new ContentTagsCloudItemTO();
			item.setId(tag.getId());
			item.setContentsCount(countsMap.get(item.getId()));
			item.setFontSize(fontSizeByCountsGroupMap.get(item.getContentsCount()));
			item.setName(tag.getName());
			itemslist.add(item);
		}

		return itemslist;
	}

	@Override
	public List<String> findByFilter(String filter, int offset, int limit) {
		return contentTagRepository.findByFilter(filter, offset, limit);
	}

	@Override
	public Integer countByFilter(String filter) {
		return contentTagRepository.countByFilter(filter);
	}

}
