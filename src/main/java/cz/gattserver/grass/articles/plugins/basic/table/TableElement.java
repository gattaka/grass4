package cz.gattserver.grass.articles.plugins.basic.table;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Hynek
 */
public class TableElement implements Element {

	private List<List<List<Element>>> rows;
	private boolean withHead;
	private int cols;

	public TableElement(List<List<List<Element>>> rows, boolean withHead, int cols) {
		this.rows = rows;
		this.withHead = withHead;
		this.cols = cols;
	}

	@Override
	public void apply(Context ctx) {
		if (rows.isEmpty())
			return;

		int row = 0;
		ctx.print(
				"<table bordercolor=\"#888\" cellpadding=\"5px\" rules=\"all\" border=\"0\" style=\"margin-left: auto; margin-right: auto;\">");
		if (withHead) {
			ctx.print("<thead>");
			Iterator<List<Element>> it = rows.get(row).iterator();
			for (int col = 0; col < cols; col++) {
				ctx.print("<th>");
				if (it.hasNext())
					for (Element e : it.next())
						e.apply(ctx);
				ctx.print("</th>");
			}
			row++;
			ctx.print("</thead>");
		}
		// Tabulka musí kontrolovat prázdná okna, ta totiž znamenají konec řádky
		// tabulky, přičemž každá řádka může mít různý počet políček - maximálně
		// cols
		for (; row < rows.size(); row++) {
			ctx.print("<tr>");
			Iterator<List<Element>> it = rows.get(row).iterator();
			for (int col = 0; col < cols; col++) {
				ctx.print("<td>");
				if (it.hasNext())
					for (Element e : it.next())
						e.apply(ctx);
				ctx.print("</td>");
			}
			ctx.print("</tr>");
		}
		ctx.print("</table>");
	}

	@Override
	public List<Element> getSubElements() {
		List<Element> superList = new ArrayList<>();
		for (List<List<Element>> row : rows)
			for (List<Element> cell : row)
				superList.addAll(cell);
		return superList;
	}
}
