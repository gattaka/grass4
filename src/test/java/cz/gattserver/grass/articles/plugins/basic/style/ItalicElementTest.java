package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.elements.TextElement;
import cz.gattserver.grass.articles.editor.parser.impl.ContextImpl;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ItalicElementTest {

    @Test
    public void test() {
        ItalicElement e = new ItalicElement(Arrays.asList(new TextElement("neco")));
        ContextImpl ctx = new ContextImpl();
        e.apply(ctx);
        String out = ctx.getOutput();
        assertEquals("<em>neco</em>", out);
    }

}
