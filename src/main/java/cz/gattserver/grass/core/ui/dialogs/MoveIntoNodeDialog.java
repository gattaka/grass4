package cz.gattserver.grass.core.ui.dialogs;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.interfaces.ContentNodeBaseTO;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.ui.components.NodeTree;
import cz.gattserver.common.spring.SpringContextHelper;

import java.util.function.Consumer;

public class MoveIntoNodeDialog extends WebDialog {

    private Button moveBtn;
    private NodeTree tree;

    public MoveIntoNodeDialog(Long preselectNodeId, Consumer<NodeTO> onSelect) {
        super("Přesunout obsah");

        setWidth(500, Unit.PIXELS);

        tree = new NodeTree();
        tree.getGrid().addSelectionListener(event -> moveBtn.setEnabled(!event.getAllSelectedItems().isEmpty()));
        tree.setHeight("300px");
        layout.add(tree);

        moveBtn = componentFactory.createSubmitButton(event -> {
            NodeTO nodeTO = tree.getGrid().getSelectedItems().iterator().next();
            onSelect.accept(nodeTO);
            close();
        });
        moveBtn.setEnabled(false);

        Button stornoBtn = componentFactory.createStornoButton(event -> close());

        layout.setHorizontalComponentAlignment(Alignment.END, stornoBtn);

        HorizontalLayout btnLayout = new HorizontalLayout(moveBtn, stornoBtn);
        btnLayout.setSizeFull();
        btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        layout.add(btnLayout);
        layout.setHorizontalComponentAlignment(Alignment.END, moveBtn);

        tree.expandTo(preselectNodeId);
    }
}