package cz.gattserver.grass.books.model.dao;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.books.model.domain.Book;
import cz.gattserver.grass.books.model.domain.QBook;
import cz.gattserver.grass.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.BookTO;
import cz.gattserver.grass.books.model.interfaces.QBookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.QBookTO;
import cz.gattserver.grass.core.model.util.PredicateBuilder;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class BookRepositoryCustomImpl implements BookRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateBooks(BookOverviewTO filterTO) {
		QBook b = QBook.book;
		PredicateBuilder builder = new PredicateBuilder();
		if (filterTO != null) {
			builder.iLike(b.author, filterTO.getAuthor());
			builder.iLike(b.name, filterTO.getName());
		}
		return builder.getBuilder();
	}

	@Override
	public long countBooks(BookOverviewTO filterTO) {
		JPAQuery<Book> query = new JPAQuery<>(entityManager);
		QBook b = QBook.book;
		return query.from(b).where(createPredicateBooks(filterTO)).fetchCount();
	}

	@Override
	public List<BookOverviewTO> findBooks(BookOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order) {
		JPAQuery<BookOverviewTO> query = new JPAQuery<>(entityManager);
		QBook b = QBook.book;
		if (order == null)
			order = new OrderSpecifier[] { new OrderSpecifier<>(Order.DESC, b.name) };
		query.offset(offset).limit(limit);
		return query.select(new QBookOverviewTO(b.id, b.name, b.author, b.rating, b.year)).from(b)
				.where(createPredicateBooks(filterTO)).orderBy(order).fetch();
	}

	@Override
	public BookTO findBookById(Long id) {
		JPAQuery<BookTO> query = new JPAQuery<>(entityManager);
		QBook b = QBook.book;
		return query.select(new QBookTO(b.id, b.name, b.author, b.rating, b.year, b.image, b.description)).from(b)
				.where(b.id.eq(id)).fetchOne();
	}

}
