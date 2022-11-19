package cz.gattserver.grass.language.facades;

import java.util.List;

import com.vaadin.flow.data.provider.QuerySortOrder;

import cz.gattserver.grass.language.model.domain.ItemType;
import cz.gattserver.grass.language.model.dto.CrosswordTO;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;
import cz.gattserver.grass.language.model.dto.LanguageTO;
import cz.gattserver.grass.language.model.dto.StatisticsTO;

public interface LanguageFacade {

	/**
	 * Získá přehled všech jazyků
	 * 
	 * @return jazyky
	 */
	List<LanguageTO> getLanguages();

	/**
	 * Uloží jazyk
	 * 
	 * @param languageTO
	 *            to jazyka
	 * @return db id, které bylo jazyku přiděleno
	 */
	long saveLanguage(LanguageTO languageTO);

	/**
	 * Získá počet všech záznamů
	 * 
	 * @param filterTO
	 *            filtrovací TO
	 * @return počet záznamů
	 */
	int countLanguageItems(LanguageItemTO filterTO);

	/**
	 * Získá všechny záznamy (stránkované)
	 * 
	 * @param filterTO
	 *            filtrovací TO
	 * @param page
	 *            stránka
	 * @param size
	 *            velikost stránky
	 * @param sortOrder
	 *            info o řazení
	 * @return list záznamů
	 */
	List<LanguageItemTO> getLanguageItems(LanguageItemTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder);

	/**
	 * Získá záznamy na zkoušení
	 * 
	 * @param languageId
	 *            jazyk v rámci kterého se bude zkoušet
	 * @param type
	 *            typ záznamů, které se budou zkoušet, může být
	 *            <code>null</code>
	 * @param minRating
	 *            minimální úspěšnost
	 * @param maxRatingExclusive
	 *            maximální (nezahrnuto) úspěšnost
	 * @param maxCount
	 *            maximální počet záznamů, který se má vrátit
	 * @return list záznamů k přezkoušení
	 */
	List<LanguageItemTO> getLanguageItemsForTest(long languageId, double minRating, double maxRatingExclusive,
			int maxCount, ItemType type);

	/**
	 * Získá záznam dle id
	 */
	LanguageItemTO getLanguageItemById(Long id);

	/**
	 * Získá záznam dle obsahu a id jazyka
	 */
	LanguageItemTO getLanguageItemByContent(long languageId, String content);

	/**
	 * Uloží záznam
	 */
	Long saveLanguageItem(LanguageItemTO itemTO);

	/**
	 * Aktualizuje stav záznamu po zkoušení
	 * 
	 * @param item
	 *            záznam
	 * @param výsledek
	 *            zkoušení
	 */
	void updateItemAfterTest(LanguageItemTO item, boolean success);

	/**
	 * Smaže daný záznam
	 * 
	 * @param item
	 */
	void deleteLanguageItem(LanguageItemTO item);

	/**
	 * Připraví křížovku
	 * 
	 * @param filterTO
	 * @param size
	 * @return křížovka
	 */
	CrosswordTO prepareCrossword(LanguageItemTO filterTO, int size);

	/**
	 * Zjistí % úspěšnosti daného jazyka, na daném typu zkoušených věcí
	 * 
	 * @param type
	 *            typ zkoušených věcí
	 * @param langId
	 *            id jazyka
	 * @return % úspěšnost
	 */
	Float getSuccessRateOfLanguageAndType(ItemType type, Long langId);

	/**
	 * Změní jazykovou kategorii položky
	 * 
	 * @param item
	 *            položka
	 * @param lang
	 *            nový jazyk
	 */
	void moveLanguageItemTo(LanguageItemTO item, LanguageTO lang);

	/**
	 * Získá přehled statistiky záznamů (stránkované)
	 * 
	 * @param type
	 *            typ věcí
	 * @param languageId
	 *            id jazyka
	 * @return list záznamů
	 */
	List<StatisticsTO> getStatisticsItems(ItemType type, Long languageId);

}
