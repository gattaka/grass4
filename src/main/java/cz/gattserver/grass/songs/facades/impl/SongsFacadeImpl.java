package cz.gattserver.grass.songs.facades.impl;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.songs.facades.SongsService;
import cz.gattserver.grass.songs.model.dao.ChordsRepository;
import cz.gattserver.grass.songs.model.dao.SongsRepository;
import cz.gattserver.grass.songs.model.domain.Chord;
import cz.gattserver.grass.songs.model.domain.Song;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.model.interfaces.SongTO;
import cz.gattserver.grass.songs.util.Mapper;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Transactional
@Component
public class SongsFacadeImpl implements SongsService {

	@Autowired
	private Mapper mapper;

	@Autowired
	private SongsRepository songsRepository;

	@Autowired
	private ChordsRepository chordsRepository;

	@Override
	public SongTO getSongById(Long id) {
		Song song = songsRepository.findById(id).orElse(null);
		if (song == null)
			return null;
		return mapper.mapSong(song);
	}

	@Override
	public SongTO saveSong(SongTO to) {
		Song song = mapper.mapSong(to);
		song.setText(eolToBreakline(to.getText()));
		song = songsRepository.save(song);
		return mapper.mapSong(song);
	}

	@Override
	public String breaklineToEol(String text) {
		return text.replace("<br/>", "" + '\n').replace("<br>", "" + '\n');
	}

	@Override
	public String eolToBreakline(String text) {
		text = text.replace("\r\n", "<br/>");
		return text.replace("\n", "<br/>");
	}

	@Override
	public int getSongsCount(SongOverviewTO filterTO) {
		return (int) songsRepository.count(filterTO);
	}

	private List<QuerySortOrder> ensureSort(List<QuerySortOrder> sortOrders) {
		// Řeší problém s nekonzistencí pořadí záznamů v celém selektu a v selektu id -- problém se projevuje, když se
		// provede sort dle sloupce, který má více shodných hodnot -- tyto "duplicity" pak nemají z pohledu DB zřejmě
		// dané pořadí a tak mohou být jiné mezi jednotlivými selekty
		for (QuerySortOrder q : sortOrders)
			if (q.getSorted().equals("id"))
				return sortOrders;

		sortOrders.add(new QuerySortOrder("id", SortDirection.DESCENDING));
		return sortOrders;
	}

	@Override
	public List<SongOverviewTO> getSongs(
			SongOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrders) {
		return songsRepository.findSongs(filterTO, offset, limit,
				QuerydslUtil.transformOrdering(ensureSort(sortOrders),
						s -> s));
	}

	@Override
	public List<Long> getSongsIds(SongOverviewTO filterTO, List<QuerySortOrder> sortOrders) {
		return songsRepository.findSongsIds(filterTO, QuerydslUtil.transformOrdering(ensureSort(sortOrders),
				s -> s));
	}

	@Override
	public void deleteSong(Long id) {
		songsRepository.deleteById(id);
	}

	@Override
	public SongTO importSong(InputStream in, String fileName) {
		SongTO to = SongFileParser.parseSongInfo(fileName);
		try {
			to.setText(Streams.asString(in, "cp1250"));
		} catch (IOException e) {
			to.setText("Nezdařilo se zpracovat obsah souboru");
		}
		return saveSong(to);
	}

	@Override
	public ChordTO saveChord(ChordTO to) {
		Chord chord = mapper.mapChord(to);
		chord = chordsRepository.save(chord);
		to.setId(chord.getId());
		return to;
	}

	@Override
	public void deleteChord(Long id) {
		chordsRepository.deleteById(id);
	}

	@Override
	public List<ChordTO> getChords(ChordTO filterTO) {
		return mapper.mapChords(chordsRepository.findAllOrderByName(filterTO));
	}

	@Override
	public ChordTO getChordById(Long id) {
		return mapper.mapChord(chordsRepository.findById(id).orElse(null));
	}

	@Override
	public ChordTO getChordByName(String name) {
		return mapper.mapChord(chordsRepository.findByName(name));
	}
}
