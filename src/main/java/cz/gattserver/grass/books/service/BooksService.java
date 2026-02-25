package cz.gattserver.grass.books.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.grass.books.model.interfaces.BookFilterTO;
import cz.gattserver.grass.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.BookTO;

import java.util.List;

public interface BooksService {

	/**
	 * Smaže knihu
	 */
	void deleteBook(Long id);

	/**
	 * Získá počet knih v DB
	 * 
	 */
	int countBooks();

	/**
	 * Získá všechny knihy
	 */
	List<BookOverviewTO> getBooks(int offset, int limit);

	/**
	 * Získá počet knih v DB
	 */
	int countBooks(BookFilterTO filterTO);

	/**
	 * Získá všechny knihy
	 */
	List<BookOverviewTO> getBooks(BookFilterTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder);

	/**
	 * Získá knihu dle id
	 */
	BookTO getBookById(Long id);

	/**
	 * Založ/uprav novou knihu
	 */
	BookTO saveBook(BookTO to);

}