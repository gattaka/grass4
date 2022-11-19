package cz.gattserver.grass.language.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.language.model.domain.Language;

public interface LanguageRepository extends JpaRepository<Language, Long> {

	@Query("select l from LANGUAGE l order by name asc")
	List<Language> findAllSortByName();
}
