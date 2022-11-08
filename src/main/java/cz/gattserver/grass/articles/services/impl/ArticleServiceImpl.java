package cz.gattserver.grass.articles.services.impl;

import com.vaadin.flow.data.provider.QuerySortOrder;
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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
public class ArticleServiceImpl implements ArticleService {

	private static final String ILLEGAL_PATH_ERR = "Podtečení adresáře příloh";

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

	private Context processArticle(String source, String contextRoot) {
		Validate.notNull(contextRoot, "ContextRoot nemůže být null");

		Lexer lexer = new Lexer(source);
		Parser parser = new ArticleParser();
		ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, contextRoot,
				pluginRegister.createRegisterSnapshot());

		// výstup
		Element tree = parser.parse(parsingProcessor);
		Context ctx = new ContextImpl();
		tree.apply(ctx);

		return ctx;
	}

	@Override
	public void deleteArticle(long id, boolean deleteAttachments) {
		Article article = articleRepository.findById(id).get();

		// smaž článek
		articleRepository.deleteById(id);

		// smaž jeho content node
		contentNodeFacade.deleteByContentId(ArticlesContentModule.ID, id);

		if (deleteAttachments && article.getAttachmentsDirId() != null) {
			// smaž jeho přílohy
			try {
				Path attachmentsPath = getAttachmentsPath(article.getAttachmentsDirId(), false);
				if (attachmentsPath != null) {
					try (Stream<Path> s = Files.walk(attachmentsPath)) {
						s.sorted(Comparator.reverseOrder()).forEach(p -> {
							try {
								Files.delete(p);
							} catch (IOException e) {
								logger.error("Chyba při mazání přílohy článku [" + article.getAttachmentsDirId() + "] ("
										+ p.getFileName().toString() + ")", e);
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

	@Override
	public long saveArticle(ArticlePayloadTO payload, long nodeId, long authorId) {
		return innerSaveArticle(payload, nodeId, authorId, true, false, null, null, null);
	}

	@Override
	public void modifyArticle(long articleId, ArticlePayloadTO payload, Integer partNumber) {
		innerSaveArticle(payload, null, null, true, false, articleId, partNumber, null);
	}

	@Override
	public long saveDraft(ArticlePayloadTO payload, long nodeId, long authorId, boolean asPreview) {
		return innerSaveArticle(payload, nodeId, authorId, asPreview, true, null, null, null);
	}

	@Override
	public long saveDraftOfExistingArticle(ArticlePayloadTO payload, long nodeId, long authorId, Integer partNumber,
			long originArticleId, boolean asPreview) {
		return innerSaveArticle(payload, nodeId, authorId, asPreview, true, null, partNumber, originArticleId);
	}

	@Override
	public void modifyDraft(long drafId, ArticlePayloadTO payload, boolean asPreview) {
		innerSaveArticle(payload, null, null, asPreview, true, drafId, null, null);
	}

	@Override
	public void modifyDraftOfExistingArticle(long drafId, ArticlePayloadTO payload, Integer partNumber,
			long originArticleId, boolean asPreview) {
		innerSaveArticle(payload, null, null, asPreview, true, drafId, partNumber, originArticleId);
	}

	private long innerSaveArticle(ArticlePayloadTO payload, Long nodeId, Long authorId, boolean process, boolean draft,
			Long existingId, Integer partNumber, Long draftSourceId) {

		Article article;
		if (existingId == null) {
			// vytvoř nový článek
			article = new Article();
			if (draft) {
				article.setPartNumber(partNumber);
			}
		} else {
			article = articleRepository.findById(existingId).orElse(null);
		}

		// nasetuj do něj vše potřebné
		if (process) {
			Context context = processArticle(payload.getText(), payload.getContextRoot());
			article.setOutputHTML(context.getOutput());
			article.setPluginCSSResources(context.getCSSResources());
			article.setPluginJSResources(createJSResourcesSet(context.getJSResources()));
			article.setPluginJSCodes(createJSCodesSet(context.getJSCodes()));
			article.setSearchableOutput(HTMLTagsFilter.trim(context.getOutput()));
		}
		article.setText(payload.getText());
		article.setAttachmentsDirId(payload.getAttachmentsDirId());

		// ulož ho a nasetuj jeho id
		article = articleRepository.save(article);

		if (existingId == null) {
			// vytvoř odpovídající content node
			Long contentNodeId = contentNodeFacade.save(ArticlesContentModule.ID, article.getId(), payload.getName(),
					payload.getTags(), payload.isPublicated(), nodeId, authorId, draft, null, draftSourceId);

			// ulož do článku referenci na jeho contentnode
			ContentNode contentNode = new ContentNode();
			contentNode.setId(contentNodeId);
			article.setContentNode(contentNode);
			articleRepository.save(article);
		} else {
			contentNodeFacade.modify(article.getContentNode().getId(), payload.getName(), payload.getTags(),
					payload.isPublicated());
		}

		return article.getId();
	}

	@Override
	public ArticleTO getArticleForDetail(long id) {
		Article article = articleRepository.findById(id).orElse(null);
		if (article == null)
			return null;
		return articlesMapper.mapArticleForDetail(article);
	}

	@Override
	public ArticleRESTTO getArticleForREST(Long id, Long userId) throws UnauthorizedAccessException {
		Article article = articleRepository.findById(id).orElse(null);
		if (article == null)
			return null;
		User user = userId == null ? null : userRepository.findById(userId).orElse(null);
		if (article.getContentNode().getPublicated() || user != null
				&& (user.isAdmin() || article.getContentNode().getAuthor().getId().equals(user.getId()))) {
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
		Collection<ContentTag> tagsDTOs = article.getContentNode().getContentTags();
		Set<String> tags = new HashSet<>();
		for (ContentTag tag : tagsDTOs)
			tags.add(tag.getName());

		ArticlePayloadTO payload = new ArticlePayloadTO(article.getContentNode().getName(), article.getText(), tags,
				article.getContentNode().getPublicated(), article.getAttachmentsDirId(), contextRoot);
		if (article.getContentNode().getDraft() == null || article.getContentNode().getDraft()) {
			if (article.getContentNode().getDraftSourceId() != null) {
				modifyDraftOfExistingArticle(article.getId(), payload, null,
						article.getContentNode().getDraftSourceId(), false);
			} else {
				modifyDraft(article.getId(), payload, false);
			}
		} else {
			modifyArticle(article.getId(), payload, null);
		}
	}

	@Override
	public List<ArticleDraftOverviewTO> getDraftsForUser(Long userId) {
		boolean isAdmin = userId == null ? false : userRepository.findById(userId).get().isAdmin();
		List<Article> articles = articleRepository.findDraftsForUser(userId, isAdmin);
		return articlesMapper.mapArticlesForDraftOverview(articles);
	}

	private Path createAttachmentsDir(Path rootPath) {
		try (Stream<Path> stream = Files.list(rootPath).sorted((p1, p2) -> p2.compareTo(p1))) {
			Iterator<Path> it = stream.iterator();
			while (it.hasNext()) {
				Path path = it.next();
				String fileName = path.getFileName().toString();
				Long val;
				try {
					val = Long.parseLong(fileName);
				} catch (NumberFormatException e) {
					throw new IllegalStateException("Nezdařilo se iterovat název adresáře příloh", e);
				}
				Path newPath = rootPath.resolve(String.valueOf(val + 1));
				if (!Files.exists(newPath))
					return fileSystemService.createDirectoryWithPerms(newPath);
			}
			return fileSystemService.createDirectoryWithPerms(rootPath.resolve("0"));
		} catch (IOException e) {
			throw new IllegalStateException("Nezdařilo se získat přehled příloh", e);
		}
	}

	private Path getAttachmentsPath(String attachmentsDirId, boolean createIfDoesNotExists) {
		ArticlesConfiguration configuration = new ArticlesConfiguration();
		configurationService.loadConfiguration(configuration);
		String rootDir = configuration.getAttachmentsDir();
		FileSystem fileSystem = fileSystemService.getFileSystem();
		Path rootPath = fileSystem.getPath(rootDir);
		if (!Files.exists(rootPath))
			throw new IllegalStateException("Kořenový adresář modulu článků musí existovat");
		if (attachmentsDirId != null) {
			Path attachmentsDirPath = rootPath.resolve(String.valueOf(attachmentsDirId));
			if (!Files.exists(attachmentsDirPath)) {
				if (!createIfDoesNotExists)
					return null;
				return createAttachmentsDir(rootPath);
			}
			return attachmentsDirPath;
		} else {
			if (!createIfDoesNotExists)
				return null;
			return createAttachmentsDir(rootPath);
		}
	}

	@Override
	public AttachmentsOperationResult saveAttachment(String attachmentsDirId, InputStream in, String name) {
		try {
			Path articleAttPath = getAttachmentsPath(attachmentsDirId, true);
			attachmentsDirId = articleAttPath.getFileName().toString();
			Path pathToSaveAs = articleAttPath.resolve(name).normalize();
			Files.copy(in, pathToSaveAs);
			fileSystemService.grantPermissions(pathToSaveAs);
			return AttachmentsOperationResult.success(attachmentsDirId);
		} catch (FileAlreadyExistsException f) {
			return AttachmentsOperationResult.alreadyExists();
		} catch (IOException e) {
			return AttachmentsOperationResult.systemError();
		}
	}

	@Override
	public AttachmentsOperationResult deleteAttachment(String attachmentsDirId, String name) {
		try {
			Path articleAttPath = getAttachmentsPath(attachmentsDirId, true);
			attachmentsDirId = articleAttPath.getFileName().toString();
			Files.delete(articleAttPath.resolve(name));
			return AttachmentsOperationResult.success(attachmentsDirId);
		} catch (IOException e) {
			return AttachmentsOperationResult.systemError();
		}
	}

	@Override
	public Path getAttachmentFilePath(String attachmentsDirId, String name) {
		Path attachmentsDirPath = getAttachmentsPath(attachmentsDirId, false);
		if (attachmentsDirPath == null)
			return null;
		Path attachment = attachmentsDirPath.resolve(name);
		if (!attachment.normalize().startsWith(attachmentsDirPath))
			throw new IllegalArgumentException(ILLEGAL_PATH_ERR);
		return attachment;
	}

	@Override
	public int listCount(String attachmentsDirId) {
		Path attachmentsDirPath = getAttachmentsPath(attachmentsDirId, false);
		if (attachmentsDirPath == null)
			return 0;
		try (Stream<Path> stream = Files.list(attachmentsDirPath)) {
			return (int) stream.count();
		} catch (IOException e) {
			throw new IllegalStateException("Nezdařilo se získat počet souborů", e);
		}
	}

	private AttachmentTO mapPathToAttachmentTO(Path path) {
		AttachmentTO to = new AttachmentTO().setName(path.getFileName().toString());
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
	public Stream<AttachmentTO> listing(String attachmentsDirId, int offset, int limit, List<QuerySortOrder> list) {
		Path attachmentsDirPath = getAttachmentsPath(attachmentsDirId, false);
		if (attachmentsDirPath == null)
			return Stream.empty();
		try (Stream<AttachmentTO> stream = Files.list(attachmentsDirPath).map(this::mapPathToAttachmentTO).skip(offset)
				.limit(limit)) {
			return stream.collect(Collectors.toList()).stream();
		} catch (IOException e) {
			throw new IllegalStateException("Nezdařilo se získat list souborů", e);
		}
	}
}
