package cz.gattserver.grass.articles.plugins.favlink.plugin;

import cz.gattserver.grass.articles.editor.lexer.Lexer;
import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass.articles.plugins.favlink.test.MockFaviconObtainStrategy;
import org.junit.jupiter.api.Test;


import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class FavlinkParserTest {

    private ParsingProcessor getParsingProcessorWithText(String text) {
        Lexer lexer = new Lexer(text);
        ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
        pluginBag.nextToken(); // musí se inicializovat
        return pluginBag;
    }

    private FavlinkParser createMockParser() {
        return new FavlinkParser("MOCK", new MockFaviconObtainStrategy());
    }

    @Test
    public void test() {
        Element element = createMockParser().parse(getParsingProcessorWithText("[MOCK]http://test.mock.neco[/MOCK]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals(
                "<a style=\"word-wrap: break-word; white-space: nowrap\" href=\"http://test.mock.neco\" ><img style=\"margin: 4px 5px -4px 2px;\" height=\"16\" width=\"16\" src=\"http://mock.neco/favicon.png\" />http://test.mock.neco</a>",
                ctx.getOutput());
    }

    @Test
    public void test_failBadStartTag() {
        assertThrows(TokenException.class, () -> {
            Element element = createMockParser().parse(getParsingProcessorWithText("[BAD_TAG]HTML[/MOCK]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

    @Test
    public void test_failBadEndTag() {
        assertThrows(TokenException.class, () -> {
            Element element = createMockParser().parse(getParsingProcessorWithText("[MOCK]HTML[/BAD_TAG]"));
            Context ctx = new ContextImpl();
            element.apply(ctx);
        });
    }

}
