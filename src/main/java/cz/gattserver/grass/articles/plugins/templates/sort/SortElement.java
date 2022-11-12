package cz.gattserver.grass.articles.plugins.templates.sort;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SortElement implements Element {

	private List<SortElementsLine> lines = new ArrayList<>();

	public SortElement(List<SortElementsLine> lines) {
		this.lines = lines;
	}

	@Override
	public void apply(Context ctx) {
		lines.sort((o1, o2) -> StringUtils.compare(o1.getComparable(), o2.getComparable()));
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
