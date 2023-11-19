package cz.gattserver.grass.core.ui.pages;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import cz.gattserver.grass.core.ui.pages.template.GrassPage;

@Route("page-c")
public class PageC extends GrassPage implements HasUrlParameter<String> {

	public PageC() {
		System.out.println("PAGE-C");		init();
	}

	@Override
	public void setParameter(
			BeforeEvent event,
			@WildcardParameter String parameter) {
		if (parameter.isEmpty()) {
			add(new Text("Welcome anonymous."));
		} else {
			add(new Text(String.format("Handling parameter %s.", parameter)));
		}
	}

	@Override
	protected void createPageElements(Div div) {
		add(new Text("Page C"));
		add(new Anchor("page-a", "PageA Link"));
		add(new Anchor("page-b", "PageB Link"));
	}
}
