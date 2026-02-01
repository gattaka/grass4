package cz.gattserver.grass.articles.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.grass.articles.AttachmentsOperationResult;
import cz.gattserver.grass.articles.editor.parser.interfaces.*;
import cz.gattserver.grass.core.exception.UnauthorizedAccessException;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface ArticleService {

    long saveArticle(ArticleEditorTO articleEditorTO);

    long saveDraft(ArticleEditorTO articleEditorTO, boolean asPreview);

    /**
     * Smaže článek
     *
     * @param id článek ke smazání
     */
    void deleteArticle(long id);

    /**
     * Získá článek dle jeho identifikátoru
     *
     * @param id identifikátor
     * @return DTO článku
     */
    ArticleTO getArticleForDetail(long id);

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

    AttachmentsOperationResult saveAttachment(Long draftId, InputStream inputStream, String name);
}
