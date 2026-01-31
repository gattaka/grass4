package cz.gattserver.grass.articles.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.core.ui.util.GridUtils;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.util.List;
import java.util.function.Consumer;

public class DraftMenuDialog extends WebDialog {

    private static final long serialVersionUID = 4105221381350726137L;

    private boolean continueFlag = false;

    private final Consumer<ArticleDraftOverviewTO> onSelect;

    private void innerChoose(ArticleDraftOverviewTO draft) {
        continueFlag = true;
        onSelect.accept(draft);
    }

    public DraftMenuDialog(List<ArticleDraftOverviewTO> drafts, Consumer<ArticleDraftOverviewTO> onSelect, Consumer<ArticleDraftOverviewTO> onDelete) {
        super("Rozpracované obsahy");
        this.onSelect = onSelect;

        Span label = new Span("Byly nalezeny rozpracované obsahy -- přejete si pokračovat v jejich úpravách?");
        addComponent(label);

        final Grid<ArticleDraftOverviewTO> grid = new Grid<>();
        UIUtils.applyGrassDefaultStyle(grid);
        grid.setItems(drafts);
        grid.setHeight(GridUtils.processHeight(drafts.size()) + "px");
        grid.setWidth("900px");
        grid.setSelectionMode(SelectionMode.SINGLE);

        grid.addColumn(new TextRenderer<>(a -> a.getContentNode().getName())).setHeader("Název").setWidth("200px")
                .setFlexGrow(0);
        grid.addColumn(
                        new TextRenderer<>(a -> a.getText().length() < 100 ? a.getText() : a.getText().substring(0, 100)))
                .setHeader("Náhled");
        grid.addColumn(new LocalDateTimeRenderer<>(
                        a -> a.getContentNode().getLastModificationDate() == null ? a.getContentNode().getCreationDate() :
                                a.getContentNode().getLastModificationDate(), "d. M. yyyy HH:mm"))
                .setHeader("Naposledy upraveno").setFlexGrow(0).setWidth("180px");

        grid.addItemDoubleClickListener(e -> {
            innerChoose(e.getItem());
            close();
        });

        addComponent(grid);

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        btnLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        add(btnLayout);

        final Button confirmBtn = componentFactory.createSubmitButton(e -> {
            innerChoose(grid.getSelectedItems().iterator().next());
            close();
        });
        confirmBtn.setEnabled(false);
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnLayout.add(confirmBtn);

        Button deleteBtn = componentFactory.createDeleteButton(e -> {
            ArticleDraftOverviewTO to = grid.getSelectedItems().iterator().next();
            onDelete.accept(to);
            drafts.remove(to);
            grid.getDataProvider().refreshAll();
            grid.deselectAll();
        });
        btnLayout.add(deleteBtn);
        deleteBtn.setEnabled(false);

        Button closeBtn = componentFactory.createStornoButton(e -> close());
        btnLayout.add(closeBtn);

        grid.addSelectionListener(e -> {
            confirmBtn.setEnabled(!e.getAllSelectedItems().isEmpty());
            deleteBtn.setEnabled(!e.getAllSelectedItems().isEmpty());
        });
    }

    @Override
    public void close() {
        super.close();
        if (!continueFlag) onSelect.accept(null);
    }
}