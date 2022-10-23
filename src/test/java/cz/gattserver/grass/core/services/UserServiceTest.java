package cz.gattserver.grass.core.services;

import static org.junit.jupiter.api.Assertions.*;

import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.core.mock.CoreMockService;
import cz.gattserver.grass.core.util.MockUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserServiceTest extends DBCleanTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ContentNodeService contentNodeService;

    @Autowired
    private CoreMockService coreMockService;

    @Test
    public void testGetUserInfoFromAllUsers() {
        Long userId = coreMockService.createMockUser(1);
        coreMockService.createMockUser(2);
        List<UserInfoTO> list = userService.getUserInfoFromAllUsers();
        assertEquals(2, list.size());
        assertEquals(userId, list.get(0).getId());
        assertEquals(MockUtils.MOCK_USER_EMAIL + 1, list.get(0).getEmail());
        assertEquals(MockUtils.MOCK_USER_NAME + 1, list.get(0).getName());
        assertNotNull(list.get(0).getPassword());
        assertNotNull(list.get(0).getRegistrationDate());
        assertNull(list.get(0).getLastLoginDate());
        assertEquals(1, list.get(0).getRoles().size());
        assertTrue(list.get(0).getRoles().contains(CoreRole.USER));
    }

    @Test
    public void testChangeUserRoles() {
        Long userId = coreMockService.createMockUser(1);
        UserInfoTO user = userService.getUserById(userId);
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(CoreRole.USER));

        Set<CoreRole> roles = new HashSet<>();
        roles.add(CoreRole.ADMIN);
        roles.add(CoreRole.FRIEND);
        userService.changeUserRoles(userId, roles);

        user = userService.getUserById(userId);
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(CoreRole.ADMIN));
        assertTrue(user.getRoles().contains(CoreRole.FRIEND));
    }

    @Test
    public void testChangeUserRoles_fail() {
        assertThrows(NullPointerException.class, () -> userService.changeUserRoles(1L, null));
    }

    @Test
    public void testBanUser() {
        Long userId = coreMockService.createMockUser(1);
        UserInfoTO user = userService.getUserById(userId);
        assertTrue(user.isConfirmed());

        userService.banUser(userId);
        user = userService.getUserById(userId);
        assertFalse(user.isConfirmed());
    }

    @Test
    public void testActivateUser() {
        long userId = userService.registrateNewUser(MockUtils.MOCK_USER_EMAIL + 1, MockUtils.MOCK_USER_NAME + 1,
                MockUtils.MOCK_USER_PASSWORD + 1);
        UserInfoTO user = userService.getUserById(userId);
        assertFalse(user.isConfirmed());

        user = userService.getUserById(userId);
        assertFalse(user.isConfirmed());

        userService.activateUser(user.getId());
        user = userService.getUserById(userId);
        assertTrue(user.isConfirmed());
    }

    @Test
    public void testAddContentToFavourites() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);
        Long contentNodeId = coreMockService.createMockContentNode(220L, null, nodeId, userId, 1);

        Long user2Id = coreMockService.createMockUser(2);
        userService.addContentToFavourites(contentNodeId, user2Id);

        List<ContentNodeOverviewTO> favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
        assertEquals(1, favourites.size());
        assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 1, favourites.get(0).getName());
    }

    @Test
    public void testHasInFavourite() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);
        Long contentNodeId = coreMockService.createMockContentNode(220L, null, nodeId, userId, 1);
        Long contentNodeId2 = coreMockService.createMockContentNode(20L, null, nodeId, userId, 2);

        Long user2Id = coreMockService.createMockUser(2);
        userService.addContentToFavourites(contentNodeId, user2Id);

        assertTrue(userService.hasInFavourites(contentNodeId, user2Id));
        assertFalse(userService.hasInFavourites(contentNodeId2, user2Id));
    }

    @Test
    public void testRemoveContentFromFavourites_manual() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);
        Long contentNodeId = coreMockService.createMockContentNode(220L, null, nodeId, userId, 1);

        Long user2Id = coreMockService.createMockUser(2);
        userService.addContentToFavourites(contentNodeId, user2Id);

        List<ContentNodeOverviewTO> favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
        assertEquals(1, favourites.size());
        assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 1, favourites.get(0).getName());

        userService.removeContentFromFavourites(contentNodeId, user2Id);

        favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
        assertTrue(favourites.isEmpty());
    }

    @Test
    public void testRemoveContentFromFavourites_byContentDelete() {
        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(2);
        Long contentNodeId = coreMockService.createMockContentNode(220L, null, nodeId, userId, 1);

        Long user2Id = coreMockService.createMockUser(2);
        userService.addContentToFavourites(contentNodeId, user2Id);

        List<ContentNodeOverviewTO> favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
        assertEquals(1, favourites.size());
        assertEquals(MockUtils.MOCK_CONTENTNODE_NAME + 1, favourites.get(0).getName());

        contentNodeService.deleteByContentNodeId(contentNodeId);

        favourites = contentNodeService.getUserFavourite(user2Id, 0, 10);
        assertTrue(favourites.isEmpty());
    }

    @Test
    public void testRegistrateNewUser() {
        String username = "TestUser";
        String email = "testuser@email.cz";
        userService.registrateNewUser(email, username, "testUser00012xxx$");

        UserInfoTO user = userService.getUser(username);
        assertEquals(username, user.getName());
        assertEquals(email, user.getEmail());
        assertFalse(user.isConfirmed());
    }

    @Test
    public void testRegistrateNewUser_fail() {
        assertThrows(NullPointerException.class, () -> userService.registrateNewUser(null, "username", "testUser00012xxx$"));
    }

    @Test
    public void testRegistrateNewUser_fail2() {
        assertThrows(IllegalArgumentException.class, () -> userService.registrateNewUser("", "username", "testUser00012xxx$"));
    }

    @Test
    public void testRegistrateNewUser_fail3() {
        assertThrows(IllegalArgumentException.class, () -> userService.registrateNewUser(" ", "username", "testUser00012xxx$"));
    }

    @Test
    public void testRegistrateNewUser_fail4() {
        assertThrows(NullPointerException.class, () -> userService.registrateNewUser("email", null, "testUser00012xxx$"));
    }

    @Test
    public void testRegistrateNewUser_fail5() {
        assertThrows(IllegalArgumentException.class, () -> userService.registrateNewUser("email", "", "testUser00012xxx$"));
    }

    @Test
    public void testRegistrateNewUser_fail6() {
        assertThrows(IllegalArgumentException.class, () -> userService.registrateNewUser("email", " ", "testUser00012xxx$"));
    }

    @Test
    public void testRegistrateNewUser_fail7() {
        assertThrows(NullPointerException.class, () -> userService.registrateNewUser("email", "username", null));
    }

    @Test
    public void testRegistrateNewUser_fail8() {
        assertThrows(IllegalArgumentException.class, () -> userService.registrateNewUser("email", "username", ""));
    }

    @Test
    public void testRegistrateNewUser_fail9() {
        assertThrows(IllegalArgumentException.class, () -> userService.registrateNewUser("email", "username", " "));
    }

    @Test
    public void testGetUser_fail() {
        assertThrows(NullPointerException.class, () -> userService.getUser(null));
    }

    @Test
    public void testGetUser_fail2() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUser(""));
    }

    @Test
    public void testGetUser_fail3() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUser(" "));
    }

}
