package cz.gattserver.grass.rest;

import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleEditorTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.modules.ArticlesContentModule;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/ws/articles")
public class ArticlesResource {

    private static Logger logger = LoggerFactory.getLogger(ArticlesResource.class);

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ContentNodeService contentNodeService;

    @Autowired
    private NodeService nodeService;

    // http://localhost:8180/web/ws/articles/create
    // http://resttesttest.com/ (pozor na http -- nedá se posílaz na http, pokud
    // je resttesttest spuštěn z https
    // POST http://localhost:8180/web/ws/articles/create
    // text test článku...
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<Long> smsImport(@RequestParam(value = "text") String text) {
        logger.info("articles /create volán");
        UserInfoTO user = securityService.getCurrentUser();
        if (user.getId() == null) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        // TODO "dummy" context root?
        ArticleEditorTO payload = new ArticleEditorTO("dummy");
        payload.setDraftName(
                "GrassAndroid Import " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("d.M.yyyy")));
        payload.setDraftText(text);
        payload.setDraftPublicated(false);
        payload.setNodeId(nodeService.getRootNodes().get(0).getId());

        long articleId = articleService.saveArticle(payload);

        logger.info("articles /create dokončen");
        return new ResponseEntity<>(articleId, HttpStatus.OK);
    }

    @RequestMapping("/count")
    public ResponseEntity<Integer> count(@RequestParam(value = "filter", required = false) String filter) {
        return new ResponseEntity<>(contentNodeService.getCountByFilter(
                new ContentNodeFilterTO().setName(filter).setContentReaderID(ArticlesContentModule.ID)), HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity<List<ContentNodeOverviewTO>> list(@RequestParam(value = "page", required = true) int page,
                                                            @RequestParam(value = "pageSize") int pageSize,
                                                            @RequestParam(value = "filter", required = false)
                                                            String filter) {
        int count = contentNodeService.getCountByFilter(
                new ContentNodeFilterTO().setName(filter).setContentReaderID(ArticlesContentModule.ID));
        // startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
        // poradí a sníží ho
        if (page * pageSize > count) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(contentNodeService.getByFilter(
                new ContentNodeFilterTO().setName(filter).setContentReaderID(ArticlesContentModule.ID), page * pageSize,
                pageSize), HttpStatus.OK);
    }

    @RequestMapping(value = "/article", method = RequestMethod.GET)
    public ResponseEntity<ArticleTO> show(@RequestParam(value = "id") Long id) {
        logger.info("articles /article volán");
        UserInfoTO user = securityService.getCurrentUser();
        ArticleTO articleRESTTO;
        articleRESTTO = articleService.getArticleForDetail(id, user.getId(), user.isAdmin());
        if (articleRESTTO == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(articleRESTTO, HttpStatus.OK);
    }

}
