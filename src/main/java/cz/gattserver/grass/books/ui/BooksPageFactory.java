package cz.gattserver.grass.books.ui;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("booksPageFactory")
public class BooksPageFactory extends AbstractPageFactory {

	public BooksPageFactory() {
		super("books");
	}
}
