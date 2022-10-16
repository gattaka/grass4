package cz.gattserver.grass.services;

import java.util.List;

import cz.gattserver.grass.interfaces.QuoteTO;

public interface QuotesService {

	/**
	 * Uloží novou hlášku
	 * 
	 * @param content
	 *            obsah hlášky
	 * @return id hlášky
	 */
	public long createQuote(String content);

	/**
	 * Upraví existující hlášku
	 * 
	 * @param quoteId
	 *            id existující hlášky
	 * @param modifiedContent
	 *            upravený obsah
	 */
	public void modifyQuote(long quoteId, String modifiedContent);

	/**
	 * Získá všechny hlášky a vrátí je jako list {@link QuoteTO}
	 * 
	 * @param filter
	 *            filtr
	 * @return list hlášek
	 */
	public List<QuoteTO> getQuotes(String filter);

	/**
	 * Vybere náhodně hlášku a vrátí její text
	 * 
	 * @return náhodná hláška
	 */
	public String getRandomQuote();

	/**
	 * Smaže hlášku
	 * 
	 * @param quoteId
	 *            id hlášky ke smazání
	 */
	public void deleteQuote(long quoteId);

}
