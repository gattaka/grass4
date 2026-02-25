package cz.gattserver.grass.articles.editor.parser.elements;

import cz.gattserver.grass.articles.editor.parser.Context;

import java.util.List;

public record TextElement(String text) implements Element {

    @Override
    public void apply(Context ctx) {
        ctx.println(text);
    }

    @Override
    public List<Element> getSubElements() {
        return null;
    }
}
