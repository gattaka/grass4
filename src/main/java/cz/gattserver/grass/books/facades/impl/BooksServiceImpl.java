package cz.gattserver.grass.books.facades.impl;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.grass.books.facades.BooksService;
import cz.gattserver.grass.books.model.dao.BookRepository;
import cz.gattserver.grass.books.model.domain.Book;
import cz.gattserver.grass.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.BookTO;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Component
public class BooksServiceImpl implements BooksService {

	@Autowired
	private BookRepository bookRepository;

	@Override
	public void deleteBook(Long id) {
		bookRepository.deleteById(id);
	}

	@Override
	public int countBooks() {
		return (int) bookRepository.countBooks(null);
	}

	@Override
	public List<BookOverviewTO> getBooks(int page, int size) {
		return bookRepository.findBooks(null, page * size, size, null);
	}

	@Override
	public int countBooks(BookOverviewTO filterTO) {
		return (int) bookRepository.countBooks(filterTO);
	}

	@Override
	public List<BookOverviewTO> getBooks(BookOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		return bookRepository.findBooks(filterTO, offset, limit, QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public BookTO getBookById(Long id) {
		return bookRepository.findBookById(id);
	}

	private Book createBook(BookTO to) {
		Book d = new Book();
		d.setName(to.getName());
		d.setAuthor(to.getAuthor());
		d.setRating(to.getRating());
		d.setImage(to.getImage());
		d.setDescription(to.getDescription());
		d.setYear(to.getYear());
		d.setId(to.getId());
		return d;
	}

	@Override
	public BookTO saveBook(BookTO to) {
		Book book = createBook(to);
		book = bookRepository.save(book);

		to.setId(book.getId());
		return to;
	}

}
