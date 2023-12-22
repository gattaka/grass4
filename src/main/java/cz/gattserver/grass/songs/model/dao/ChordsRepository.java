package cz.gattserver.grass.songs.model.dao;

import cz.gattserver.grass.songs.model.domain.Chord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChordsRepository extends JpaRepository<Chord, Long>, ChordsRepositoryCustom {

	@Query("select s from CHORD s order by s.name asc")
	List<Chord> findAllOrderByNamePageable(Pageable pageRequest);

	@Query("select s from CHORD s where LOWER(s.name) = LOWER(?1)")
	Chord findByName(String name);

}
