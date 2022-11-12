package cz.gattserver.grass.articles.plugins.templates.container;

import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;
import java.util.UUID;

public class ContainerElement implements Element {

	private List<Element> elist;

	public ContainerElement(List<Element> elist) {
		this.elist = elist;
	}

	@Override
	public void apply(Context ctx) {
		String uuid = UUID.randomUUID().toString();
		String headId = "container-head-" + uuid;
		String boxId = "container-box-" + uuid;
		ctx.print("<div style=\"border: 1px solid lightgrey;\">");
		// Není možné fyzicky zneviditelnit prvek přes display: none, protože JS
		// procesované elementy uvnitř zavřeného kontejneru by se
		// neinicializovaly správně -- proto je potřeba obsah skrýt přes
		// nastavení height a opacity
		ctx.print("<div id=\"" + headId + "\" onclick=\"var contHead = document.getElementById('" + headId + "'); "
				+ "var contBox = document.getElementById('" + boxId + "');" + "if (contBox.style.height == '0px') {"
				+ "contBox.style.height = 'auto';" + "contBox.style.padding = '5px 10px 10px 10px';"
				+ "contBox.style.opacity = 1;" + "this.innerHTML = '-';" + "} else {" + "contBox.style.height = '0px';"
				+ "contBox.style.padding = 0;" + "contBox.style.opacity = 0;" + "this.innerHTML = '+';"
				+ "}\" style=\"padding: 2px 5px 4px; font-size: 25px; font-family: monospace; cursor: pointer;\">+</div>");
		ctx.print("<div id=\"" + boxId
				+ "\" style=\"border-top: 1px solid lightgrey; opacity: 0; height: 0; padding: 0;\">");
		generateBlock(ctx);
		ctx.print("</div>");
		ctx.print("</div>");
	}

	protected void generateBlock(Context ctx) {
		if (elist != null) {
			for (Element et : elist) {
				et.apply(ctx);
			}
		}
	}

	@Override
	public List<Element> getSubElements() {
		return elist;
	}
}
