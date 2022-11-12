package cz.gattserver.grass.songs.util;

import cz.gattserver.grass.songs.model.domain.Chord;
import cz.gattserver.grass.songs.model.domain.Song;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.model.interfaces.SongTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <b>Mapper pro různé typy.</b>
 * 
 * <p>
 * Je potřeba aby byl volán na objektech s aktivními proxy objekty. To znamená,
 * že před tímto mapperem nedošlo k uzavření session, ve které byl původní
 * objekt pořízen.
 * </p>
 * 
 * <p>
 * Mapper využívá proxy objekty umístěné v atributech předávaných entit. Během
 * mapování tak může docházet k dotazům na DB, které produkují tyto proxy
 * objekty a které se bez původní session mapovaného objektu neobejdou.
 * </p>
 * 
 * @author gatt
 * 
 */
@Component("songsMapper")
public class Mapper {

	/**
	 * Převede {@link Song} na {@link SongTO}
	 * 
	 * @param e
	 * @return
	 */
	public SongTO mapSong(Song e) {
		if (e == null)
			return null;

		SongTO song = new SongTO();

		song.setId(e.getId());
		song.setName(e.getName());
		song.setAuthor(e.getAuthor());
		song.setYear(e.getYear());
		song.setText(e.getText());
		song.setPublicated(e.getPublicated() == null ? true : e.getPublicated());
		song.setEmbedded(e.getEmbedded());

		return song;
	}

	/**
	 * Převede list {@link Song} na list {@link SongTO}
	 * 
	 * @param songs
	 * @return
	 */
	public List<SongOverviewTO> mapSongs(Collection<Song> songs) {
		if (songs == null)
			return new ArrayList<>();

		List<SongOverviewTO> songsTOs = new ArrayList<SongOverviewTO>();
		for (Song song : songs)
			songsTOs.add(new SongOverviewTO(song.getName(), song.getAuthor(), song.getYear(), song.getId(),
					song.getPublicated()));

		return songsTOs;
	}

	/**
	 * Převede {@link SongTO} na {@link Song}
	 * 
	 * @param e
	 * @return
	 */
	public Song mapSong(SongTO e) {
		if (e == null)
			return null;

		Song song = new Song();

		song.setId(e.getId());
		song.setName(e.getName());
		song.setAuthor(e.getAuthor());
		song.setYear(e.getYear());
		song.setText(e.getText());
		song.setPublicated(e.getPublicated());
		song.setEmbedded(e.getEmbedded());

		return song;
	}

	/**
	 * Převede {@link Chord} na {@link ChordTO}
	 * 
	 * @param e
	 * @return
	 */
	public ChordTO mapChord(Chord e) {
		if (e == null)
			return null;

		ChordTO chord = new ChordTO();

		chord.setId(e.getId());
		chord.setName(e.getName());
		chord.setConfiguration(e.getConfiguration());

		return chord;
	}

	/**
	 * Převede {@link ChordTO} na {@link Chord}
	 * 
	 * @param e
	 * @return
	 */
	public Chord mapChord(ChordTO e) {
		if (e == null)
			return null;

		Chord chord = new Chord();

		chord.setId(e.getId());
		chord.setName(e.getName());
		chord.setConfiguration(e.getConfiguration());

		return chord;
	}

	/**
	 * Převede list {@link Chord} na list {@link ChordTO}
	 * 
	 * @param chords
	 * @return
	 */
	public List<ChordTO> mapChords(Collection<Chord> chords) {
		if (chords == null)
			return new ArrayList<>();

		List<ChordTO> chordsTOs = new ArrayList<ChordTO>();
		for (Chord chord : chords)
			chordsTOs.add(new ChordTO(chord.getName(), chord.getConfiguration(), chord.getId()));

		return chordsTOs;
	}

}