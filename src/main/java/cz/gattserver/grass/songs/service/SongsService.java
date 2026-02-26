package cz.gattserver.grass.songs.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.grass.songs.interfaces.ChordTO;
import cz.gattserver.grass.songs.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.interfaces.SongTO;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

 public interface SongsService {

	/**
	 * Získá počet písniček v DB
	 */
	 int getSongsCount(SongOverviewTO filterTO);

	/**
	 * Získá všechny písničky
	 */
	 List<SongOverviewTO> getSongs(SongOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrders);

	 List<Long> getSongsIds(SongOverviewTO filterTO, List<QuerySortOrder> sortOrders);
	
	/**
	 * Získá písničku dle id
	 */
	 SongTO getSongById(Long id);

	/**
	 * Založí/uprav novou písničku
	 */
	 SongTO saveSong(SongTO songDTO);

	/**
	 * Převede každý "< br/ >" nebo "< br >" v textu na EOL znak
	 */
	 String breaklineToEol(String text);

	/**
	 * Převede každý EOL znak v textu na "< br/ >"
	 */
	 String eolToBreakline(String text);

	/**
	 * Smaže písničku
	 */
	 void deleteSong(Long id);

	/**
     * Provede import písničky ze souboru
     */
	 void importSong(InputStream in, String fileName);

	/**
	 * Uloží akord
	 */
	 ChordTO saveChord(ChordTO to);

	/**
	 * Smaže akord
	 */
	 void deleteChord(Long id);

	/**
	 * Vyhledá akordy dle filtru
	 */
	 List<ChordTO> getChords(ChordTO filterTO);

     /**
	 * Vyhledá akord dle názvu
	 */
	 ChordTO getChordByName(String name);

     Path print(SongTO s, boolean twoColumn);
 }
