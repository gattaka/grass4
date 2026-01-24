package cz.gattserver.grass.campgames.ui;

import java.util.Arrays;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.campgames.CampgamesRole;
import cz.gattserver.grass.campgames.interfaces.CampgameFilterTO;
import cz.gattserver.grass.campgames.interfaces.CampgameOverviewTO;
import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.service.CampgamesService;
import cz.gattserver.grass.campgames.ui.dialogs.CampgameDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;

import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;

public class CampgamesTab extends Div {

    private static final long serialVersionUID = -5013459007975657195L;

    private static final String NAME_BIND = "nameBind";
    private static final String PLAYERS_BIND = "playersBind";
    private static final String PREPARATIONTIME_BIND = "preparationTimeBind";
    private static final String PLAYTIME_BIND = "playTimeBind";

    @Autowired
    private CampgamesService campgamesService;

    private Grid<CampgameOverviewTO> grid;
    private TokenField keywordsFilter;

    private CampgameFilterTO filterDTO;

    public CampgamesTab() {
        SpringContextHelper.inject(this);

        filterDTO = new CampgameFilterTO();

        // Filtr na klíčová slova
        keywordsFilter = new TokenField(campgamesService.getAllCampgameKeywordNames());
        keywordsFilter.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        keywordsFilter.setPlaceholder("Filtrovat dle klíčových slov");
        keywordsFilter.getInputField().setWidth("200px");
        keywordsFilter.addTokenAddListener(token -> populate());
        keywordsFilter.addTokenRemoveListener(e -> populate());
        keywordsFilter.setAllowNewItems(false);
        add(keywordsFilter);

        // Tabulka her
        grid = new Grid<>();
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        UIUtils.applyGrassDefaultStyle(grid);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setWidthFull();

        Column<CampgameOverviewTO> nameColumn =
                grid.addColumn(CampgameOverviewTO::getName).setKey(NAME_BIND).setHeader("Název").setWidth("180px")
                        .setFlexGrow(0);
        Column<CampgameOverviewTO> playersColumn =
                grid.addColumn(CampgameOverviewTO::getPlayers).setKey(PLAYERS_BIND).setHeader("Hráčů").setWidth("280px")
                        .setFlexGrow(0);
        Column<CampgameOverviewTO> playTimeColumn =
                grid.addColumn(CampgameOverviewTO::getPlayTime).setKey(PLAYTIME_BIND).setHeader("Délka hry")
                        .setWidth("280px").setFlexGrow(0);
        Column<CampgameOverviewTO> prepTimeColumn =
                grid.addColumn(CampgameOverviewTO::getPreparationTime).setKey(PREPARATIONTIME_BIND)
                        .setHeader("Délka přípravy");
        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Název
        UIUtils.addHeaderTextField(filteringHeader.getCell(nameColumn), e -> {
            filterDTO.setName(e.getValue());
            populate();
        });

        // Hráčů
        UIUtils.addHeaderTextField(filteringHeader.getCell(playersColumn), e -> {
            filterDTO.setPlayers(e.getValue());
            populate();
        });

        // Délka hry
        UIUtils.addHeaderTextField(filteringHeader.getCell(playTimeColumn), e -> {
            filterDTO.setPlayTime(e.getValue());
            populate();
        });

        // Délka přípravy
        UIUtils.addHeaderTextField(filteringHeader.getCell(prepTimeColumn), e -> {
            filterDTO.setPreparationTime(e.getValue());
            populate();
        });

        populate();
        grid.sort(Arrays.asList(new GridSortOrder<CampgameOverviewTO>(nameColumn, SortDirection.ASCENDING)));
        grid.addItemClickListener(event -> {
            if (event.getClickCount() > 2) openDetailWindow(event.getItem().getId());
        });
        add(grid);

        ComponentFactory componentFactory = new ComponentFactory();

        Div buttonLayout = componentFactory.createButtonLayout();
        add(buttonLayout);

        boolean editor = SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles()
                .contains(CampgamesRole.CAMPGAME_EDITOR);

        // Založení nové hry
        Button newCampgameBtn = componentFactory.createCreateButton(e -> openItemWindow(null));
        buttonLayout.add(newCampgameBtn);
        newCampgameBtn.setVisible(editor);

        // Zobrazení detailů hry
        buttonLayout.add(componentFactory.createDetailGridButton(
                e -> openDetailWindow(grid.getSelectedItems().iterator().next().getId()), grid));

        // Oprava údajů existující hry
        Button fixBtn = componentFactory.createEditGridButton(e -> openItemWindow(e), grid);
        fixBtn.setVisible(editor);
        buttonLayout.add(fixBtn);

        // Smazání hry
        Button deleteBtn = componentFactory.createDeleteGridButton(e -> openDeleteWindow(), grid);
        deleteBtn.setEnabled(false);
        deleteBtn.setVisible(editor);
        buttonLayout.add(deleteBtn);
    }

    private void populate() {
        Set<String> types = keywordsFilter.getValues();
        filterDTO.setKeywords(types);

        FetchCallback<CampgameOverviewTO, Void> fetchCallback =
                q -> campgamesService.getCampgames(filterDTO, q.getOffset(), q.getLimit(),
                        QuerydslUtil.transformOrdering(q.getSortOrders(), column -> {
                            switch (column) {
                                case NAME_BIND:
                                    return "name";
                                case PLAYERS_BIND:
                                    return "players";
                                case PLAYTIME_BIND:
                                    return "playTime";
                                case PREPARATIONTIME_BIND:
                                    return "preparationTime";
                                default:
                                    return column;
                            }
                        })).stream();
        CountCallback<CampgameOverviewTO, Void> countCallback = q -> campgamesService.countCampgames(filterDTO);
        grid.setDataProvider(DataProvider.fromCallbacks(fetchCallback, countCallback));
    }

    private void openItemWindow(CampgameOverviewTO selectedTO) {
        CampgameTO campgame = null;
        if (selectedTO != null) {
            if (grid.getSelectedItems().isEmpty()) return;
            campgame = campgamesService.getCampgame(selectedTO.getId());
        }
        new CampgameDialog(campgame == null ? null : campgamesService.getCampgame(campgame.getId()),
                to -> onSave(to)).open();
    }

    private void refreshGrid() {
        grid.getDataProvider().refreshAll();
    }

    private void onSave(CampgameTO to) {
        campgamesService.saveCampgame(to);
        CampgameOverviewTO filterTO = new CampgameOverviewTO();
        filterTO.setId(to.getId());
        // select musí neintuitivně být dřív než refresh, jinak se do
        // tabulky zobrazí prázdný řádek
        grid.select(filterTO);
        refreshGrid();
    }

    private void openDetailWindow(Long id) {
        new CampgameDialog(campgamesService.getCampgame(id), to -> onSave(to)).open();
    }

    private void openDeleteWindow() {
        if (grid.getSelectedItems().isEmpty()) return;
        CampgameOverviewTO to = grid.getSelectedItems().iterator().next();
        try {
            campgamesService.deleteCampgame(to.getId());
            refreshGrid();
        } catch (Exception ex) {
            new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
        }
    }
}