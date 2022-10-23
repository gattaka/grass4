package cz.gattserver.grass.campgames.model.repositories;

import java.util.Collection;
import java.util.List;

import cz.gattserver.grass.campgames.model.domain.Campgame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CampgameRepository extends JpaRepository<Campgame, Long>, CampgameRepositoryCustom {

	public List<Campgame> findByKeywordsId(Long id);

	@Query("select i from CAMPGAME i inner join i.keywords keywords where keywords.name in ?1")
	public List<Campgame> getCampgamesByKeywords(Collection<String> keywords);

}
