package cz.gattserver.grass.books.model.dao;

import cz.gattserver.grass.books.model.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

}
