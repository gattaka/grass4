package cz.gattserver.grass.articles.plugins.basic.abbr;

import cz.gattserver.grass.articles.editor.lexer.Lexer;
import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass.articles.editor.parser.impl.ContextImpl;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbbrParserTest {

    private ParsingProcessor getParsingProcessorWithText(String text) {
        Lexer lexer = new Lexer(text);
        ParsingProcessor parsingProcessor = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
        parsingProcessor.nextToken(); // musí se inicializovat
        return parsingProcessor;
    }

    @Test
    public void test() {
        AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
        Element element = parser.parse(getParsingProcessorWithText(
                "[CUSTOM_TAG]HTML[CUSTOM_TAG2]Hypertext Markup Language[/CUSTOM_TAG2][/CUSTOM_TAG]"));

        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<abbr title=\"Hypertext Markup Language\">HTML</abbr>", ctx.getOutput());
    }

    @Test
    public void test_failAbbrEOF() {
        assertThrows(TokenException.class, () -> {
            AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
            Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]HTML[CUSTOM_TAG2]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

    @Test
    public void test_failTitleEOF() {
        assertThrows(TokenException.class, () -> {
            AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
            Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

    @Test
    public void test_failBadAbbrStartTag() {
        assertThrows(TokenException.class, () -> {
            AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
            Element element = parser.parse(getParsingProcessorWithText(
                    "[BAD_TAG]HTML[CUSTOM_TAG2]Hypertext Markup Language[/CUSTOM_TAG2][/CUSTOM_TAG]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

    @Test
    public void test_failBadAbbrEndTag() {
        assertThrows(TokenException.class, () -> {
            AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
            Element element = parser.parse(getParsingProcessorWithText(
                    "[CUSTOM_TAG]HTML[CUSTOM_TAG2]Hypertext Markup Language[/CUSTOM_TAG2][/BAD_TAG]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

    @Test
    public void test_failBadTitleStartTag() {
        assertThrows(TokenException.class, () -> {
            AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
            Element element = parser.parse(getParsingProcessorWithText(
                    "[CUSTOM_TAG]HTML[BAD_TAG2]Hypertext Markup Language[/CUSTOM_TAG2][/CUSTOM_TAG]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

    @Test
    public void test_failBadTitleEndTag() {
        assertThrows(TokenException.class, () -> {
            AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
            Element element = parser.parse(getParsingProcessorWithText(
                    "[CUSTOM_TAG]HTML[CUSTOM_TAG2]Hypertext Markup Language[/BAD_TAG2][/CUSTOM_TAG]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

    @Test
    public void test_failMandatoryAbbr() {
        assertThrows(TokenException.class, () -> {
            AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
            Element element = parser
                    .parse(getParsingProcessorWithText("[CUSTOM_TAG][CUSTOM_TAG2][/CUSTOM_TAG2][/CUSTOM_TAG]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

    @Test
    public void test_failMandatoryTitle() {
        assertThrows(TokenException.class, () -> {
            AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
            Element element = parser
                    .parse(getParsingProcessorWithText("[CUSTOM_TAG] [CUSTOM_TAG2][/CUSTOM_TAG2][/CUSTOM_TAG]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

    @Test
    public void test_failMandatorySubtag() {
        assertThrows(TokenException.class, () -> {
            AbbrParser parser = new AbbrParser("CUSTOM_TAG", "CUSTOM_TAG2");
            Element element = parser.parse(getParsingProcessorWithText("[CUSTOM_TAG][/CUSTOM_TAG]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

}
