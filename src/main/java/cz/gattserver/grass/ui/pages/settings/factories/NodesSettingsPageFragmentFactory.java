package cz.gattserver.grass.ui.pages.settings.factories;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;

import cz.gattserver.grass.ui.components.NodeTree;
import cz.gattserver.grass.ui.pages.settings.AbstractPageFragmentFactory;

public class NodesSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

	@Override
	public void createFragment(Div layout) {
		NodeTree tree = new NodeTree(true);

		layout.add(new H2("Správa kategorií"));
		layout.add(tree);
	}

}
