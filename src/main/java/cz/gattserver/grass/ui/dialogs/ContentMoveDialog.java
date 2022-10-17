package cz.gattserver.grass.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import cz.gattserver.grass.interfaces.ContentNodeTO;
import cz.gattserver.grass.interfaces.NodeOverviewTO;
import cz.gattserver.grass.services.ContentNodeService;
import cz.gattserver.grass.ui.components.NodeTree;
import cz.gattserver.web.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.dialogs.WebDialog;

public abstract class ContentMoveDialog extends WebDialog {

	private static final long serialVersionUID = -2550619983411515006L;

	private Button moveBtn;
	private NodeTree tree;

	public ContentMoveDialog(final ContentNodeTO contentNodeDTO) {
		super("Přesunout obsah");

		setWidth("500px");

		tree = new NodeTree();
		tree.getGrid().addSelectionListener(event -> moveBtn.setEnabled(!event.getAllSelectedItems().isEmpty()));
		tree.setHeight("300px");
		layout.add(tree);

		moveBtn = new Button("Přesunout");
		moveBtn.setEnabled(false);
		moveBtn.addClickListener(event -> {
			NodeOverviewTO nodeDTO = tree.getGrid().getSelectedItems().iterator().next();
			SpringContextHelper.getBean(ContentNodeService.class).moveContent(nodeDTO.getId(), contentNodeDTO.getId());
			close();
			onMove();
		});

		layout.add(moveBtn);
		layout.setHorizontalComponentAlignment(Alignment.END, moveBtn);

		NodeOverviewTO to = contentNodeDTO.getParent();
		tree.expandTo(to.getId());
	}

	protected abstract void onMove();

}
