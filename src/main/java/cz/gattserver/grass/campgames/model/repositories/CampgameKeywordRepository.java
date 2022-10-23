package cz.gattserver.grass.campgames.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass.campgames.model.domain.CampgameKeyword;

public interface CampgameKeywordRepository extends JpaRepository<CampgameKeyword, Long> {

	@Query("select t.name from CAMPGAME_KEYWORD t order by name asc")
	List<String> findNames();

	@Query("select t from CAMPGAME_KEYWORD t order by name asc")
	List<CampgameKeyword> findListOrderByName();

	@Query("select t from CAMPGAME_KEYWORD t where t.name = ?1")
	CampgameKeyword findByName(String name);

}
