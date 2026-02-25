package cz.gattserver.grass.books.rest;

import cz.gattserver.grass.books.service.BooksService;
import cz.gattserver.grass.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.BookTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/ws/books")
public class BooksResource {

	private final BooksService booksService;

    public BooksResource(BooksService booksService) {
        this.booksService = booksService;
    }

    @RequestMapping("/list")
	public ResponseEntity<List<BookOverviewTO>> list(@RequestParam(value = "page") int page,
													 @RequestParam(value = "pageSize") int pageSize) {
		int count = booksService.countBooks();
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(booksService.getBooks(page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/count")
	public ResponseEntity<Integer> count() {
		return new ResponseEntity<>(booksService.countBooks(), HttpStatus.OK);
	}

	@RequestMapping("/book")
	public @ResponseBody BookTO beer(@RequestParam(value = "id") Long id) {
		return booksService.getBookById(id);
	}

}
