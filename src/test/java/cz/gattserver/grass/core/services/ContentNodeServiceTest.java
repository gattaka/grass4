package cz.gattserver.grass.core.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.mock.CoreMockService;
import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.model.domain.Node;
import cz.gattserver.grass.core.model.repositories.ContentNodeRepository;
import cz.gattserver.grass.core.model.repositories.NodeRepository;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.impl.LoginResult;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.core.util.MockUtils;
import cz.gattserver.grass.test.MockSecurityService;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.*;

public class ContentNodeServiceTest extends DBCleanTest {

    @Autowired
    private ContentNodeService contentNodeService;

    @Autowired
    private ContentNodeRepository contentNodeRepository;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private ContentTagService contentTagService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoreMockService coreMockService;

    @Autowired
    private MockSecurityService mockSecurityService;

    @BeforeEach
    public void setup() {
        mockSecurityService.reset();
    }

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

        // hidden by parent varianta

        Long nodeId2 = coreMockService.createMockRootNode(3, true);
        nodeService.moveNode(nodeId1, nodeId2);

        added = contentNodeService.getRecentAdded(0, 10);
        assertEquals(0, added.size());

        mockSecurityService.setRoles(new HashSet<>(List.of(CoreRole.ADMIN)));

        added = contentNodeService.getRecentAdded(0, 10);
        assertEquals(2, added.size());
    }

    @Test
    public void testGetRecentModified() {
        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);

        Long contentNodeId1 = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        Long contentNodeId2 = coreMockService.createMockContentNode(31L, null, nodeId1, userId1, 2);

        contentNodeService.modify(contentNodeId1, "newName", false);

        List<ContentNodeOverviewTO> added = contentNodeService.getRecentModified(0, 10);
        assertEquals(2, added.size());

        ContentNodeOverviewTO added1 = added.get(0);
        assertEquals(contentNodeId1, added1.id());

        ContentNodeOverviewTO added2 = added.get(1);
        assertEquals(contentNodeId2, added2.id());

        // hidden by parent varianta

        Long nodeId2 = coreMockService.createMockRootNode(3, true);
        nodeService.moveNode(nodeId1, nodeId2);

        added = contentNodeService.getRecentModified(0, 10);
        assertEquals(0, added.size());

        mockSecurityService.setRoles(new HashSet<>(List.of(CoreRole.ADMIN)));

        added = contentNodeService.getRecentModified(0, 10);
        assertEquals(2, added.size());
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
        assertEquals(contentNode1, favourites.getFirst().id());
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
        ContentNodeTO contentNode = contentNodeService.getById(contentNode1);

        assertEquals("newNameAfterModify", contentNode.getName());
        for (ContentTagTO t : contentNode.getContentTags())
            tags.remove(t.getName());
        assertTrue(tags.isEmpty());
        assertEquals(Long.valueOf(30L), contentNode.getContentNodeId());
        assertEquals(userId1, contentNode.getAuthorId());
        assertEquals(MockUtils.MOCK_CONTENTNODE_MODULE + 1, contentNode.getContentReaderId());
        assertEquals(LocalDateTime.of(1980, 2, 3, 10, 15), contentNode.getCreationDate());
        assertNotNull(contentNode.getLastModificationDate());
        assertFalse(contentNode.isHidden());
    }

    @Test
    public void testHiddenByParent() {
        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1, true);
        Long nodeId2 = coreMockService.createMockRootNode(2, false);

        Long contentNodeId = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        assertNotNull(contentNodeId);
        ContentNode contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertTrue(contentNode.getHiddenByParent());

        contentNodeId = coreMockService.createMockContentNode(31L, null, nodeId2, userId1, 2);
        assertNotNull(contentNodeId);
        contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertFalse(contentNode.getHiddenByParent());

        Long nodeId3 = coreMockService.createMockNode(nodeId1, 3, false);
        Long nodeId4 = coreMockService.createMockNode(nodeId2, 4, false);

        contentNodeId = coreMockService.createMockContentNode(32L, null, nodeId3, userId1, 3);
        assertNotNull(contentNodeId);
        contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertTrue(contentNode.getHiddenByParent());

        contentNodeId = coreMockService.createMockContentNode(33L, null, nodeId4, userId1, 4);
        assertNotNull(contentNodeId);
        contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertFalse(contentNode.getHiddenByParent());
    }

    @Test
    public void testHiddenByMoveToParent() {
        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1, true);
        Long nodeId2 = coreMockService.createMockRootNode(2, false);
        Long nodeId3 = coreMockService.createMockNode(nodeId1, 3, false);
        Long nodeId4 = coreMockService.createMockNode(nodeId2, 3, false);

        Long contentNodeId = coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        assertNotNull(contentNodeId);
        ContentNode contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertTrue(contentNode.getHiddenByParent());

        contentNodeService.moveContent(nodeId2, contentNodeId);

        contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertFalse(contentNode.getHiddenByParent());

        contentNodeService.moveContent(nodeId3, contentNodeId);

        contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertTrue(contentNode.getHiddenByParent());

        contentNodeService.moveContent(nodeId4, contentNodeId);

        contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertFalse(contentNode.getHiddenByParent());

        nodeService.moveNode(nodeId4, nodeId1);

        contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertTrue(contentNode.getHiddenByParent());

        nodeService.moveNode(nodeId4, nodeId2);

        contentNode = contentNodeRepository.findById(contentNodeId).orElseThrow();
        assertFalse(contentNode.getHidden());
        assertFalse(contentNode.getHiddenByParent());
    }

    @Test
    public void testModify_fail() {
        assertThrows(NullPointerException.class,
                () -> contentNodeService.modify(4, null, new HashSet<>(), false, LocalDateTime.of(1980, 2, 3, 10, 15)));
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

        assertNotNull(contentNodeService.getById(contentNode1));
        assertNull(contentNodeService.getById(contentNode2));
        assertNotNull(contentNodeService.getById(contentNode3));
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

        assertNotNull(contentNodeService.getById(contentNode1));
        assertNull(contentNodeService.getById(contentNode2));
        assertNotNull(contentNodeService.getById(contentNode3));
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
        long contentNodeId = contentNodeService.save(moduleId, contentId, name, tags, false, nodeId, userId, false,
                LocalDateTime.now(), null);

        assertEquals(1, contentNodeService.getCount());
        ContentNodeTO contentNodeByID = contentNodeService.getById(contentNodeId);
        assertNotNull(contentNodeByID);
        assertEquals(moduleId, contentNodeByID.getContentReaderId());
        assertEquals(contentId, contentNodeByID.getContentNodeId());
        for (ContentTagTO t : contentNodeByID.getContentTags())
            tags.remove(t.getName());
        assertTrue(tags.isEmpty());
        assertEquals(name, contentNodeByID.getName());
        assertEquals(userId, contentNodeByID.getAuthorId());
        assertEquals(nodeId, contentNodeByID.getParentId());
        assertNotNull(contentNodeByID.getCreationDate());
    }

    @Test
    public void testGetTagsByContentId() {
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
        long contentNodeId = contentNodeService.save(moduleId, contentId, name, tags, false, nodeId, userId, false,
                LocalDateTime.now(), null);

        List<String> list = contentNodeService.getTagsByContentId(contentNodeId);
        assertEquals("pokusy", list.get(0));
        assertEquals("testy", list.get(1));
        assertEquals("novinky", list.get(2));
        assertEquals("ŘěŇšb test diakritiky", list.get(3));
    }

    @Test
    public void testSave_GetByID_withoutTags() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);

        String moduleId = "mockModule";
        Long contentId = 2L;
        String name = "Test obsah";
        long contentNodeId =
                contentNodeService.save(moduleId, contentId, name, null, false, nodeId, userId, false, null, null);

        assertEquals(1, contentNodeService.getCount());
        ContentNodeTO contentNode = contentNodeService.getById(contentNodeId);
        assertNotNull(contentNode);
        assertNotNull(contentNode.getContentTags());
        assertTrue(contentNode.getContentTags().isEmpty());
        assertEquals(moduleId, contentNode.getContentReaderId());
        assertEquals(contentId, contentNode.getContentNodeId());
        assertEquals(name, contentNode.getName());
        assertEquals(userId, contentNode.getAuthorId());
        assertEquals(nodeId, contentNode.getParentId());
        assertNotNull(contentNode.getCreationDate());
    }

    @Test
    public void testSave_withoutContentModuleId() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);
        assertThrows(NullPointerException.class,
                () -> contentNodeService.save(null, 2L, "Test obsah", null, false, nodeId, userId, false, null, null));
    }

    @Test
    public void testSave_withoutName() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);
        assertThrows(NullPointerException.class,
                () -> contentNodeService.save("testModule", 2L, null, null, false, nodeId, userId, false, null, null));
    }

    @Test
    public void testGetByNode() throws Exception {
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

        List<ContentNodeOverviewTO> contentNodesByNode =
                contentNodeService.getByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId2), 0, 10);
        assertEquals(2, contentNodesByNode.size());

        ContentNodeOverviewTO contentNodeByNode = contentNodesByNode.getFirst();
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

        // hidden by parent varianta

        Long nodeId3 = coreMockService.createMockRootNode(3, true);
        nodeService.moveNode(nodeId2, nodeId3);

        contentNodesByNode = contentNodeService.getByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId2), 0, 10);
        assertEquals(0, contentNodesByNode.size());

        mockSecurityService.setRoles(new HashSet<>(List.of(CoreRole.ADMIN)));

        contentNodesByNode = contentNodeService.getByFilter(new ContentNodeFilterTO().setParentNodeId(nodeId2), 0, 10);
        assertEquals(2, contentNodesByNode.size());
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
        ContentNodeOverviewTO contentNodeByTag = contentNodesByTag.getFirst();
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