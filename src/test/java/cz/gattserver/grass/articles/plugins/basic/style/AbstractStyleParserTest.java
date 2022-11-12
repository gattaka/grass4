package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.lexer.Lexer;
import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass.articles.plugins.basic.style.align.CenterAlignPlugin;
import cz.gattserver.grass.articles.plugins.basic.style.align.LeftAlignPlugin;
import cz.gattserver.grass.articles.plugins.basic.style.align.RightAlignPlugin;
import cz.gattserver.grass.articles.plugins.basic.style.color.BluePlugin;
import cz.gattserver.grass.articles.plugins.basic.style.color.GreenPlugin;
import cz.gattserver.grass.articles.plugins.basic.style.color.RedPlugin;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractStyleParserTest {

    private ParsingProcessor getParsingProcessorWithText(String text) {
        Lexer lexer = new Lexer(text);
        ParsingProcessor pluginBag = new ParsingProcessor(lexer, "contextRoot", new HashMap<>());
        pluginBag.nextToken(); // musí se inicializovat
        return pluginBag;
    }

    private AbstractStyleParser createMockParser() {
        return new AbstractStyleParser("MOCK") {

            @Override
            protected AbstractStyleElement getElement(List<Element> elist) {
                return new AbstractStyleElement(elist) {

                    @Override
                    protected void generateStartTag(Context ctx) {
                        ctx.print("<mock>");
                    }

                    @Override
                    protected void generateEndTag(Context ctx) {
                        ctx.print("</mock>");
                    }
                };
            }
        };
    }

    @Test
    public void test() {
        Element element = createMockParser().parse(getParsingProcessorWithText("[MOCK]HTMLsample[/MOCK]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<mock>HTMLsample</mock>", ctx.getOutput());
    }

    @Test
    public void testCrossoutParser() {
        Element element = new CrossoutPlugin().getParser()
                .parse(getParsingProcessorWithText("[CROSS]HTMLsample[/CROSS]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<span style='text-decoration: line-through'>HTMLsample</span>", ctx.getOutput());
    }

    @Test
    public void testItalicParser() {
        Element element = new ItalicPlugin().getParser().parse(getParsingProcessorWithText("[EM]HTMLsample[/EM]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<em>HTMLsample</em>", ctx.getOutput());
    }

    @Test
    public void testMonospaceParser() {
        Element element = new MonospacePlugin().getParser()
                .parse(getParsingProcessorWithText("[MONSPC]HTMLsample[/MONSPC]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<span class=\"articles-basic-monospaced\">HTMLsample</span>", ctx.getOutput());
    }

    @Test
    public void testStrongParser() {
        Element element = new StrongPlugin().getParser().parse(getParsingProcessorWithText("[STR]HTMLsample[/STR]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<strong>HTMLsample</strong>", ctx.getOutput());
    }

    @Test
    public void testSubParser() {
        Element element = new SubPlugin().getParser().parse(getParsingProcessorWithText("[SUB]HTMLsample[/SUB]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<sub>HTMLsample</sub>", ctx.getOutput());
    }

    @Test
    public void testSupParser() {
        Element element = new SupPlugin().getParser().parse(getParsingProcessorWithText("[SUP]HTMLsample[/SUP]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<sup>HTMLsample</sup>", ctx.getOutput());
    }

    @Test
    public void testUnderlineParser() {
        Element element = new UnderlinePlugin().getParser().parse(getParsingProcessorWithText("[UND]HTMLsample[/UND]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<span style='text-decoration: underline'>HTMLsample</span>", ctx.getOutput());
    }

    @Test
    public void testCenterAlignParser() {
        Element element = new CenterAlignPlugin().getParser()
                .parse(getParsingProcessorWithText("[ALGNCT]HTMLsample[/ALGNCT]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<div style='text-align: center'>HTMLsample</div>", ctx.getOutput());
    }

    @Test
    public void testLeftAlignParser() {
        Element element = new LeftAlignPlugin().getParser()
                .parse(getParsingProcessorWithText("[ALGNLT]HTMLsample[/ALGNLT]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<div style='text-align: left'>HTMLsample</div>", ctx.getOutput());
    }

    @Test
    public void testRightAlignParser() {
        Element element = new RightAlignPlugin().getParser()
                .parse(getParsingProcessorWithText("[ALGNRT]HTMLsample[/ALGNRT]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<div style='text-align: right'>HTMLsample</div>", ctx.getOutput());
    }

    @Test
    public void testBlueParser() {
        Element element = new BluePlugin().getParser().parse(getParsingProcessorWithText("[BLU]HTMLsample[/BLU]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<span style='color: blue'>HTMLsample</span>", ctx.getOutput());
    }

    @Test
    public void testRedParser() {
        Element element = new RedPlugin().getParser().parse(getParsingProcessorWithText("[RED]HTMLsample[/RED]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<span style='color: red'>HTMLsample</span>", ctx.getOutput());
    }

    @Test
    public void testGreenParser() {
        Element element = new GreenPlugin().getParser().parse(getParsingProcessorWithText("[GRN]HTMLsample[/GRN]"));
        Context ctx = new ContextImpl();
        element.apply(ctx);
        assertEquals("<span style='color: green'>HTMLsample</span>", ctx.getOutput());
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
