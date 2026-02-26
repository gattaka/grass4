package cz.gattserver.grass.songs.model;

import cz.gattserver.grass.songs.interfaces.ChordTO;

import java.util.List;

public interface ChordsRepositoryCustom {

	List<Chord> findAllOrderByName(ChordTO filterTO);
}
