package cz.gattserver.grass.core.services;

import static org.junit.jupiter.api.Assertions.*;

import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.mock.CoreMockService;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.core.util.MockUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContentNodeServiceTest extends DBCleanTest {

    @Autowired
    private ContentNodeService contentNodeService;

    @Autowired
    private ContentTagService contentTagService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoreMockService coreMockService;

    @Test
    public void testGetRecentAdded() {
        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);

        Long contentNodeId1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        Long contentNodeId2 = coreMockService.createMockContentNode(31L, null, nodeId1, userId1, 2);

        List<ContentNodeOverviewTO> added = contentNodeService.getRecentAdded(0, 10);
        assertEquals(2, added.size());

        ContentNodeOverviewTO added2 = added.get(0);
        assertEquals(contentNodeId2, added2.id());

        ContentNodeOverviewTO added1 = added.get(1);
        assertEquals(contentNodeId1, added1.id());
    }

    @Test
    public void testGetRecentModified() {
        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);

        Long contentNodeId1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        Long contentNodeId2 = coreMockService.createMockContentNode(31L, null, nodeId1, userId1, 2);

        contentNodeService.modify(contentNodeId1, "newName", true);

        List<ContentNodeOverviewTO> added = contentNodeService.getRecentModified(0, 10);
        assertEquals(2, added.size());

        ContentNodeOverviewTO added1 = added.get(0);
        assertEquals(contentNodeId1, added1.id());

        ContentNodeOverviewTO added2 = added.get(1);
        assertEquals(contentNodeId2, added2.id());
    }

    @Test
    public void testGetUserFavourite() {
        Long userId1 = coreMockService.createMockUser(1);
        Long userId2 = coreMockService.createMockUser(2);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        coreMockService.createMockContentNode(31L, null, nodeId1, userId1, 2);

        userService.addContentToFavourites(contentNode1, userId1);

        assertEquals(1, contentNodeService.getUserFavouriteCount(userId1));
        assertEquals(0, contentNodeService.getUserFavouriteCount(userId2));

        List<ContentNodeOverviewTO> favourites = contentNodeService.getUserFavourite(userId1, 0, 10);
        assertEquals(1, favourites.size());
        assertEquals(contentNode1, favourites.get(0).id());
    }

    @Test
    public void testModify() {
        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);

        Set<String> tags = new HashSet<>();
        tags.add("novinky");
        tags.add("pokusy");
        Long contentNode1 = coreMockService.createMockContentNode(30L, tags, nodeId1, userId1, 1);

        tags = new HashSet<>();
        tags.add("new1");
        tags.add("new2");

        contentNodeService.modify(contentNode1, "newNameAfterModify", tags, false,
                LocalDateTime.of(1980, 2, 3, 10, 15));
        ContentNodeTO contentNode = contentNodeService.getByID(contentNode1);

        assertEquals("newNameAfterModify", contentNode.name());
        for (ContentTagTO t : contentNode.contentTags())
            tags.remove(t.getName());
        assertTrue(tags.isEmpty());
        assertEquals(Long.valueOf(30L), contentNode.getContentID());
        assertEquals(userId1, contentNode.getAuthor().getId());
        assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 1, contentNode.getContentReaderID());
        assertEquals(LocalDateTime.of(1980, 2, 3, 10, 15), contentNode.creationDate());
        assertNotNull(contentNode.lastModificationDate());
        assertFalse(contentNode.publicated());
    }

    @Test
    public void testModify_fail() {
        assertThrows(NullPointerException.class, () -> contentNodeService.modify(4, null, new HashSet<>(), false, LocalDateTime.of(1980, 2, 3, 10, 15)));
    }

    @Test
    public void testMoveContent() {
        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        Long nodeId2 = coreMockService.createMockRootNode(2);
        Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        coreMockService.createMockContentNode(32L, null, nodeId2, userId1, 1);

        assertEquals(1, contentNodeService.getCountByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId1)));
        assertEquals(1, contentNodeService.getCountByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId2)));

        contentNodeService.moveContent(nodeId2, contentNode1);

        assertEquals(0, contentNodeService.getCountByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId1)));
        assertEquals(2, contentNodeService.getCountByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId2)));
    }

    @Test
    public void testDeleteByContentNodeId() {
        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);

        assertEquals(0, contentNodeService.getCount());

        Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        Long contentNode2 = coreMockService.createMockContentNode(31L, null, nodeId1, userId1, 2);
        Long contentNode3 = coreMockService.createMockContentNode(32L, null, nodeId1, userId1, 3);

        assertEquals(3, contentNodeService.getCount());

        contentNodeService.deleteByContentNodeId(contentNode2);

        assertEquals(2, contentNodeService.getCount());

        assertNotNull(contentNodeService.getByID(contentNode1));
        assertNull(contentNodeService.getByID(contentNode2));
        assertNotNull(contentNodeService.getByID(contentNode3));
    }

    @Test
    public void testDeleteByContentId() {
        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);

        assertEquals(0, contentNodeService.getCount());

        Long contentNode1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        Long contentNode2 = coreMockService.createMockContentNode(31L, null, nodeId1, userId1, 2);
        Long contentNode3 = coreMockService.createMockContentNode(32L, null, nodeId1, userId1, 3);

        assertEquals(3, contentNodeService.getCount());

        contentNodeService.deleteByContentId(MockUtils.MOCK_CONTENTNODE_MODULE + 2, 31L);

        assertEquals(2, contentNodeService.getCount());

        assertNotNull(contentNodeService.getByID(contentNode1));
        assertNull(contentNodeService.getByID(contentNode2));
        assertNotNull(contentNodeService.getByID(contentNode3));
    }

    @Test
    public void testDeleteByContentId_fail() {
        assertThrows(IllegalStateException.class, () -> contentNodeService.deleteByContentId("noModule", 999999L));
    }

    @Test
    public void testSave_GetByID_withTags() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);

        Set<String> tags = new HashSet<>();
        tags.add("novinky");
        tags.add("pokusy");
        tags.add("testy");
        tags.add("ŘěŇšb test diakritiky");

        String moduleId = "mockModule";
        Long contentId = 2L;
        String name = "Test obsah";
        Long contentNodeId = contentNodeService.save(moduleId, contentId, name, tags, true, nodeId, userId, false,
                LocalDateTime.now(), null);

        assertEquals(1, contentNodeService.getCount());
        ContentNodeTO contentNodeByID = contentNodeService.getByID(contentNodeId);
        assertNotNull(contentNodeByID);
        assertEquals(moduleId, contentNodeByID.getContentReaderID());
        assertEquals(contentId, contentNodeByID.getContentID());
        for (ContentTagTO t : contentNodeByID.contentTags())
            tags.remove(t.getName());
        assertTrue(tags.isEmpty());
        assertEquals(name, contentNodeByID.name());
        assertEquals(userId, contentNodeByID.getAuthor().getId());
        assertEquals(nodeId, contentNodeByID.getParent().getId());
        assertNotNull(contentNodeByID.creationDate());
    }

    @Test
    public void testSave_GetByID_withoutTags() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);

        String moduleId = "mockModule";
        Long contentId = 2L;
        String name = "Test obsah";
        Long contentNodeId = contentNodeService.save(moduleId, contentId, name, null, true, nodeId, userId, false, null,
                null);

        assertEquals(1, contentNodeService.getCount());
        ContentNodeTO contentNode = contentNodeService.getByID(contentNodeId);
        assertNotNull(contentNode);
        assertNotNull(contentNode.contentTags());
        assertTrue(contentNode.contentTags().isEmpty());
        assertEquals(moduleId, contentNode.getContentReaderID());
        assertEquals(contentId, contentNode.getContentID());
        assertEquals(name, contentNode.name());
        assertEquals(userId, contentNode.getAuthor().getId());
        assertEquals(nodeId, contentNode.getParent().getId());
        assertNotNull(contentNode.creationDate());
    }

    @Test
    public void testSave_withoutContentModuleId() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);
        assertThrows(NullPointerException.class, () -> contentNodeService.save(null, 2L, "Test obsah", null, true, nodeId, userId, false, null, null));
    }

    @Test
    public void testSave_withoutName() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);
        assertThrows(NullPointerException.class, () -> contentNodeService.save("testModule", 2L, null, null, true, nodeId, userId, false, null, null));
    }

    @Test
    public void testGetByNode() {
        assertEquals(0, contentNodeService.getCount());

        Long userId1 = coreMockService.createMockUser(1);
        Long userId2 = coreMockService.createMockUser(2);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        Long nodeId2 = coreMockService.createMockRootNode(2);

        Set<String> tags = new HashSet<>();

        coreMockService.createMockContentNode(20L, tags, nodeId1, userId1, 1);
        Long contentNode2 = coreMockService.createMockContentNode(30L, tags, nodeId2, userId1, 2);
        Long contentNode3 = coreMockService.createMockContentNode(25L, tags, nodeId2, userId2, 3);

        assertEquals(1, contentNodeService.getCountByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId1)));
        assertEquals(2, contentNodeService.getCountByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId2)));

        List<ContentNodeOverviewTO> contentNodesByNode = contentNodeService
                .getByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId2), 0, 10);
        assertEquals(2, contentNodesByNode.size());

        ContentNodeOverviewTO contentNodeByNode = contentNodesByNode.get(0);
        assertEquals(contentNode3, contentNodeByNode.id());
        assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 3, contentNodeByNode.contentReaderID());
        assertEquals(Long.valueOf(25), contentNodeByNode.contentID());
        assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 3, contentNodeByNode.name());
        assertEquals(userId2, contentNodeByNode.authorId());
        assertEquals(nodeId2, contentNodeByNode.parentNodeId());

        contentNodeByNode = contentNodesByNode.get(1);
        assertEquals(contentNode2, contentNodeByNode.id());
        assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 2, contentNodeByNode.contentReaderID());
        assertEquals(Long.valueOf(30L), contentNodeByNode.contentID());
        assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 2, contentNodeByNode.name());
        assertEquals(userId1, contentNodeByNode.authorId());
        assertEquals(nodeId2, contentNodeByNode.parentNodeId());

        assertEquals(3, contentNodeService.getCount());
    }

    @Test
    public void testGetByTag() {
        assertEquals(0, contentNodeService.getCount());

        Long userId1 = coreMockService.createMockUser(1);
        Long userId2 = coreMockService.createMockUser(2);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        Long nodeId2 = coreMockService.createMockRootNode(2);

        Set<String> tags = new HashSet<>();
        tags.add("novinky");
        tags.add("pokusy");

        coreMockService.createMockContentNode(20L, tags, nodeId1, userId1, 1);
        coreMockService.createMockContentNode(30L, tags, nodeId2, userId1, 2);

        tags.add("něco");

        Long contentNode3 = coreMockService.createMockContentNode(25L, tags, nodeId2, userId2, 3);

        ContentTagTO tag = contentTagService.getTagByName("něco");
        assertNotNull(tag);
        assertEquals("něco", tag.getName());
        assertEquals(1, contentTagService.getTagContentsCount(tag.getId()));
        assertEquals(1, contentNodeService.getCountByTag(tag.getId()));

        List<ContentNodeOverviewTO> contentNodesByTag = contentNodeService.getByTag(tag.getId(), 0, 10);
        assertEquals(1, contentNodesByTag.size());
        ContentNodeOverviewTO contentNodeByTag = contentNodesByTag.get(0);
        assertEquals(contentNode3, contentNodeByTag.id());
        assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 3, contentNodeByTag.contentReaderID());
        assertEquals(Long.valueOf(25L), contentNodeByTag.contentID());
        assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 3, contentNodeByTag.name());
        assertEquals(userId2, contentNodeByTag.authorId());
        assertEquals(nodeId2, contentNodeByTag.parentNodeId());

        tag = contentTagService.getTagByName("pokusy");
        assertNotNull(tag);
        assertEquals("pokusy", tag.getName());
        assertEquals(3, contentTagService.getTagContentsCount(tag.getId()));
        assertEquals(3, contentNodeService.getCountByTag(tag.getId()));

        assertEquals(3, contentNodeService.getCount());
    }
}