package cz.gattserver.grass.songs.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChordsRepository extends JpaRepository<Chord, Long>, ChordsRepositoryCustom {

}