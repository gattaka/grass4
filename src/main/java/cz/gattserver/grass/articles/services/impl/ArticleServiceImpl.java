package cz.gattserver.grass.articles.services.impl;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.common.util.ServiceUtils;
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
import cz.gattserver.grass.articles.model.*;
import cz.gattserver.grass.articles.plugins.register.PluginRegisterService;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.core.model.repositories.ContentNodeContentTagRepository;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.modules.ArticlesContentModule;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.FileSystemService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ArticleRepository articleRepository;

    @Autowired
    private PluginRegisterService pluginRegister;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ArticleCSSResourceRepository articleCSSResourceRepository;

    @Autowired
    private ArticleJSResourceRepository articleJSResourceRepository;

    @Autowired
    private ArticleJSCodeRepository articleJSCodeRepository;

    @Autowired
    private ContentNodeContentTagRepository contentNodeContentTagRepository;

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
        Article article = innerSaveArticle(articleEditorTO, true, false, true);

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
        return innerSaveArticle(articleEditorTO, asPreview, true, false).getId();
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

    private SortedSet<ArticleJSResource> createJSResourcesSet(Long articleId, Set<String> resources) {
        int order = 0;
        SortedSet<ArticleJSResource> set = new TreeSet<>();
        for (String resource : resources)
            set.add(new ArticleJSResource(articleId, resource, order++));
        return set;
    }

    private SortedSet<ArticleJSCode> createJSCodesSet(Long articleId, Set<String> codes) {
        int order = 0;
        SortedSet<ArticleJSCode> set = new TreeSet<>();
        for (String code : codes)
            set.add(new ArticleJSCode(articleId, code, order++));
        return set;
    }

    private Article innerSaveArticle(ArticleEditorTO payload, boolean process, boolean draft,
                                     boolean replaceAttachmentsId) {
        Long existingArticleId = draft ? payload.getDraftId() : payload.getExistingArticleId();

        Article article;
        if (existingArticleId == null) {
            article = new Article();
        } else {
            article = articleRepository.findById(existingArticleId).orElse(null);
        }

        if (article == null) throw new IllegalStateException("Nenalezen článek dle id " + existingArticleId);

        article.setText(payload.getDraftText());

        // nasetuj do něj vše potřebné
        Context context = null;
        if (process) {
            context = processArticle(payload.getDraftText(), payload.getContextRoot());
            article.setOutputHTML(context.getOutput());
            article.setSearchableOutput(HTMLTagsFilter.trim(context.getOutput()));

            if (replaceAttachmentsId) {
                // Nahraď draft id ve všech attachment odkazech novým id ostrého článku
                String linkFrom = ArticlesConfiguration.ATTACHMENTS_PATH + "/" + payload.getDraftId();
                String linkTo = ArticlesConfiguration.ATTACHMENTS_PATH + "/" + article.getId();
                article.setText(article.getText().replaceAll(linkFrom, linkTo));
            }
        }

        // ulož ho a nasetuj jeho id
        article.setId(articleRepository.save(article).getId());

        if (process) processJSAndCSS(article.getId(), existingArticleId, context);

        if (existingArticleId == null) {
            Long contentNodeId =
                    contentNodeFacade.save(ArticlesContentModule.ID, article.getId(), payload.getDraftName(),
                            payload.getDraftTags(), payload.isDraftPublicated(), payload.getContentNodeId(),
                            securityService.getCurrentUser().getId(), draft, null, payload.getExistingArticleId());
            articleRepository.updateContentNodeId(article.getId(), contentNodeId);
        } else {
            contentNodeFacade.modify(article.getContentNodeId(), payload.getDraftName(), payload.getDraftTags(),
                    payload.isDraftPublicated());
        }

        return article;
    }

    private void processJSAndCSS(Long articleId, Long existingArticleId, Context context) {
        Set<String> cssResourcesSet = context.getCSSResources();
        if (existingArticleId != null) {
            cssResourcesSet = ServiceUtils.processDependentSetAndDeleteMissing(cssResourcesSet,
                    articleCSSResourceRepository.findByArticleId(existingArticleId),
                    set -> articleCSSResourceRepository.deleteCSSResources(existingArticleId, set));
        }
        articleCSSResourceRepository.saveAll(
                cssResourcesSet.stream().map(resource -> new ArticleCSSResource(articleId, resource)).toList());

        Set<String> jsResourcesSet = context.getJSResources();
        if (existingArticleId != null) {
            jsResourcesSet = ServiceUtils.processDependentSetAndDeleteMissing(jsResourcesSet,
                    articleJSResourceRepository.findByArticleId(existingArticleId),
                    set -> articleJSResourceRepository.deleteJSResources(existingArticleId, set));
        }
        articleJSResourceRepository.saveAll(createJSResourcesSet(articleId, jsResourcesSet));

        Set<String> jsCodesSet = context.getJSCodes();
        if (existingArticleId != null) {
            jsCodesSet = ServiceUtils.processDependentSetAndDeleteMissing(jsCodesSet,
                    articleJSCodeRepository.findByArticleId(existingArticleId),
                    set -> articleJSCodeRepository.deleteJSCodes(existingArticleId, set));
        }
        articleJSCodeRepository.saveAll(createJSCodesSet(articleId, jsCodesSet));
    }

    @Override
    public ArticleTO getArticleForDetail(Long id, Long userId, boolean isAdmin) {
        ArticleTO to = articleRepository.findByForDetailId(id, userId, isAdmin);
        if (to == null) return null;
        to.pluginCSSResources().addAll(articleCSSResourceRepository.findByArticleId(id));
        to.pluginJSResources().addAll(articleJSResourceRepository.findByArticleId(id));
        to.pluginJSCodes().addAll(articleJSCodeRepository.findByArticleId(id));
        to.contentTags().addAll(contentNodeContentTagRepository.findByContendNodeId(to.contentNodeId()));
        return to;
    }

    @Async
    @Override
    public void reprocessAllArticles(UUID operationId, String contextRoot) {
        int total = (int) articleRepository.count();
        eventBus.publish(new ArticlesProcessStartEvent(total));
        List<Long> ids = articleRepository.findAllIds();
        int current = 0;
        for (Long id : ids) {
            ArticleTO articleTO = getArticleForDetail(id, null, true);
            Context context = processArticle(articleTO.text(), contextRoot);
            articleRepository.updateOutputs(articleTO.id(), context.getOutput(),
                    HTMLTagsFilter.trim(context.getOutput()));
            processJSAndCSS(id, id, context);
            eventBus.publish(new ArticlesProcessProgressEvent("(" + current + "/" + total + ") " + articleTO.name()));
            current++;
        }

        eventBus.publish(new ArticlesProcessResultEvent(operationId));
    }

    @Override
    public List<ArticleDraftOverviewTO> getDraftsForUser(Long userId) {
        return articleRepository.findDraftsForUser(userId);
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
        return fileSystem.getPath(rootDir);
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

}