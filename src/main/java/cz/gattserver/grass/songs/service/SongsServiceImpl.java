package cz.gattserver.grass.songs.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import cz.gattserver.grass.core.export.ExportsService;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.songs.model.ChordsRepository;
import cz.gattserver.grass.songs.model.SongsRepository;
import cz.gattserver.grass.songs.model.Chord;
import cz.gattserver.grass.songs.model.Song;
import cz.gattserver.grass.songs.interfaces.ChordTO;
import cz.gattserver.grass.songs.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.interfaces.SongTO;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

@Transactional
@Component
public class SongsServiceImpl implements SongsService {

    private final SongsRepository songsRepository;
    private final ChordsRepository chordsRepository;
    private final ExportsService exportsService;

    public SongsServiceImpl(SongsRepository songsRepository, ChordsRepository chordsRepository,
                            ExportsService exportsService) {
        this.songsRepository = songsRepository;
        this.chordsRepository = chordsRepository;
        this.exportsService = exportsService;
    }

    @Override
    public SongTO getSongById(Long id) {
        return songsRepository.findAndMapById(id);
    }

    @Override
    public SongTO saveSong(SongTO to) {
        Song song = new Song(to.getId(), to.getName(), to.getAuthor(), to.getYear(), eolToBreakline(to.getText()),
                to.getPublicated(), to.getEmbedded());
        song = songsRepository.save(song);
        to.setId(song.getId());
        return to;
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
            if (q.getSorted().equals("id")) return sortOrders;

        sortOrders.add(new QuerySortOrder("id", SortDirection.DESCENDING));
        return sortOrders;
    }

    @Override
    public List<SongOverviewTO> getSongs(SongOverviewTO filterTO, int offset, int limit,
                                         List<QuerySortOrder> sortOrders) {
        return songsRepository.findSongs(filterTO, offset, limit,
                QuerydslUtil.transformOrdering(ensureSort(sortOrders), s -> s));
    }

    @Override
    public List<Long> getSongsIds(SongOverviewTO filterTO, List<QuerySortOrder> sortOrders) {
        return songsRepository.findSongsIds(filterTO, QuerydslUtil.transformOrdering(ensureSort(sortOrders), s -> s));
    }

    @Override
    public void deleteSong(Long id) {
        songsRepository.deleteById(id);
    }

    @Override
    public SongTO importSong(InputStream in, String fileName) {
        SongTO to = SongFileParser.parseSongInfo(fileName);
        try {
            to.setText(IOUtils.toString(in, "cp1250"));
        } catch (IOException e) {
            to.setText("Nezdařilo se zpracovat obsah souboru");
        }
        return saveSong(to);
    }

    @Override
    public ChordTO saveChord(ChordTO to) {
        Chord chord = new Chord(to.getId(),to.getName(), to.getConfiguration());
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
        return chordsRepository.findAllOrderByName(filterTO);
    }

    @Override
    public ChordTO getChordById(Long id) {
        return chordsRepository.findAndMapById(id);
    }

    @Override
    public ChordTO getChordByName(String name) {
        return chordsRepository.findByName(name);
    }

    @Override
    public Path print(SongTO s, boolean twoColumn) {
        var ctx = new Context();
        ctx.setVariable("name", s.getName());
        ctx.setVariable("author", s.getAuthor());
        ctx.setVariable("columnsCount", twoColumn ? 2 : 1);
        if (twoColumn) {
            List<String> lines = List.of(s.getText().split("\n"));
            int half = lines.size() / 2;
            String textCol1 = Strings.join(lines.subList(0, half), '\n');
            String textCol2 = Strings.join(lines.subList(half, lines.size()), '\n');
            ctx.setVariable("textCol1", textCol1);
            ctx.setVariable("textCol2", textCol2);
        } else {
            ctx.setVariable("text", s.getText());
        }

        return exportsService.createPDFReport(ctx, "song");
    }
}