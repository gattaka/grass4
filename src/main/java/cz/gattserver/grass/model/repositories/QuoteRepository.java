package cz.gattserver.grass.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.model.domain.Quote;

public interface QuoteRepository extends JpaRepository<Quote, Long>, QuoteRepositoryCustom {

	@Query("select q from QUOTE q where LOWER(name) like ?1")
	List<Quote> findLike(String filter);

}
