package cz.gattserver.grass.core.mock;

import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.UserService;
import cz.gattserver.grass.core.util.MockUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
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
        return createMockUser(variant, false);
    }

    public Long createMockUser(int variant, boolean admin) {
        Long userId =
                userService.registrateNewUser(MockUtils.MOCK_USER_EMAIL + variant, MockUtils.MOCK_USER_NAME + variant,
                        MockUtils.MOCK_USER_PASSWORD + variant);
        userService.activateUser(userId);

        if (admin) {
            Set<CoreRole> roles = new HashSet<>();
            roles.add(CoreRole.ADMIN);
            userService.changeUserRoles(userId, roles);
        }

        return userId;
    }

    public Long createMockRootNode(int variant) {
        return createMockRootNode(variant, false);
    }

    public Long createMockRootNode(int variant, boolean hidden) {
        return createMockNode(null, variant, hidden);
    }

    public Long createMockNode(long parentId, int variant) {
        return createMockNode(parentId, variant, false);
    }

    public Long createMockNode(Long parentId, int variant, boolean hidden) {
        NodeTO nodeTO = new NodeTO(null, MockUtils.MOCK_NODE_NAME + variant, null, parentId, hidden, false);
        return nodeService.save(nodeTO);
    }

    public Long createMockContentNode(Long contentId, Set<String> tags, long nodeId, long userId, int variant) {
        return contentNodeService.save(MockUtils.MOCK_CONTENTNODE_MODULE + variant, contentId,
                MockUtils.MOCK_CONTENTNODE_NAME + variant, tags, false, nodeId, userId, false, LocalDateTime.now(),
                null);
    }

}
