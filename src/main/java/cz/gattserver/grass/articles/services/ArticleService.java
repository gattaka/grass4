package cz.gattserver.grass.articles.services;

import cz.gattserver.grass.articles.AttachmentsOperationResult;
import cz.gattserver.grass.articles.editor.parser.interfaces.*;
import cz.gattserver.grass.core.exception.UnauthorizedAccessException;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface ArticleService {

    Long saveArticle(ArticleEditorTO articleEditorTO);

    Long saveDraft(ArticleEditorTO articleEditorTO, boolean asPreview);

    /**
     * Smaže článek
     *
     * @param id článek ke smazání
     */
    void deleteArticle(Long id);

    /**
     * Získá článek dle jeho identifikátoru
     *
     * @param id identifikátor
     * @return DTO článku
     */
    ArticleTO getArticleForDetail(Long id);

    /**
     * Získá článek pro REST dle jeho identifikátoru
     *
     * @param id     identifikátor
     * @param userId id přihlášeného uživatele
     * @return DTO článku
     * @throws UnauthorizedAccessException pokud uživatel nemá právo na přístup k obsahu
     */
    ArticleRESTTO getArticleForREST(Long id, Long userId) throws UnauthorizedAccessException;

    /**
     * Spustí přegenerování všech článků
     *
     * @param contextRoot kořenová adresa, od které mají být vytvoření linky na CSS a JS
     *                    zdroje, jež může článek na sobě mít
     */
    void reprocessAllArticles(UUID operationId, String contextRoot);

    /**
     * Získá všechny rozpracované články viditelné daným uživatelem
     *
     * @param userId id uživatele, kterým je omezena viditelnost na rozpracované
     *               články
     * @return list konceptů
     */
    List<ArticleDraftOverviewTO> getDraftsForUser(Long userId);

    List<AttachmentTO> findAttachments(Long articleId);

    /**
     * Získá z konfigurace dobu intervalu (v sekundách) pravidelné zálohy rozpracovaných článků.
     *
     * @return
     */
    Integer getBackupTimeout();

    AttachmentsOperationResult saveDraftAttachment(Long draftId, Long existingArticleId, InputStream inputStream,
                                                   String name);

    Path getAttachmentFilePath(Long articleId, String name);

    int renameAttachmentDirs(String contextRoot);
}
