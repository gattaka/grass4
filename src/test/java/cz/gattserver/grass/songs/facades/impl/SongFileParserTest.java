package cz.gattserver.grass.songs.facades.impl;

import cz.gattserver.grass.songs.model.interfaces.SongTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SongFileParserTest {

    //@Rule
    //public ExpectedException exception = ExpectedException.none();

    @Test
    public void testOk() {
        SongTO to = SongFileParser.parseSongInfo("Autor - Název (2004).txt");
        assertEquals("Autor", to.getAuthor());
        assertEquals("Název", to.getName());
        assertEquals(2004, to.getYear().intValue());
    }

    @Test
    public void testOkYearInName() {
        SongTO to = SongFileParser.parseSongInfo("Autor - Název 2004.txt");
        assertEquals("Autor", to.getAuthor());
        assertEquals("Název 2004", to.getName());
        assertNull(to.getYear());
    }

    @Test
    public void testOkNoYear() {
        SongTO to = SongFileParser.parseSongInfo("Autor - Název.txt");
        assertEquals("Autor", to.getAuthor());
        assertEquals("Název", to.getName());
        assertNull(to.getYear());
    }

    @Test
    public void testOkSpaces() {
        SongTO to = SongFileParser.parseSongInfo("  Autor   -   Název   (2004)  .txt");
        assertEquals("Autor", to.getAuthor());
        assertEquals("Název", to.getName());
        assertEquals(2004, to.getYear().intValue());
    }

    @Test
    public void testOkSpacesNoYear() {
        SongTO to = SongFileParser.parseSongInfo("  Autor   -   Název   .txt");
        assertEquals("Autor", to.getAuthor());
        assertEquals("Název", to.getName());
        assertNull(to.getYear());
    }

    @Test
    public void testAuthorFail() {
        assertThrows(IllegalStateException.class, () -> {
            SongFileParser.parseSongInfo("Název (2004).txt");
        }, SongFileParser.AUTHOR_ERR);
    }

    @Test
    public void testNameFail() {
        assertThrows(IllegalStateException.class, () -> {
            SongFileParser.parseSongInfo("Autor - (2004).txt");
        }, SongFileParser.NAME_ERR);
    }

    @Test
    public void testNameFail2() {
        assertThrows(IllegalStateException.class, () -> {
            SongFileParser.parseSongInfo("Autor - .txt");
        }, SongFileParser.NAME_ERR);
    }

    @Test
    public void testYearFail() {
        assertThrows(IllegalStateException.class, () -> {
            SongFileParser.parseSongInfo("Autor - Název (090s).txt");
        }, SongFileParser.YEAR_ERR);
    }

}
