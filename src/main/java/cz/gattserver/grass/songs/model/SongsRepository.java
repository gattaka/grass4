package cz.gattserver.grass.songs.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SongsRepository extends JpaRepository<Song, Long>, SongsRepositoryCustom {

}
