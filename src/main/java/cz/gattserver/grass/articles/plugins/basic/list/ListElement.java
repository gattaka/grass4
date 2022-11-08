package cz.gattserver.grass.articles.plugins.basic.list;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.ArrayList;
import java.util.List;

public class ListElement implements Element {

	private List<List<Element>> listElements;
	private boolean ordered;

	public ListElement(List<List<Element>> listElements, boolean ordered) {
		this.listElements = listElements;
		this.ordered = ordered;
	}

	@Override
	public void apply(Context ctx) {
		ctx.print(ordered ? "<ol>" : "<ul>");
		// Každá položka listu se může skládat z více parserových elementů
		for (List<Element> elist : listElements) {
			ctx.print("<li>");
			for (Element elementTree : elist) {
				elementTree.apply(ctx);
			}
			ctx.print("</li>");
		}
		ctx.print(ordered ? "</ol>" : "</ul>");
	}

	@Override
	public List<Element> getSubElements() {
		List<Element> superList = new ArrayList<>();
		for (List<Element> list : listElements)
			superList.addAll(list);
		return superList;
	}
}
