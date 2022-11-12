package cz.gattserver.grass.songs.facades;

import com.vaadin.flow.component.grid.GridSortOrder;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.model.interfaces.SongTO;

import java.io.InputStream;
import java.util.List;

public interface SongsService {

	/**
	 * Získá počet písniček v DB
	 * 
	 * @param filterTO
	 *            filtr
	 */
	public int getSongsCount(SongOverviewTO filterTO);

	/**
	 * Získá všechny písničky
	 * 
	 * @param filterTO
	 *            filtr
	 */
	public List<SongOverviewTO> getSongs(SongOverviewTO filterTO, int offset, int limit);
	
	/**
	 * Získá všechny písničky
	 * 
	 * @param filterTO
	 *            filtr
	 * @param list 
	 */
	public List<SongOverviewTO> getSongs(SongOverviewTO filterTO, List<GridSortOrder<SongOverviewTO>> list);

	/**
	 * Získá písničku dle id
	 */
	public SongTO getSongById(Long id);

	/**
	 * Založí/uprav novou písničku
	 */
	public SongTO saveSong(SongTO songDTO);

	/**
	 * Převede každý "< br/ >" nebo "< br >" v textu na EOL znak
	 */
	public String breaklineToEol(String text);

	/**
	 * Převede každý EOL znak v textu na "< br/ >"
	 */
	public String eolToBreakline(String text);

	/**
	 * Smaže písničku
	 * 
	 * @param id
	 */
	public void deleteSong(Long id);

	/**
	 * Provede import písničky ze souboru
	 * 
	 * @param in
	 * @param fileName
	 * @return
	 */
	public SongTO importSong(InputStream in, String fileName);

	/**
	 * Uloží akord
	 * 
	 * @param to
	 *            akord
	 * @return
	 */
	public ChordTO saveChord(ChordTO to);

	/**
	 * Smaže akord
	 * 
	 * @param id
	 */
	public void deleteChord(Long id);

	/**
	 * Vyhledá akordy dle filtru
	 * 
	 * @param filterTO
	 * @return
	 */
	public List<ChordTO> getChords(ChordTO filterTO);

	/**
	 * Vyhledá akord dle id
	 * 
	 * @param id
	 * @return
	 */
	public ChordTO getChordById(Long id);

	/**
	 * Vyhledá akord dle názvu
	 * 
	 * @param name
	 * @return
	 */
	public ChordTO getChordByName(String name);

}
