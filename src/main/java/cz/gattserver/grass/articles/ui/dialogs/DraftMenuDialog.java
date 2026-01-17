package cz.gattserver.grass.articles.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.GridUtils;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;

import java.util.List;

public abstract class DraftMenuDialog extends WebDialog {

    private static final long serialVersionUID = 4105221381350726137L;

    private transient ArticleService articleFacade;

    private boolean continueFlag = false;

    protected abstract void onChoose(ArticleDraftOverviewTO draft);

    protected abstract void onCancel();

    private ArticleService getArticleService() {
        if (articleFacade == null) articleFacade = SpringContextHelper.getBean(ArticleService.class);
        return articleFacade;
    }

    private void innerChoose(ArticleDraftOverviewTO draft) {
        continueFlag = true;
        onChoose(draft);
    }

    public DraftMenuDialog(List<ArticleDraftOverviewTO> drafts) {
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
                                a.getContentNode().getLastModificationDate(), "d.M.yyyy HH:mm")).setHeader("Naposledy upraveno")
                .setFlexGrow(0).setWidth("90px");

        grid.addItemClickListener(e -> {
            if (e.getClickCount() > 1) {
                innerChoose(e.getItem());
                close();
            }
        });

        addComponent(grid);

        ButtonLayout btnLayout = new ButtonLayout();
        add(btnLayout);

        final Button confirm = new Button("Vybrat", e -> {
            innerChoose(grid.getSelectedItems().iterator().next());
            close();
        });
        confirm.setEnabled(false);
        btnLayout.add(confirm);

        Button close = new Button("Zrušit", e -> close());
        btnLayout.add(close);

        Button delete = new Button("Smazat", ev -> new ConfirmDialog("Smazat rozpracovaný článek?", e -> {
            ArticleDraftOverviewTO to = grid.getSelectedItems().iterator().next();
            // smaž draft, ponechej přílohy, pokud k draftu existuje článek
            getArticleService().deleteArticle(to.getId(), to.getContentNode().getDraftSourceId() == null);
            drafts.remove(to);
            grid.getDataProvider().refreshAll();
            grid.deselectAll();
        }).open());
        btnLayout.add(delete);
        delete.setEnabled(false);

        grid.addSelectionListener(e -> {
            confirm.setEnabled(!e.getAllSelectedItems().isEmpty());
            delete.setEnabled(!e.getAllSelectedItems().isEmpty());
        });
    }

    @Override
    public void close() {
        super.close();
        if (!continueFlag) onCancel();
    }

}
