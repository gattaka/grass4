package cz.gattserver.grass.songs.model.dao;

import cz.gattserver.grass.songs.model.domain.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SongsRepository extends JpaRepository<Song, Long>, SongsRepositoryCustom {

	@Query("select s from SONG s order by name asc")
	List<Song> findAllOrderByNamePageable(Pageable pageRequest);

}
