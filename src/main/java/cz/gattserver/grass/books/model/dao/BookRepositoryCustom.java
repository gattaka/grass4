package cz.gattserver.grass.books.model.dao;

import com.querydsl.core.types.OrderSpecifier;
import cz.gattserver.grass.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.BookTO;

import java.util.List;

public interface BookRepositoryCustom {

	long countBooks(BookOverviewTO filterTO);

	List<BookOverviewTO> findBooks(BookOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	BookTO findBookById(Long id);

}
