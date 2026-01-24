package cz.gattserver.grass.campgames.ui;

import java.util.Set;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.grid.Grid.SelectionMode;

import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.campgames.CampgamesRole;
import cz.gattserver.grass.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass.campgames.service.CampgamesService;
import cz.gattserver.grass.campgames.ui.dialogs.CampgameKeywordDialog;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;

public class CampgameKeywordsTab extends Div {

    private static final long serialVersionUID = -5013459007975657195L;

    private transient CampgamesService campgamesService;

    private Grid<CampgameKeywordTO> grid;

    private CampgamesService getCampgamesService() {
        if (campgamesService == null) campgamesService = SpringContextHelper.getBean(CampgamesService.class);
        return campgamesService;
    }

    public CampgameKeywordsTab() {
        grid = new Grid<>();
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        UIUtils.applyGrassDefaultStyle(grid);
        Set<CampgameKeywordTO> data = getCampgamesService().getAllCampgameKeywords();
        grid.setItems(data);

        grid.addColumn(CampgameKeywordTO::getName).setHeader("Název").setKey("name");
        grid.setWidthFull();
        grid.setSelectionMode(SelectionMode.SINGLE);

        add(grid);

        ComponentFactory componentFactory = new ComponentFactory();
        Div buttonLayout = componentFactory.createButtonLayout();
        add(buttonLayout);

        buttonLayout.setVisible(SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
                .contains(CampgamesRole.CAMPGAME_EDITOR));
        buttonLayout.add(componentFactory.createEditGridButton(e -> openNewTypeWindow(data, true), grid));
        buttonLayout.add(componentFactory.createDeleteGridSetButton(e -> openDeleteWindow(data), grid));
    }

    // BUG ? Při disable na tabu a opětovném enabled zůstane table disabled
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        grid.setEnabled(enabled);
    }

    private void openNewTypeWindow(final Set<CampgameKeywordTO> data, boolean fix) {
        CampgameKeywordTO campgameKeywordTO = null;
        if (fix) campgameKeywordTO = grid.getSelectedItems().iterator().next();
        new CampgameKeywordDialog(campgameKeywordTO == null ? null : campgameKeywordTO) {
            private static final long serialVersionUID = -7566950396535469316L;

            @Override
            protected void onSuccess(CampgameKeywordTO campgameKeywordTO) {
                if (fix) {
                    grid.getDataProvider().refreshItem(campgameKeywordTO);
                } else {
                    data.add(campgameKeywordTO);
                    grid.getDataProvider().refreshAll();
                }
            }
        }.open();
    }

    private void openDeleteWindow(final Set<CampgameKeywordTO> data) {
        CampgameKeywordsTab.this.setEnabled(false);
        final CampgameKeywordTO campgameKeywordTO = grid.getSelectedItems().iterator().next();
        new ConfirmDialog("Opravdu smazat '" + campgameKeywordTO.getName() +
                "' (klíčové slovo bude odebráno od všech označených her)?", e -> {
            try {
                getCampgamesService().deleteCampgameKeyword(campgameKeywordTO.getId());
                data.remove(campgameKeywordTO);
                grid.getDataProvider().refreshAll();
            } catch (Exception ex) {
                new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
            }
        }) {
            private static final long serialVersionUID = -422763987707688597L;

            @Override
            public void close() {
                CampgameKeywordsTab.this.setEnabled(true);
                super.close();
            }
        }.open();
    }
}
