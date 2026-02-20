package cz.gattserver.grass.songs.facades;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.model.interfaces.SongTO;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

 public interface SongsService {

	/**
	 * Získá počet písniček v DB
	 * 
	 * @param filterTO
	 *            filtr
	 */
	 int getSongsCount(SongOverviewTO filterTO);

	/**
	 * Získá všechny písničky
	 *
	 * @param filterTO   filtr
	 * @param sortOrders
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
	 * 
	 * @param id
	 */
	 void deleteSong(Long id);

	/**
	 * Provede import písničky ze souboru
	 * 
	 * @param in
	 * @param fileName
	 * @return
	 */
	 SongTO importSong(InputStream in, String fileName);

	/**
	 * Uloží akord
	 * 
	 * @param to
	 *            akord
	 * @return
	 */
	 ChordTO saveChord(ChordTO to);

	/**
	 * Smaže akord
	 * 
	 * @param id
	 */
	 void deleteChord(Long id);

	/**
	 * Vyhledá akordy dle filtru
	 * 
	 * @param filterTO
	 * @return
	 */
	 List<ChordTO> getChords(ChordTO filterTO);

	/**
	 * Vyhledá akord dle id
	 * 
	 * @param id
	 * @return
	 */
	 ChordTO getChordById(Long id);

	/**
	 * Vyhledá akord dle názvu
	 * 
	 * @param name
	 * @return
	 */
	 ChordTO getChordByName(String name);

     Path print(SongTO s, boolean twoColumn);
 }
