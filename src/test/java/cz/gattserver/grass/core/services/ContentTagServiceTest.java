package cz.gattserver.grass.core.services;

import static org.junit.jupiter.api.Assertions.*;

import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.core.mock.CoreMockService;
import cz.gattserver.grass.core.util.MockUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ContentTagServiceTest extends DBCleanTest {

	@Autowired
	private ContentTagService contentTagService;

	@Autowired
	private ContentNodeService contentNodeService;

	@Autowired
	private CoreMockService coreMockService;

	@Test
	public void testGetTagsForOverviewOrderedByName() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagService.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = coreMockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagService.saveTags(tags, contentNode2);

		tags.add("atrapa");
		Long contentNode3 = coreMockService.createMockContentNode(33L, null, nodeId1, userId1, 3);
		contentTagService.saveTags(tags, contentNode3);

		Set<ContentTagOverviewTO> tagsTOs = contentTagService.getTagsForOverviewOrderedByName();
		assertEquals(3, tagsTOs.size());

		Iterator<ContentTagOverviewTO> iter = tagsTOs.iterator();
		assertEquals("atrapa", iter.next().getName());
		assertEquals("novinky", iter.next().getName());
		assertEquals("pokusy", iter.next().getName());
	}

	@Test
	public void testGetTagContentsCount() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagService.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = coreMockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagService.saveTags(tags, contentNode2);

		tags.add("atrapa");
		Long contentNode3 = coreMockService.createMockContentNode(33L, null, nodeId1, userId1, 3);
		contentTagService.saveTags(tags, contentNode3);

		Set<ContentTagOverviewTO> tagsTOs = contentTagService.getTagsForOverviewOrderedByName();
		assertEquals(3, tagsTOs.size());

		Iterator<ContentTagOverviewTO> iter = tagsTOs.iterator();
		assertEquals(1, contentTagService.getTagContentsCount(iter.next().getId()));
		assertEquals(3, contentTagService.getTagContentsCount(iter.next().getId()));
		assertEquals(2, contentTagService.getTagContentsCount(iter.next().getId()));
	}

	@Test
	public void testSaveTags_fail3() {
		assertThrows(NullPointerException.class, ()-> contentTagService.saveTags(new ArrayList<>(), null));
	}

	@Test
	public void testGetTagByName() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagService.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = coreMockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagService.saveTags(tags, contentNode2);

		ContentTagOverviewTO tagTO = contentTagService.getTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagService.getTagContentsCount(tagTO.getId()));

		tagTO = contentTagService.getTagByName("pokusy");
		assertEquals("pokusy", tagTO.getName());
		assertEquals(1, contentTagService.getTagContentsCount(tagTO.getId()));
	}

	@Test
	public void testGetTagByName_fail() {
		assertThrows(NullPointerException.class, ()-> contentTagService.getTagByName(null));
	}

	@Test
	public void testGetTagByName_fail2() {
		assertThrows(IllegalArgumentException.class, ()-> contentTagService.getTagByName(""));
	}

	@Test
	public void testGetTagByName_fail3() {
		assertThrows(IllegalArgumentException.class, ()-> 	contentTagService.getTagByName(" "));
	}

	@Test
	public void testGetTagById() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		tags.add("pokusy");
		coreMockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		ContentTagOverviewTO tagTO = contentTagService.getTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		tagTO = contentTagService.getTagById(tagTO.getId());
		assertEquals("novinky", tagTO.getName());

		tagTO = contentTagService.getTagByName("pokusy");
		assertEquals("pokusy", tagTO.getName());
		tagTO = contentTagService.getTagById(tagTO.getId());
		assertEquals("pokusy", tagTO.getName());
	}

	@Test
	public void testTagsDelete_bySaveTags() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagService.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = coreMockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagService.saveTags(tags, contentNode2);

		ContentTagOverviewTO tagTO = contentTagService.getTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagService.getTagContentsCount(tagTO.getId()));

		tagTO = contentTagService.getTagByName("pokusy");
		assertEquals("pokusy", tagTO.getName());
		assertEquals(1, contentTagService.getTagContentsCount(tagTO.getId()));

		tags = new HashSet<>();
		contentTagService.saveTags(tags, contentNode2);

		tagTO = contentTagService.getTagByName("novinky");
		assertEquals(1, contentTagService.getTagContentsCount(tagTO.getId()));

		tagTO = contentTagService.getTagByName("pokusy");
		assertNull(tagTO);
	}

	@Test
	public void testTagsDelete_byContentDelete() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagService.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = coreMockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagService.saveTags(tags, contentNode2);

		ContentTagOverviewTO tagTO = contentTagService.getTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagService.getTagContentsCount(tagTO.getId()));

		contentNodeService.deleteByContentId(MockUtils.MOCK_CONTENTNODE_MODULE + 2, 32L);

		tagTO = contentTagService.getTagByName("novinky");
		assertEquals(1, contentTagService.getTagContentsCount(tagTO.getId()));

		tagTO = contentTagService.getTagByName("pokusy");
		assertNull(tagTO);
	}

	@Test
	public void testTagsDelete_byContentDelete2() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("novinky");
		Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
		contentTagService.saveTags(tags, contentNode1);

		tags.add("pokusy");
		Long contentNode2 = coreMockService.createMockContentNode(32L, null, nodeId1, userId1, 2);
		contentTagService.saveTags(tags, contentNode2);

		ContentTagOverviewTO tagTO = contentTagService.getTagByName("novinky");
		assertEquals("novinky", tagTO.getName());
		assertEquals(2, contentTagService.getTagContentsCount(tagTO.getId()));

		contentNodeService.deleteByContentNodeId(contentNode2);

		tagTO = contentTagService.getTagByName("novinky");
		assertEquals(1, contentTagService.getTagContentsCount(tagTO.getId()));

		tagTO = contentTagService.getTagByName("pokusy");
		assertNull(tagTO);
	}

	@Test
	public void testGetTagsContentsCountsGroups() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		coreMockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		tags.add("tag2");
		tags.add("tag3");
		coreMockService.createMockContentNode(32L, tags, nodeId1, userId1, 2);

		tags.add("tag4");
		coreMockService.createMockContentNode(33L, tags, nodeId1, userId1, 3);
		coreMockService.createMockContentNode(34L, tags, nodeId1, userId1, 4);

		tags.add("tag5");
		coreMockService.createMockContentNode(35L, tags, nodeId1, userId1, 5);

		List<Integer> list = contentTagService.getTagsContentsCountsGroups();
		assertEquals(4, list.size());
		assertEquals(1, list.get(0).intValue());
		assertEquals(3, list.get(1).intValue());
		assertEquals(4, list.get(2).intValue());
		assertEquals(5, list.get(3).intValue());
	}

	@Test
	public void testGetTagsContentsCountsMap() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("tag1");
		coreMockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		tags.add("tag2");
		tags.add("tag3");
		coreMockService.createMockContentNode(32L, tags, nodeId1, userId1, 2);

		tags.add("tag4");
		coreMockService.createMockContentNode(33L, tags, nodeId1, userId1, 3);
		coreMockService.createMockContentNode(34L, tags, nodeId1, userId1, 4);

		tags.add("tag5");
		coreMockService.createMockContentNode(35L, tags, nodeId1, userId1, 5);

		Map<Long, Integer> map = contentTagService.getTagsContentsCountsMap();
		assertEquals(5, map.size());
		Iterator<Integer> iter = map.values().iterator();
		assertEquals(1, iter.next().intValue());
		assertEquals(3, iter.next().intValue());
		assertEquals(4, iter.next().intValue());
		assertEquals(4, iter.next().intValue());
		assertEquals(5, iter.next().intValue());
	}

	@Test
	public void testCreateTagsCloud() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("a");
		coreMockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		tags.add("d");
		tags.add("e");
		coreMockService.createMockContentNode(32L, tags, nodeId1, userId1, 2);

		tags.add("b");
		coreMockService.createMockContentNode(33L, tags, nodeId1, userId1, 3);
		coreMockService.createMockContentNode(34L, tags, nodeId1, userId1, 4);

		tags.add("c");
		coreMockService.createMockContentNode(35L, tags, nodeId1, userId1, 5);

		List<ContentTagsCloudItemTO> list = contentTagService.createTagsCloud(20, 5);
		assertEquals(5, list.size());

		Iterator<ContentTagsCloudItemTO> iter = list.iterator();
		ContentTagsCloudItemTO item = iter.next();
		assertEquals("a", item.getName());
		assertEquals(5, item.getContentsCount().intValue());
		assertEquals(20, item.getFontSize());

		item = iter.next();
		assertEquals("b", item.getName());
		assertEquals(3, item.getContentsCount().intValue());
		assertEquals(10, item.getFontSize());

		item = iter.next();
		assertEquals("c", item.getName());
		assertEquals(1, item.getContentsCount().intValue());
		assertEquals(5, item.getFontSize());

		item = iter.next();
		assertEquals("d", item.getName());
		assertEquals(4, item.getContentsCount().intValue());
		assertEquals(15, item.getFontSize());

		item = iter.next();
		assertEquals("e", item.getName());
		assertEquals(4, item.getContentsCount().intValue());
		assertEquals(15, item.getFontSize());
	}

	@Test
	public void testCreateTagsCloud_smallFontRange() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("a");
		coreMockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);
		coreMockService.createMockContentNode(33L, tags, nodeId1, userId1, 2);

		tags.add("c");
		coreMockService.createMockContentNode(34L, tags, nodeId1, userId1, 3);

		tags.add("Ab");
		coreMockService.createMockContentNode(35L, tags, nodeId1, userId1, 4);

		tags.add("aa");
		coreMockService.createMockContentNode(36L, tags, nodeId1, userId1, 5);

		List<ContentTagsCloudItemTO> list = contentTagService.createTagsCloud(7, 5);
		assertEquals(4, list.size());

		Iterator<ContentTagsCloudItemTO> iter = list.iterator();
		ContentTagsCloudItemTO item = iter.next();
		assertEquals("a", item.getName());
		assertEquals(5, item.getContentsCount().intValue());
		assertEquals(7, item.getFontSize());

		item = iter.next();
		assertEquals("aa", item.getName());
		assertEquals(1, item.getContentsCount().intValue());
		assertEquals(5, item.getFontSize());

		// Velká a malá písmena musí být brána v řazení jako stejná
		item = iter.next();
		assertEquals("Ab", item.getName());
		assertEquals(2, item.getContentsCount().intValue());
		assertEquals(6, item.getFontSize());

		item = iter.next();
		assertEquals("c", item.getName());
		assertEquals(3, item.getContentsCount().intValue());
		assertEquals(7, item.getFontSize());

	}

	@Test
	public void testCreateTagsCloud_singleElement() {
		Long userId1 = coreMockService.createMockUser(1);
		Long nodeId1 = coreMockService.createMockRootNode(1);

		Set<String> tags = new HashSet<>();
		tags.add("q");
		coreMockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

		List<ContentTagsCloudItemTO> list = contentTagService.createTagsCloud(20, 5);
		assertEquals(1, list.size());

		Iterator<ContentTagsCloudItemTO> iter = list.iterator();
		ContentTagsCloudItemTO item = iter.next();
		assertEquals("q", item.getName());
		assertEquals(1, item.getContentsCount().intValue());
		assertEquals(5, item.getFontSize());
	}

	@Test
	public void testCreateTagsCloud_empty() {
		List<ContentTagsCloudItemTO> list = contentTagService.createTagsCloud(20, 5);
		assertEquals(0, list.size());
	}

}
