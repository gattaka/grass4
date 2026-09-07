package cz.gattserver.grass.core.mock;

import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.UserService;
import cz.gattserver.grass.core.util.MockUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class CoreMockService {

    @Autowired
    private UserService userService;

    @Autowired
    private ContentNodeService contentNodeService;

    @Autowired
    private NodeService nodeService;

    public Long createMockUser(int variant) {
        Long userId =
                userService.registrateNewUser(MockUtils.MOCK_USER_EMAIL + variant, MockUtils.MOCK_USER_NAME + variant,
                        MockUtils.MOCK_USER_PASSWORD + variant);
        userService.activateUser(userId);
        return userId;
    }

    public Long createMockRootNode(int variant) {
        NodeTO nodeTO = new NodeTO(null, MockUtils.MOCK_NODE_NAME + variant, null, null, false, false);
        return nodeService.save(nodeTO);
    }

    public Long createMockContentNode(Long contentId, Set<String> tags, long nodeId, long userId, int variant) {
        return contentNodeService.save(MockUtils.MOCK_CONTENTNODE_MODULE + variant, contentId,
                MockUtils.MOCK_CONTENTNODE_NAME + variant, tags, false, nodeId, userId, false, LocalDateTime.now(),
                null);
    }

}
