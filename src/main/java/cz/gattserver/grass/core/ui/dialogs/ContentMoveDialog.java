package cz.gattserver.grass.core.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.interfaces.ContentNodeTO2;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.ui.components.NodeTree;
import cz.gattserver.common.spring.SpringContextHelper;

// TODO abstract -> callback
public abstract class ContentMoveDialog extends WebDialog {

    private Button moveBtn;
    private NodeTree tree;

    public ContentMoveDialog(final ContentNodeTO2 contentNodeTO) {
        super("Přesunout obsah");

        setWidth("500px");

        tree = new NodeTree();
        tree.getGrid().addSelectionListener(event -> moveBtn.setEnabled(!event.getAllSelectedItems().isEmpty()));
        tree.setHeight("300px");
        layout.add(tree);

        moveBtn = componentFactory.createSubmitButton(event -> {
            NodeOverviewTO nodeDTO = tree.getGrid().getSelectedItems().iterator().next();
            SpringContextHelper.getBean(ContentNodeService.class)
                    .moveContent(nodeDTO.getId(), contentNodeTO.contentNodeId());
            close();
            onMove();
        });
        moveBtn.setEnabled(false);

        Button stornoBtn = componentFactory.createStornoButton(event -> close());

        layout.setHorizontalComponentAlignment(Alignment.END, stornoBtn);

        HorizontalLayout btnLayout = new HorizontalLayout(moveBtn, stornoBtn);
        btnLayout.setSizeFull();
        btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        layout.add(btnLayout);
        layout.setHorizontalComponentAlignment(Alignment.END, moveBtn);

        tree.expandTo(contentNodeTO.parentId());
    }

    protected abstract void onMove();

}