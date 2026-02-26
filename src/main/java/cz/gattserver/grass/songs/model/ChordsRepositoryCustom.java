package cz.gattserver.grass.songs.model;

import cz.gattserver.grass.songs.interfaces.ChordTO;

import java.util.List;

public interface ChordsRepositoryCustom {

	List<ChordTO> findAllOrderByName(ChordTO filterTO);

    ChordTO findAndMapById(Long id);

    ChordTO findByName(String name);

}