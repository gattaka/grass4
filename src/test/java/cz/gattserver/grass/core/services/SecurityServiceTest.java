package cz.gattserver.grass.core.services;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.impl.LoginResult;
import cz.gattserver.grass.core.util.TestDBService;
import cz.gattserver.grass.core.mock.CoreMockService;
import cz.gattserver.grass.core.util.MockUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import jakarta.annotation.Resource;
import jakarta.servlet.Filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityServiceTest {

    @Autowired
    private TestDBService testDbService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Resource
    @Qualifier("securityServiceImpl")
    private SecurityService securityService;

    @Autowired
    private CoreMockService coreMockService;

    private MockMvc mvc;

    @AfterEach
    void afterEach() {
        testDbService.resetDatabase();
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();
    }

    @Test
    public void testLogin() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/")).andReturn();

        coreMockService.createMockUser(1);
        LoginResult result = securityService.login(MockUtils.MOCK_USER_NAME + 1, MockUtils.MOCK_USER_PASSWORD + 1,
                false, mvcResult.getRequest(), mvcResult.getResponse());
        assertEquals(LoginResult.SUCCESS, result);
        UserInfoTO user = securityService.getCurrentUser();
        assertEquals(MockUtils.MOCK_USER_NAME + 1, user.getUsername());
    }

    @Test
    public void testLogin_remember() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/")).andReturn();

        coreMockService.createMockUser(1);
        LoginResult result = securityService.login(MockUtils.MOCK_USER_NAME + 1, MockUtils.MOCK_USER_PASSWORD + 1, true,
                mvcResult.getRequest(), mvcResult.getResponse());
        assertEquals(LoginResult.SUCCESS, result);
        UserInfoTO user = securityService.getCurrentUser();
        assertEquals(MockUtils.MOCK_USER_NAME + 1, user.getUsername());
    }

    @Test
    public void testLogin_failed() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/")).andReturn();

        coreMockService.createMockUser(1);
        LoginResult result = securityService.login("wrong", MockUtils.MOCK_USER_PASSWORD + 1, false,
                mvcResult.getRequest(), mvcResult.getResponse());
        assertEquals(LoginResult.FAILED_CREDENTIALS, result);
    }

    @Test
    public void testLogin_failed2() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/")).andReturn();

        coreMockService.createMockUser(1);
        LoginResult result = securityService.login(MockUtils.MOCK_USER_NAME + 1, "wrong", false, mvcResult.getRequest(),
                mvcResult.getResponse());
        assertEquals(LoginResult.FAILED_CREDENTIALS, result);
    }

    @Test
    public void testGetCurrentUser() {
        UserInfoTO user = securityService.getCurrentUser();
        assertNotNull(user);
        assertNull(user.getName());
    }

}
