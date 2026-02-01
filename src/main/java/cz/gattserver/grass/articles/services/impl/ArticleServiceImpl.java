package cz.gattserver.grass.articles.services.impl;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass.articles.AttachmentsOperationResult;
import cz.gattserver.grass.articles.config.ArticlesConfiguration;
import cz.gattserver.grass.articles.editor.lexer.Lexer;
import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.impl.ArticleParser;
import cz.gattserver.grass.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass.articles.editor.parser.interfaces.*;
import cz.gattserver.grass.articles.editor.parser.util.HTMLTagsFilter;
import cz.gattserver.grass.articles.events.impl.ArticlesProcessProgressEvent;
import cz.gattserver.grass.articles.events.impl.ArticlesProcessResultEvent;
import cz.gattserver.grass.articles.events.impl.ArticlesProcessStartEvent;
import cz.gattserver.grass.articles.model.domain.Article;
import cz.gattserver.grass.articles.model.domain.ArticleJSCode;
import cz.gattserver.grass.articles.model.domain.ArticleJSResource;
import cz.gattserver.grass.articles.model.repositories.ArticleRepository;
import cz.gattserver.grass.articles.plugins.register.PluginRegisterService;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.articles.services.ArticlesMapperService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.modules.ArticlesContentModule;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.UnauthorizedAccessException;
import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.model.domain.ContentTag;
import cz.gattserver.grass.core.model.domain.User;
import cz.gattserver.grass.core.model.repositories.UserRepository;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.FileSystemService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
public class ArticleServiceImpl implements ArticleService {

    private static final String ILLEGAL_PATH_ERR = "Podtečení adresáře příloh";
    private static final String ATTACHMENT_DIR_PREFIX = "attachments-";

    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private EventBus eventBus;

    @Autowired
    private ContentNodeService contentNodeFacade;

    @Autowired
    private ArticlesMapperService articlesMapper;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PluginRegisterService pluginRegister;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private SecurityService securityService;

    private Context processArticle(String source, String contextRoot) {
        Validate.notNull(contextRoot, "ContextRoot nemůže být null");

        Lexer lexer = new Lexer(source);
        Parser parser = new ArticleParser();
        ParsingProcessor parsingProcessor =
                new ParsingProcessor(lexer, contextRoot, pluginRegister.createRegisterSnapshot());

        // výstup
        Element tree = parser.parse(parsingProcessor);
        Context ctx = new ContextImpl();
        tree.apply(ctx);

        return ctx;
    }

    @Override
    public Long saveArticle(ArticleEditorTO articleEditorTO) {
        // zatím bez procesování
        Article article = innerSaveArticle(articleEditorTO, false, false);

        // Nahraď draft id ve všech attachment odkazech novým id ostrého článku
        String linkFrom = ArticlesConfiguration.ATTACHMENTS_PATH + "/" + articleEditorTO.getDraftId();
        String linkTo = ArticlesConfiguration.ATTACHMENTS_PATH + "/" + article.getId();
        article.setText(article.getText().replaceAll(linkFrom, linkTo));
        reprocessArticle(article, articleEditorTO.getContextRoot());

        // Nejprve smaž draft, ale ponech přílohy (ty se musí přenést do ostrého článku).
        // Pokud dojde k problémům se soubory, dá se provést DB rollback, pokud by pořadí operací bylo obráceně,
        // mohl by nastat problém, protože filesystem nemá rollback
        deleteArticleInner(articleEditorTO.getDraftId(), false);

        Path existingArticleDirPath = getAttachmentsPath(articleEditorTO.getExistingArticleId(), true);

        Set<String> draftAttachmentsNames =
                articleEditorTO.getDraftAttachments().stream().map(AttachmentTO::getName).collect(Collectors.toSet());

        // Smaž odebrané přílohy
        try (Stream<Path> s = Files.list(existingArticleDirPath)) {
            s.forEach(p -> {
                String fileName = p.getFileName().toString();
                try {
                    if (!draftAttachmentsNames.contains(fileName)) Files.delete(p);
                } catch (IOException e) {
                    throw new RuntimeException("Chyba při mazání přílohy " + fileName + " článku " +
                            articleEditorTO.getExistingArticleId(), e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(
                    "Nezdařilo se získat přehled příloh článku " + articleEditorTO.getExistingArticleId(), e);
        }

        // Ulož nové přílohy
        for (AttachmentTO to : articleEditorTO.getDraftAttachments()) {
            if (to.isDraft()) {
                Path targetPath = existingArticleDirPath.resolve(to.getPath().getFileName());
                try {
                    Files.move(to.getPath(), targetPath);
                } catch (IOException e) {
                    throw new RuntimeException("Nezdařilo se uložit přílohu " + to.getPath().getFileName(), e);
                }
            }
        }

        // Smaž adresář příloh draftu
        Path draftAttachmentsDirPath = getAttachmentsPath(articleEditorTO.getDraftId(), false);
        if (draftAttachmentsDirPath != null) {
            try {
                Files.delete(draftAttachmentsDirPath);
            } catch (IOException e) {
                throw new RuntimeException("Chyba při mazání adresáře příloh článku " + articleEditorTO.getDraftId(),
                        e);
            }
        }

        return article.getId();
    }

    @Override
    public Long saveDraft(ArticleEditorTO articleEditorTO, boolean asPreview) {
        return innerSaveArticle(articleEditorTO, asPreview, true).getId();
    }

    @Override
    public void deleteArticle(Long id) {
        deleteArticleInner(id, true);
    }

    private void deleteArticleInner(Long id, boolean deleteAttachments) {
        Article article = articleRepository.findById(id).get();

        // smaž článek
        articleRepository.deleteById(id);

        // smaž jeho content node
        contentNodeFacade.deleteByContentId(ArticlesContentModule.ID, id);

        if (deleteAttachments) {
            // smaž jeho přílohy
            try {
                Path attachmentsPath = getAttachmentsPath(article.getId(), false);
                if (attachmentsPath != null) {
                    try (Stream<Path> s = Files.walk(attachmentsPath)) {
                        // reverseOrder, protože jinak by walk začal přímo adresářem,
                        // namísto jeho obsahem. Maže se všechno, takže potřebuju i samotný
                        // adresář -- jinak bych použil Files::list, namísto Files::walk
                        s.sorted(Comparator.reverseOrder()).forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                logger.error(
                                        "Chyba při mazání přílohy článku [" + article.getAttachmentsDirId() + "] (" +
                                                p.getFileName().toString() + ")", e);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                logger.warn("Nezdařilo se smazat adresář příloh článku [" + article.getAttachmentsDirId() + "]");
            }
        }
    }

    private SortedSet<ArticleJSResource> createJSResourcesSet(Set<String> scripts) {
        int order = 0;
        SortedSet<ArticleJSResource> set = new TreeSet<>();
        for (String name : scripts) {
            ArticleJSResource resource = new ArticleJSResource();
            resource.setName(name);
            resource.setExecutionOrder(order++);
            set.add(resource);
        }
        return set;
    }

    private SortedSet<ArticleJSCode> createJSCodesSet(Set<String> contents) {
        int order = 0;
        SortedSet<ArticleJSCode> set = new TreeSet<>();
        for (String content : contents) {
            ArticleJSCode resource = new ArticleJSCode();
            resource.setContent(content);
            resource.setExecutionOrder(order++);
            set.add(resource);
        }
        return set;
    }

    private Article innerSaveArticle(ArticleEditorTO payload, boolean process, boolean draft) {
        Long articleIdToFind = draft ? payload.getDraftId() : payload.getExistingArticleId();

        Article article;
        if (articleIdToFind == null) {
            article = new Article();
        } else {
            article = articleRepository.findById(articleIdToFind).orElse(null);
        }

        if (article == null) throw new IllegalStateException("Nenalezen článek dle id " + articleIdToFind);

        // nasetuj do něj vše potřebné
        if (process) {
            Context context = processArticle(payload.getDraftText(), payload.getContextRoot());
            article.setOutputHTML(context.getOutput());
            article.setPluginCSSResources(context.getCSSResources());
            article.setPluginJSResources(createJSResourcesSet(context.getJSResources()));
            article.setPluginJSCodes(createJSCodesSet(context.getJSCodes()));
            article.setSearchableOutput(HTMLTagsFilter.trim(context.getOutput()));
        }
        article.setText(payload.getDraftText());

        // ulož ho a nasetuj jeho id
        article = articleRepository.save(article);

        if (articleIdToFind == null) {
            // vytvoř odpovídající content node
            Long contentNodeId =
                    contentNodeFacade.save(ArticlesContentModule.ID, article.getId(), payload.getDraftName(),
                            payload.getDraftTags(), payload.isDraftPublicated(), payload.getNodeId(),
                            securityService.getCurrentUser().getId(), draft, null, payload.getExistingArticleId());

            // ulož do článku referenci na jeho contentnode
            ContentNode contentNode = new ContentNode();
            contentNode.setId(contentNodeId);
            article.setContentNode(contentNode);
            articleRepository.save(article);
        } else {
            contentNodeFacade.modify(article.getContentNode().getId(), payload.getDraftName(), payload.getDraftTags(),
                    payload.isDraftPublicated());
        }

        return article;
    }

    @Override
    public ArticleTO getArticleForDetail(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) return null;
        return articlesMapper.mapArticleForDetail(article);
    }

    @Override
    public ArticleRESTTO getArticleForREST(Long id, Long userId) throws UnauthorizedAccessException {
        Article article = articleRepository.findById(id).orElse(null);
        if (article == null) return null;
        User user = userId == null ? null : userRepository.findById(userId).orElse(null);
        if (article.getContentNode().getPublicated() ||
                user != null && (user.isAdmin() || article.getContentNode().getAuthor().getId().equals(user.getId()))) {
            return articlesMapper.mapArticleForREST(article);
        }
        throw new UnauthorizedAccessException();
    }

    @Async
    @Override
    public void reprocessAllArticles(UUID operationId, String contextRoot) {
        int total = (int) articleRepository.count();
        eventBus.publish(new ArticlesProcessStartEvent(total));
        int current = 0;
        int pageSize = 100;
        int pages = (int) Math.ceil(total * 1.0 / pageSize);
        for (int page = 0; page < pages; page++) {
            List<Article> articles = articleRepository.findAll(PageRequest.of(page, pageSize)).getContent();
            for (Article article : articles) {
                reprocessArticle(article, contextRoot);
                eventBus.publish(new ArticlesProcessProgressEvent(
                        "(" + current + "/" + total + ") " + article.getContentNode().getName()));
                current++;
            }
        }

        eventBus.publish(new ArticlesProcessResultEvent(operationId));
    }

    private void reprocessArticle(Article article, String contextRoot) {
        Context context = processArticle(article.getText(), contextRoot);
        article.setOutputHTML(context.getOutput());
        article.setPluginCSSResources(context.getCSSResources());
        article.setPluginJSResources(createJSResourcesSet(context.getJSResources()));
        article.setPluginJSCodes(createJSCodesSet(context.getJSCodes()));
        article.setSearchableOutput(HTMLTagsFilter.trim(context.getOutput()));
        articleRepository.save(article);
    }

    @Override
    public List<ArticleDraftOverviewTO> getDraftsForUser(Long userId) {
        boolean isAdmin = userId == null ? false : userRepository.findById(userId).get().isAdmin();
        List<Article> articles = articleRepository.findDraftsForUser(userId, isAdmin);
        return articlesMapper.mapArticlesForDraftOverview(articles);
    }

    @Override
    public Integer getBackupTimeout() {
        ArticlesConfiguration configuration = new ArticlesConfiguration();
        configurationService.loadConfiguration(configuration);
        return configuration.getBackupTimeout();
    }

    private Path createAttachmentsDir(Path newPath) {
        try {
            return fileSystemService.createDirectoryWithPerms(newPath);
        } catch (IOException e) {
            throw new IllegalStateException("Nezdařilo se vytvořit přehled příloh", e);
        }
    }

    @Override
    public List<AttachmentTO> findAttachments(Long articleId) {
        Path path = getAttachmentsPath(articleId, true);
        try {
            return Files.list(path).map(this::mapPathToAttachmentTO).toList();
        } catch (IOException e) {
            throw new RuntimeException("Nezdařilo se získat přílohy článku " + articleId, e);
        }
    }

    private Path findAttachmentsRootPath() {
        ArticlesConfiguration configuration = new ArticlesConfiguration();
        configurationService.loadConfiguration(configuration);
        String rootDir = configuration.getAttachmentsDir();
        FileSystem fileSystem = fileSystemService.getFileSystem();
        Path rootPath = fileSystem.getPath(rootDir);
        return rootPath;
    }

    private Path getAttachmentsPath(Long articleId, boolean createIfDoesNotExists) {
        Path rootPath = findAttachmentsRootPath();
        if (!Files.exists(rootPath)) throw new IllegalStateException("Kořenový adresář modulu článků musí existovat");
        Path attachmentsPath = rootPath.resolve(ATTACHMENT_DIR_PREFIX + articleId);
        if (Files.exists(attachmentsPath)) return attachmentsPath;
        if (createIfDoesNotExists) return createAttachmentsDir(attachmentsPath);
        return null;
    }

    @Override
    public AttachmentsOperationResult saveDraftAttachment(Long draftId, Long existingArticleId, InputStream inputStream,
                                                          String name) {
        try {
            // kontrola, že nedojde ke kolizi při uložení a přesunu příloh z draftu do ostrého článku
            Path existingArticleDirPath = getAttachmentsPath(existingArticleId, false);
            if (existingArticleDirPath != null) {
                Path futureTargetPath = existingArticleDirPath.resolve(name);
                if (Files.exists(futureTargetPath)) {
                    // Tohle je potřeba pro případ, že byla smazána příloha z reálného článku (příprava smazání) a záhy
                    // byla příloha nahrána znova ... nemá smysl ji dávat do draftu, protože se vlastně nic nezměnilo.
                    // Řešením je tedy vrátit původní AttachmentTO se záznamem z existujího článku
                    AttachmentTO attachmentTO = mapPathToAttachmentTO(futureTargetPath);
                    return AttachmentsOperationResult.success(attachmentTO);
                }
            }

            Path draftDirPath = getAttachmentsPath(draftId, true);
            Path targetPath = draftDirPath.resolve(name);
            Files.copy(inputStream, targetPath);
            fileSystemService.grantPermissions(targetPath);
            AttachmentTO attachmentTO = mapPathToAttachmentTO(targetPath);
            attachmentTO.setDraft(true);
            return AttachmentsOperationResult.success(attachmentTO);
        } catch (FileAlreadyExistsException f) {
            return AttachmentsOperationResult.alreadyExists();
        } catch (IOException e) {
            return AttachmentsOperationResult.systemError();
        }
    }

    @Override
    public Path getAttachmentFilePath(Long articleId, String name) {
        Path attachmentsDirPath = getAttachmentsPath(articleId, false);
        if (attachmentsDirPath == null) return null;
        Path attachment = attachmentsDirPath.resolve(name);
        if (!attachment.normalize().startsWith(attachmentsDirPath))
            throw new IllegalArgumentException(ILLEGAL_PATH_ERR);
        return attachment;
    }

    private AttachmentTO mapPathToAttachmentTO(Path path) {
        AttachmentTO to = new AttachmentTO();
        to.setName(path.getFileName().toString());
        to.setPath(path);
        try {
            to.setNumericSize(Files.size(path));
            to.setSize(HumanBytesSizeFormatter.format(to.getNumericSize(), true));
        } catch (IOException e) {
            to.setSize("n/a");
        }
        try {
            to.setLastModified(
                    LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault()));
        } catch (IOException e) {
            to.setLastModified(null);
        }
        return to;
    }

    @Override
    public int renameAttachmentDirs() {
        Path rootPath = findAttachmentsRootPath();
        List<Article> articleList = articleRepository.findWithAttachments();
        int renamed = 0;
        for (Article article : articleList) {
            Path attachmentsDirPath = rootPath.resolve(article.getAttachmentsDirId());
            Path targetPath = rootPath.resolve(ATTACHMENT_DIR_PREFIX + article.getId());
            try {
                Files.move(attachmentsDirPath, targetPath);
            } catch (IOException e) {
                logger.error("Nezdařilo se přejmenovat adresář příloh článku {}", article.getId(), e);
            }
            articleRepository.clearAttachmentsDirId(article.getId());
            renamed++;
        }
        return renamed;
    }
}