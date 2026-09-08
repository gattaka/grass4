package cz.gattserver.grass.core.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.model.domain.Node;
import cz.gattserver.grass.core.model.repositories.NodeRepository;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.core.mock.CoreMockService;
import cz.gattserver.grass.core.util.MockUtils;
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

import java.util.List;

public class NodeServiceTest extends DBCleanTest {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CoreMockService coreMockService;

    @Resource
    @Qualifier("securityServiceImpl")
    private SecurityService securityService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
    }

    public void createAndLoginAdmin() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/")).andReturn();
        coreMockService.createMockUser(1, true);
        securityService.login(MockUtils.MOCK_USER_NAME + 1, MockUtils.MOCK_USER_PASSWORD + 1, false,
                mvcResult.getRequest(), mvcResult.getResponse());
    }

    private Long createNewNode(Long parentId, boolean hidden, String name) {
        NodeTO to = new NodeTO(null, name, null, parentId, hidden, false);
        return nodeService.save(to);
    }

    @Test
    public void testCreateNewNode() {
        Long nodeId = createNewNode(null, false, "testNode");
        NodeTO node = nodeService.getNodeById(nodeId);
        assertNotNull(node);
        assertEquals(nodeId, node.getId());
        assertNull(node.getParentId());
        assertFalse(node.getHidden());
        assertFalse(node.getHiddenByParent());
        assertEquals("testNode", node.getName());
    }

    @Test
    public void testCreateNewHiddenNode() {
        NodeTO to = new NodeTO(null, "testHiddenNode", null, null, true, true);
        long nodeId = nodeService.save(to);
        Node node = nodeRepository.findById(nodeId).orElseThrow();
        assertNotNull(node);
        assertEquals(nodeId, node.getId());
        assertNull(node.getParentId());
        assertTrue(node.getHidden());
        assertFalse(node.getHiddenByParent());
        assertEquals("testHiddenNode", node.getName());
    }

    @Test
    public void testCreateNewNode_fail() {
        assertThrows(NullPointerException.class, () -> createNewNode(null, false, null));
    }

    @Test
    public void testCreateNewNode_fail2() {
        assertThrows(IllegalArgumentException.class, () -> createNewNode(null, false, ""));
    }

    @Test
    public void testCreateNewNode_fail3() {
        assertThrows(IllegalArgumentException.class, () -> createNewNode(null, false, " "));
    }

    @Test
    public void testDeleteNode() {
        assertEquals(0, nodeService.getNodesForTree().size());
        Long nodeId1 = createNewNode(null, false, "testNode");
        createNewNode(null, false, "testNode2");
        assertEquals(2, nodeService.getNodesForTree().size());
        nodeService.deleteNode(nodeId1);
        assertEquals(1, nodeService.getNodesForTree().size());
    }

    @Test
    public void testDeleteNode_notEmpty() {
        Long nodeId1 = createNewNode(null, false, "testNode");
        createNewNode(nodeId1, false, "testNode");
        assertThrows(IllegalStateException.class, () -> nodeService.deleteNode(nodeId1));
    }

    @Test
    public void testDeleteNode_notEmpty2() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = createNewNode(null, false, "testNode");
        coreMockService.createMockContentNode(3L, null, nodeId, userId, 1);
        assertThrows(IllegalStateException.class, () -> nodeService.deleteNode(nodeId));
    }

    @Test
    public void testGetNodeByIdForDetail() {
        Long nodeId0 = createNewNode(null, false, "testParent");
        Long nodeId1 = createNewNode(nodeId0, false, "testNode");
        NodeTO node = nodeService.getNodeById(nodeId1);
        assertEquals(nodeId1, node.getId());
        assertEquals("testNode", node.getName());
        assertEquals("testParent", node.getParentName());
    }

    @Test
    public void testGetNodeByIdForOverview() {
        Long nodeId0 = createNewNode(null, false, "testParent");
        Long nodeId1 = createNewNode(nodeId0, false, "testNode");
        NodeTO node = nodeService.getNodeById(nodeId1);
        assertEquals(nodeId1, node.getId());
        assertEquals("testNode", node.getName());
        assertEquals(nodeId0, node.getParentId());
        assertEquals("testParent", node.getParentName());
    }

    @Test
    public void testGetNodesByParentNode() {
        Long nodeId0 = createNewNode(null, false, "testParent");
        createNewNode(nodeId0, false, "testNode1");
        createNewNode(nodeId0, false, "testNode2");
        List<NodeTO> nodes = nodeService.getNodesByParentNode(nodeId0);
        assertEquals(2, nodes.size());
        assertEquals("testNode1", nodes.get(0).getName());
        assertEquals("testNode2", nodes.get(1).getName());
    }

    @Test
    public void testGetNodesForTree() {
        Long nodeId0 = createNewNode(null, false, "testParent");
        createNewNode(nodeId0, false, "testNode1");
        Long nodeId1 = createNewNode(nodeId0, false, "testNode2");
        createNewNode(nodeId1, false, "testChild");
        List<NodeTO> nodes = nodeService.getNodesForTree();
        assertEquals(4, nodes.size());
        assertEquals("testParent", nodes.get(0).getName());
        assertEquals("testNode1", nodes.get(1).getName());
        assertEquals("testNode2", nodes.get(2).getName());
        assertEquals("testChild", nodes.get(3).getName());
    }

    @Test
    public void testGetRootNodes() {
        Long nodeId0 = createNewNode(null, false, "testParent");
        createNewNode(null, false, "testParent2");
        createNewNode(nodeId0, false, "testNode1");
        Long nodeId1 = createNewNode(nodeId0, false, "testNode2");
        createNewNode(nodeId1, false, "testChild");
        List<NodeTO> nodes = nodeService.getRootNodes();
        assertEquals(2, nodes.size());
        assertEquals("testParent", nodes.get(0).getName());
        assertEquals("testParent2", nodes.get(1).getName());
    }

    @Test
    public void testIsNodeEmpty() {
        Long nodeId1 = createNewNode(null, false, "nodeWithContentNode");
        Long nodeId2 = createNewNode(null, false, "nodeWithSubNode");
        Long nodeId3 = createNewNode(nodeId2, false, "emptyNode");
        Long userId1 = coreMockService.createMockUser(1);
        coreMockService.createMockContentNode(30L, null, nodeId1, userId1, 1);
        assertFalse(nodeService.isNodeEmpty(nodeId1));
        assertFalse(nodeService.isNodeEmpty(nodeId2));
        assertTrue(nodeService.isNodeEmpty(nodeId3));
    }

    @Test
    public void testMoveNode_newRoot() {
        Long nodeId1 = createNewNode(null, false, "testNode1");
        Long nodeId2 = createNewNode(nodeId1, false, "testNode3");

        NodeTO nodeDTO = nodeService.getNodeById(nodeId2);
        assertEquals(nodeId1, nodeDTO.getParentId());

        nodeDTO = nodeService.getNodeById(nodeDTO.getParentId());
        assertNull(nodeDTO.getParentId());

        nodeService.moveNode(nodeId2, null);

        nodeDTO = nodeService.getNodeById(nodeId2);
        assertNull(nodeDTO.getParentId());
    }

    @Test
    public void testMoveNode_ok1() {
        Long nodeId1 = createNewNode(null, false, "testNode1");
        Long nodeId2 = createNewNode(null, false, "testNode2");
        Long nodeId3 = createNewNode(nodeId2, false, "testNode3");

        nodeService.moveNode(nodeId2, nodeId1);
        assertEquals(nodeId1, nodeService.getNodeById(nodeId2).getParentId());

        NodeTO nodeDTO = nodeService.getNodeById(nodeId3);
        assertEquals(nodeId2, nodeDTO.getParentId());

        nodeDTO = nodeService.getNodeById(nodeDTO.getParentId());
        assertEquals(nodeId1, nodeDTO.getParentId());
    }

    @Test
    public void testMoveNode_ok2() {
        Long nodeId1 = createNewNode(null, false, "testNode1");
        Long nodeId2 = createNewNode(null, false, "testNode2");
        Long nodeId3 = createNewNode(nodeId2, false, "testNode3");

        nodeService.moveNode(nodeId2, nodeId1);
        nodeService.moveNode(nodeId3, nodeId1);

        NodeTO nodeDTO = nodeService.getNodeById(nodeId3);
        assertEquals(nodeId1, nodeDTO.getParentId());

        nodeDTO = nodeService.getNodeById(nodeDTO.getParentId());
        assertNull(nodeDTO.getParentId());
    }

    @Test
    public void testMoveNode_noChange() {
        Long nodeId1 = createNewNode(null, false, "testNode1");
        Long nodeId2 = createNewNode(nodeId1, false, "testNode2");
        Long nodeId3 = createNewNode(nodeId2, false, "testNode3");

        nodeService.moveNode(nodeId3, nodeId2);

        NodeTO nodeDTO = nodeService.getNodeById(nodeId3);
        assertEquals(nodeId2, nodeDTO.getParentId());

        nodeDTO = nodeService.getNodeById(nodeDTO.getParentId());
        assertEquals(nodeId1, nodeDTO.getParentId());

        nodeDTO = nodeService.getNodeById(nodeDTO.getParentId());
        assertNull(nodeDTO.getParentId());
    }

    @Test
    public void testMoveNode_fail1() {
        Long nodeId1 = createNewNode(null, false, "testNode1");
        Long nodeId2 = createNewNode(nodeId1, false, "testNode2");
        Long nodeId3 = createNewNode(nodeId2, false, "testNode3");
        assertThrows(IllegalArgumentException.class, () -> nodeService.moveNode(nodeId1, nodeId3));
    }

    @Test
    public void testMoveNode_fail2() {
        Long nodeId1 = createNewNode(null, false, "testNode1");
        Long nodeId2 = createNewNode(nodeId1, false, "testNode2");
        Long nodeId3 = createNewNode(nodeId2, false, "testNode3");
        assertThrows(IllegalArgumentException.class, () -> nodeService.moveNode(nodeId2, nodeId3));
    }

    @Test
    public void testMoveNode_dbCycle() {
        Long nodeId1 = createNewNode(null, false, "testNode1");
        Long nodeId2 = createNewNode(nodeId1, false, "testNode2");
        Long nodeId3 = createNewNode(nodeId2, false, "testNode3");

        Node node = nodeRepository.findById(nodeId1).orElseThrow();
        node.setParentId(nodeId3);
        nodeRepository.save(node);

        Long nodeId4 = createNewNode(null, false, "testNode4");
        assertThrows(IllegalStateException.class, () -> nodeService.moveNode(nodeId4, nodeId3));
    }

    @Test
    public void testRenameNode() {
        Long nodeId1 = createNewNode(null, false, "testNode");
        nodeService.rename(nodeId1, "newTestNode");
        assertEquals("newTestNode", nodeService.getNodeById(nodeId1).getName());
    }

    @Test
    public void testRenameNode_fail() {
        assertThrows(NullPointerException.class, () -> nodeService.rename(1L, null));
    }

    @Test
    public void testRenameNode_fail2() {
        assertThrows(IllegalArgumentException.class, () -> nodeService.rename(1L, ""));
    }

    @Test
    public void testRenameNode_fail3() {
        assertThrows(IllegalArgumentException.class, () -> nodeService.rename(1L, " "));
    }

    @Test
    public void testGetByFilter() {
        createNewNode(null, false, "testNode");
        createNewNode(null, false, "testNode2");
        createNewNode(null, false, "test3");
        List<NodeTO> results = nodeService.getByFilter("node");
        assertEquals(2, results.size());
        assertEquals("testNode", results.getFirst().getName());
        assertEquals("testNode2", results.getLast().getName());
    }

    @Test
    public void testHiddenByParent() {
        Long nodeId1 = coreMockService.createMockRootNode(1, true);
        Long nodeId2 = coreMockService.createMockRootNode(2, false);

        Node node = nodeRepository.findById(nodeId1).orElseThrow();
        assertTrue(node.getHidden());
        assertFalse(node.getHiddenByParent());

        node = nodeRepository.findById(nodeId2).orElseThrow();
        assertFalse(node.getHidden());
        assertFalse(node.getHiddenByParent());

        Long nodeId3 = coreMockService.createMockNode(nodeId1, 3, false);
        Long nodeId4 = coreMockService.createMockNode(nodeId2, 4, false);

        node = nodeRepository.findById(nodeId3).orElseThrow();
        assertFalse(node.getHidden());
        assertTrue(node.getHiddenByParent());

        node = nodeRepository.findById(nodeId4).orElseThrow();
        assertFalse(node.getHidden());
        assertFalse(node.getHiddenByParent());
    }

    @Test
    public void testHiddenByMoveToParent() {
        Long nodeId1 = coreMockService.createMockRootNode(1, true);
        Long nodeId2 = coreMockService.createMockRootNode(2, false);
        Long nodeId3 = coreMockService.createMockNode(nodeId2, 3, false);

        Node node = nodeRepository.findById(nodeId3).orElseThrow();
        assertFalse(node.getHidden());
        assertFalse(node.getHiddenByParent());

        nodeService.moveNode(nodeId3, nodeId1);

        node = nodeRepository.findById(nodeId3).orElseThrow();
        assertFalse(node.getHidden());
        assertTrue(node.getHiddenByParent());

        nodeService.moveNode(nodeId3, nodeId2);

        node = nodeRepository.findById(nodeId3).orElseThrow();
        assertFalse(node.getHidden());
        assertFalse(node.getHiddenByParent());
    }
}