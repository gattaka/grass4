package cz.gattserver.grass.articles;

import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.mock.CoreMockService;
import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.model.repositories.ContentNodeRepository;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.UserService;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.core.util.MockUtils;
import cz.gattserver.grass.test.MockSecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ArticlesServiceTest extends DBCleanTest {

    @Autowired
    private ArticleService articleService;

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

}