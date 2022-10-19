package cz.gattserver.grass.services;

import static org.junit.jupiter.api.Assertions.*;

import cz.gattserver.grass.interfaces.QuoteTO;
import cz.gattserver.grass.mock.MockRandomSourceImpl;
import cz.gattserver.grass.util.DBCleanTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QuotesServiceTest extends DBCleanTest {

    @Autowired
    private QuotesService quotesService;

    @Test
    public void testGetAllQuotes() {
        Long quoteId = quotesService.createQuote("test");
        Long quoteId2 = quotesService.createQuote("test2");
        List<QuoteTO> quotes = quotesService.getQuotes(null);
        assertEquals(2, quotes.size());
        assertEquals(quoteId, quotes.get(0).getId());
        assertEquals("test", quotes.get(0).getName());
        assertEquals(quoteId2, quotes.get(1).getId());
        assertEquals("test2", quotes.get(1).getName());
    }

    @Test
    public void testGetRandomQuote() {
        MockRandomSourceImpl.longValuesIndex = 0;
        MockRandomSourceImpl.longValues = new long[]{0L, 1L, 2L, 3L};

        assertEquals("", quotesService.getRandomQuote());
        quotesService.createQuote("test1");
        assertEquals("test1", quotesService.getRandomQuote());

        quotesService.createQuote("test2");
        quotesService.createQuote("test3");
        quotesService.createQuote("test4");

        assertEquals("test2", quotesService.getRandomQuote());
        assertEquals("test3", quotesService.getRandomQuote());
        assertEquals("test4", quotesService.getRandomQuote());
    }

    @Test
    public void testDeleteQuote() {
        Long quoteId = quotesService.createQuote("test");
        quotesService.createQuote("test2");
        List<QuoteTO> quotes = quotesService.getQuotes(null);
        assertEquals(2, quotes.size());
        quotesService.deleteQuote(quoteId);
        quotes = quotesService.getQuotes(null);
        assertEquals(1, quotes.size());
    }

    @Test
    public void testCreateQuote() {
        Long quoteId = quotesService.createQuote("test");
        List<QuoteTO> quotes = quotesService.getQuotes(null);
        assertEquals(1, quotes.size());
        assertEquals(quoteId, quotes.get(0).getId());
        assertEquals("test", quotes.get(0).getName());
    }

    @Test
    public void testCreateQuote_fail() {
        assertThrows(NullPointerException.class, () -> quotesService.createQuote(null));
    }

    @Test
    public void testCreateQuote_fail2() {
        assertThrows(IllegalArgumentException.class, () -> quotesService.createQuote(""));
    }

    @Test
    public void testCreateQuote_fail3() {
        assertThrows(IllegalArgumentException.class, () -> quotesService.createQuote(" "));
    }

    @Test
    public void testModifyQuote() {
        Long quoteId = quotesService.createQuote("test");
        List<QuoteTO> quotes = quotesService.getQuotes(null);
        assertEquals(1, quotes.size());
        assertEquals("test", quotes.get(0).getName());
        quotesService.modifyQuote(quoteId, "ehhh");
        quotes = quotesService.getQuotes(null);
        assertEquals(1, quotes.size());
        assertEquals("ehhh", quotes.get(0).getName());
    }

    @Test
    public void testModifyQuote_fail2() {
        assertThrows(NullPointerException.class, () -> quotesService.modifyQuote(999L, null));
    }

    @Test
    public void testModifyQuote_fail3() {
        assertThrows(IllegalArgumentException.class, () -> quotesService.modifyQuote(999L, ""));
    }

    @Test
    public void testModifyQuote_fail4() {
        assertThrows(IllegalArgumentException.class, () -> quotesService.modifyQuote(999L, " "));
    }

}
