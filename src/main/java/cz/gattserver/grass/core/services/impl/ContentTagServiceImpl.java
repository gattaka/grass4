package cz.gattserver.grass.core.services.impl;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass.core.model.domain.ContentNodeContentTag;
import cz.gattserver.grass.core.model.repositories.ContentNodeContentTagRepository;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.services.CoreMapperService;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass.core.model.domain.ContentTag;
import cz.gattserver.grass.core.model.repositories.ContentTagRepository;

@Transactional
@Service
public class ContentTagServiceImpl implements ContentTagService {

    private final CoreMapperService mapper;
    private final ContentTagRepository contentTagRepository;
    private final ContentNodeContentTagRepository contentNodeContentTagRepository;

    public ContentTagServiceImpl(CoreMapperService mapper, ContentTagRepository contentTagRepository,
                                 ContentNodeContentTagRepository contentNodeContentTagRepository) {
        this.mapper = mapper;
        this.contentTagRepository = contentTagRepository;
        this.contentNodeContentTagRepository = contentNodeContentTagRepository;
    }

    @Override
    public Set<ContentTagTO> getTagsForOverviewOrderedByName() {
        List<ContentTag> contentTags = contentTagRepository.findAllOrderByNameCaseInsensitive();
        return mapper.mapContentTagCollectionForOverview(contentTags);
    }

    @Override
    public ContentTagTO getTagById(long id) {
        return mapper.mapContentTagForOverview(contentTagRepository.findById(id).orElse(null));
    }

    @Override
    public ContentTagTO getTagByName(String name) {
        Validate.notBlank(name, "Název hledaného tagu nemůže být prázdný");
        return mapper.mapContentTagForOverview(contentTagRepository.findByName(name));
    }

    @Override
    public void saveTags(@NotNull Collection<String> tags, @NotNull Long contentNodeId) {
        Objects.requireNonNull(contentNodeId);
        Objects.requireNonNull(tags);

        Set<ContentTag> set = contentNodeContentTagRepository.findByContendNodeId(contentNodeId);
        Map<String, ContentTag> nameToTO = set.stream().collect(Collectors.toMap(ContentTag::getName, to -> to));
        Set<String> toRemove = set.stream().map(ContentTag::getName).collect(Collectors.toSet());

        // nové vazby
        List<ContentNodeContentTag> contentNodeContentTags = new ArrayList<>();

        // tagy, které které jsou použity/vytvořeny
        for (String tag : tags) {
            // existuje už takový tag ?
            ContentTag contentTag = contentTagRepository.findByName(tag);
            if (contentTag == null) {
                contentTag = new ContentTag();
                contentTag.setName(tag);
            }
            contentTag.setContentNodeCount(contentTag.getContentNodeCount() + 1);
            // potřebuju jeho Id, takže není možné udělat batch save později
            contentTag = contentTagRepository.save(contentTag);

            if (!nameToTO.containsKey(tag)) {
                // obsah aktuálně u sebe nemá tento tag -- je potřeba vytvořit vazbu
                contentNodeContentTags.add(new ContentNodeContentTag(contentNodeId, contentTag.getId()));
            } else {
                // tag je stále používán, odeber ho ze seznamu tagů ke zrušení vazby
                toRemove.remove(tag);
            }
        }

        if (!contentNodeContentTags.isEmpty()) contentNodeContentTagRepository.saveAll(contentNodeContentTags);

        removeTagRelation(contentNodeId, nameToTO.values().stream().filter(e->toRemove.contains(e.getName())).collect(
                Collectors.toSet()));
    }

    @Override
    public void onContentNodeDelete(@NotNull Long contentNodeId) {
        Objects.requireNonNull(contentNodeId);
        removeTagRelation(contentNodeId, contentNodeContentTagRepository.findByContendNodeId(contentNodeId));
    }

    private void removeTagRelation(Long contentNodeId, Set<ContentTag> removedTags) {
        for (ContentTag contentTag : removedTags) {
            contentTag.setContentNodeCount(contentTag.getContentNodeCount() - 1);
            contentTagRepository.save(contentTag);
            contentNodeContentTagRepository.delete(new ContentNodeContentTag(contentNodeId, contentTag.getId()));
        }

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
            map.put((Long) to[0], ((Long) to[1]).intValue());
        return map;
    }

    @Override
    public List<Integer> getTagsContentsCountsGroups() {
        List<Integer> list = new ArrayList<>();
        contentTagRepository.findContentNodesCountsGroups().forEach(i -> list.add(i));
        return list;
    }

    @Override
    public List<ContentTagsCloudItemTO> createTagsCloud(int maxFontSize, int minFontSize) {
        // Pro škálování je potřeba znát počty obsahů ze všech tagů
        Map<Long, Integer> countsMap = getTagsContentsCountsMap();
        if (countsMap.isEmpty()) return new ArrayList<>();

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
        if (fontSizeStep == 0) fontSizeStep = 1;

        // Údaj o poslední příčce a velikosti, která jí odpovídala
        int lastCountGroup = countsGroups.getFirst();
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
                if (lastFontSize + fontSizeStep <= maxFontSize) lastFontSize += fontSizeStep;
            }

            fontSizeByCountsGroupMap.put(tagContentsCount, lastFontSize);
        }

        List<ContentTagsCloudItemTO> itemslist = new ArrayList<>();

        // Vytáhni si tagy seřazené dle jména a dokonči vytváření datové sady
        // pro tags cloud
        Set<ContentTagTO> tags = getTagsForOverviewOrderedByName();
        for (ContentTagTO tag : tags) {
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
    public List<String> findByFilter(Optional<String> filter, int offset, int limit) {
        return contentTagRepository.findByFilter(filter, offset, limit);
    }

    @Override
    public Integer countByFilter(Optional<String> filter) {
        return contentTagRepository.countByFilter(filter);
    }

}
