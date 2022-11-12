package cz.gattserver.grass.songs.model.dao;

import cz.gattserver.grass.songs.model.domain.Chord;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;

import java.util.List;

public interface ChordsRepositoryCustom {

	List<Chord> findAllOrderByName(ChordTO filterTO);
}
