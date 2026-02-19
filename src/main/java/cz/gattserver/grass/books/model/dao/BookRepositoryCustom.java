package cz.gattserver.grass.books.model.dao;

import com.querydsl.core.types.OrderSpecifier;
import cz.gattserver.grass.books.model.interfaces.BookFilterTO;
import cz.gattserver.grass.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.BookTO;

import java.util.List;

public interface BookRepositoryCustom {

	long countBooks(BookFilterTO filterTO);

	List<BookOverviewTO> findBooks(BookFilterTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	BookTO findBookById(Long id);

}