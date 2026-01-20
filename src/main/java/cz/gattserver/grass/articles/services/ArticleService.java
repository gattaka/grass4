package cz.gattserver.grass.articles.services;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.grass.articles.AttachmentsOperationResult;
import cz.gattserver.grass.articles.editor.parser.interfaces.*;
import cz.gattserver.grass.core.exception.UnauthorizedAccessException;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface ArticleService {

	/**
	 * Uloží nový článek
	 * 
	 * @param payload
	 *            obsahové informace článku
	 * @param nodeId
	 *            id kategorie, do které je článek ukládán
	 * @param authorId
	 *            id uživatele, který článek vytvořil
	 * @return id uloženého článku, pokud se operace zdařila
	 */
	long saveArticle(ArticlePayloadTO payload, long nodeId, long authorId);

	/**
	 * Upraví článek
	 * 
	 * @param articleId
	 *            id upravovaného článku
	 * @param payload
	 *            obsahové informace článku
	 * @param partNumber
	 *            číslo části článku (číslováno od 0), je-li upravována pouze
	 *            jeho část, může být <code>null</code>, pokud je upravován celý
	 *            článek
	 */
	void modifyArticle(long articleId, ArticlePayloadTO payload, Integer partNumber);

	/**
	 * Uloží koncept článku z vytváření nového článku
	 * 
	 * @param payload
	 *            obsahové informace článku
	 * @param nodeId
	 *            id kategorie, do které je článek ukládán
	 * @param authorId
	 *            id uživatele, který článek vytvořil
	 * @param asPreview
	 *            <code>true</code>, pokud je koncept vytvářen za účelem náhledu
	 * @return id uloženého konceptu, pokud se operace zdařila
	 */
	long saveDraft(ArticlePayloadTO payload, long nodeId, long authorId, boolean asPreview);

	/**
	 * Uloží koncept článku z upravovaného článku
	 * 
	 * @param payload
	 *            obsahové informace článku
	 * @param nodeId
	 *            id kategorie, do které je článek ukládán
	 * @param authorId
	 *            id uživatele, který článek vytvořil
	 * @param partNumber
	 *            číslo části článku (číslováno od 0), je-li upravována pouze
	 *            jeho část, může být <code>null</code>, pokud je upravován celý
	 *            článek
	 * @param originArticleId
	 *            id článku, k jehož úpravě je ukládán tento koncept
	 * @param asPreview
	 *            <code>true</code>, pokud je koncept vytvářen za účelem náhledu
	 * @return id uloženého konceptu, pokud se operace zdařila
	 */
	long saveDraftOfExistingArticle(ArticlePayloadTO payload, long nodeId, long authorId, Integer partNumber,
			long originArticleId, boolean asPreview);

	/**
	 * Upraví koncept článku z vytváření nového článku
	 * 
	 * @param drafId
	 *            id upravovaného konceptu
	 * @param payload
	 *            obsahové informace článku
	 * @param asPreview
	 *            <code>true</code>, pokud je koncept upravován za účelem
	 *            náhledu
	 */
	void modifyDraft(long drafId, ArticlePayloadTO payload, boolean asPreview);

	/**
	 * Upraví koncept článku z upravovaného článku
	 * 
	 * @param drafId
	 *            id upravovaného konceptu
	 * @param payload
	 *            obsahové informace článku
	 * @param partNumber
	 *            číslo části článku (číslováno od 0), je-li upravována pouze
	 *            jeho část, může být <code>null</code>, pokud je upravován celý
	 *            článek
	 * @param originArticleId
	 *            id článku, k jehož úpravě je ukládán tento koncept
	 * @param asPreview
	 *            <code>true</code>, pokud je koncept upravován za účelem
	 *            náhledu
	 */
	void modifyDraftOfExistingArticle(long drafId, ArticlePayloadTO payload, Integer partNumber, long originArticleId,
			boolean asPreview);

	/**
	 * Smaže článek
	 * 
	 * @param id
	 *            článek ke smazání
	 */
	void deleteArticle(long id, boolean deleteAttachments);

	/**
	 * Získá článek dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @return DTO článku
	 */
	ArticleTO getArticleForDetail(long id);

	/**
	 * Získá článek pro REST dle jeho identifikátoru
	 * 
	 * @param id
	 *            identifikátor
	 * @param userId
	 *            id přihlášeného uživatele
	 * @return DTO článku
	 * @throws UnauthorizedAccessException
	 *             pokud uživatel nemá právo na přístup k obsahu
	 */
	ArticleRESTTO getArticleForREST(Long id, Long userId) throws UnauthorizedAccessException;

	/**
	 * Spustí přegenerování všech článků
	 * 
	 * @param contextRoot
	 *            kořenová adresa, od které mají být vytvoření linky na CSS a JS
	 *            zdroje, jež může článek na sobě mít
	 */
	void reprocessAllArticles(UUID operationId, String contextRoot);

	/**
	 * Získá všechny rozpracované články viditelné daným uživatelem
	 * 
	 * @param userId
	 *            id uživatele, kterým je omezena viditelnost na rozpracované
	 *            články
	 * @return list konceptů
	 */
	List<ArticleDraftOverviewTO> getDraftsForUser(Long userId);

	/**
	 * Uloží přílohu článku
	 * 
	 * @param attachmentsDirId
	 *            id úložiště
	 * @param in
	 *            vstupní proud dat
	 * @param name
	 *            cesta k souboru z aktuálního adresáře pod kterou bude soubor
	 *            uložen
	 * @return výsledek operace
	 */
	AttachmentsOperationResult saveAttachment(String attachmentsDirId, InputStream in, String name);

	/**
	 * Smaže přílohu článku
	 * 
	 * @param attachmentsDirId
	 *            id úložiště
	 * @param name
	 *            název přílohy
	 * @return 
	 */
	AttachmentsOperationResult deleteAttachment(String attachmentsDirId, String name);

	/**
	 * Získá přílohu článku
	 * 
	 * @param attachmentsDirId
	 *            id úložiště
	 * @param name
	 *            jméno přílohy
	 * @return cesta k souboru přílohy
	 */
	Path getAttachmentFilePath(String attachmentsDirId, String name);

	/**
	 * Získá počet příloh daného článku
	 * 
	 * @param attachmentsDirId
	 *            id úložiště
	 * @return počet příloh
	 */
	int listCount(String attachmentsDirId);

	/**
	 * Získá cesty k přílohám článku
	 * 
	 * @param attachmentsDirId
	 *            id úložiště
	 * @param offset
	 *            offset
	 * @param limit
	 *            limit
	 * @param list
	 *            řazení
	 * @return cesty k přílohám
	 */
	Stream<AttachmentTO> listing(String attachmentsDirId, int offset, int limit, List<QuerySortOrder> list);


    /**
     * Získá z konfigurace dobu intervalu (v sekundách) pravidelné zálohy rozpracovaných článků.
     * @return
     */
    Integer getBackupTimeout();
}
