package cz.gattserver.grass.articles.plugins.templates.sort;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SortElement implements Element {

    private final List<SortElementsLine> lines;

    public SortElement(List<SortElementsLine> lines) {
        this.lines = lines;
    }

    @Override
    public void apply(Context ctx) {
        lines.sort((o1, o2) -> Objects.compare(o1.getComparable(), o2.getComparable(), String::compareTo));
        for (SortElementsLine line : lines) {
            for (Element el : line.getElements())
                el.apply(ctx);
            ctx.print("<br/>");
        }
    }

    @Override
    public List<Element> getSubElements() {
        List<Element> elements = new ArrayList<>();
        for (SortElementsLine line : lines)
            elements.addAll(line.getElements());
        return elements;
    }
}