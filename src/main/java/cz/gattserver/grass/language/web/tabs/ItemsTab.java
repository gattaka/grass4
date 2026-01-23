package cz.gattserver.grass.language.web.tabs;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.icon.VaadinIcon;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.CountCallback;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.grass.language.facades.LanguageFacade;
import cz.gattserver.grass.language.model.domain.ItemType;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;
import cz.gattserver.grass.language.model.dto.LanguageTO;
import cz.gattserver.grass.language.web.LanguagePage;
import cz.gattserver.grass.language.web.dialogs.LanguageItemDialog;

public class ItemsTab extends Div {

    private static final long serialVersionUID = 678133965931216087L;

    @Autowired
    private LanguageFacade languageFacade;

    @Autowired
    private SecurityService securityService;

    private LanguagePage languagePage;

    public ItemsTab(Long langId, ItemType type, LanguagePage languagePage) {
        SpringContextHelper.inject(this);

        this.languagePage = languagePage;

        LanguageItemTO filterTO = new LanguageItemTO();
        filterTO.setLanguage(langId);
        filterTO.setType(type);

        Grid<LanguageItemTO> grid = new Grid<>();
        grid.setWidthFull();
        grid.setHeight("500px");

        Column<LanguageItemTO> contentColumn =
                grid.addColumn(LanguageItemTO::getContent).setHeader("Obsah").setSortProperty("content")
                        .setFlexGrow(100);
        Column<LanguageItemTO> translationColumn =
                grid.addColumn(LanguageItemTO::getTranslation).setHeader(LanguagePage.PREKLAD_LABEL)
                        .setSortProperty("translation").setFlexGrow(100);

        if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN)) {
            grid.addColumn(item -> (Math.floor(item.getSuccessRate() * 1000) / 10) + "%").setHeader("Úspěšnost")
                    .setTextAlign(ColumnTextAlign.END).setSortProperty("successRate");
            grid.addColumn(new LocalDateTimeRenderer<LanguageItemTO>(LanguageItemTO::getLastTested, "d.M.yyyy"))
                    .setHeader("Naposledy zkoušeno").setTextAlign(ColumnTextAlign.END).setSortProperty("lastTested")
                    .setWidth("158px").setFlexGrow(0);
            grid.addColumn(LanguageItemTO::getTested).setHeader("Zkoušeno").setSortProperty("tested");
            grid.addColumn(LanguageItemTO::getId).setHeader("Id").setSortProperty("id");
        }

        grid.sort(Arrays.asList(new GridSortOrder<>(contentColumn, SortDirection.ASCENDING)));

        HeaderRow filteringHeader = grid.appendHeaderRow();

        // Obsah
        UIUtils.addHeaderTextField(filteringHeader.getCell(contentColumn), e -> {
            filterTO.setContent(e.getValue());
            populate(grid, filterTO);
        });

        // Překlad
        UIUtils.addHeaderTextField(filteringHeader.getCell(translationColumn), e -> {
            filterTO.setTranslation(e.getValue());
            populate(grid, filterTO);
        });

        populate(grid, filterTO);

        add(grid);

        if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN))
            add(createButtonLayout(grid, langId, type));
    }

    private ButtonLayout createButtonLayout(Grid<LanguageItemTO> grid, long langId, ItemType type) {
        ButtonLayout btnLayout = new ButtonLayout();

        ComponentFactory componentFactory = new ComponentFactory();
        btnLayout.add(componentFactory.createCreateButton(event -> new LanguageItemDialog(to -> {
            to.setLanguage(langId);
            languageFacade.saveLanguageItem(to);
            grid.getDataProvider().refreshAll();
        }, langId, type).open()));

        btnLayout.add(componentFactory.createEditGridButton(item -> {
            ItemType oldType = item.getType();
            new LanguageItemDialog(item, to -> {
                languageFacade.saveLanguageItem(to);
                if (oldType.equals(to.getType())) grid.getDataProvider().refreshItem(to);
                else grid.getDataProvider().refreshAll();
            }, langId, type).open();
        }, grid));

        btnLayout.add(componentFactory.createDeleteGridButton(items -> items.forEach(item -> {
            languageFacade.deleteLanguageItem(item);
            grid.getDataProvider().refreshAll();
        }), grid));

        Button moveBtn = componentFactory.createMoveGridButton(items -> changeLangOfItems(items, grid), grid);
        btnLayout.add(moveBtn);

        String caption;
        if (ItemType.WORD == type) caption = "slovíček";
        else if (ItemType.PHRASE == type) caption = "frází";
        else caption = "všeho";

        Button testBtn = new Button("Spustit test " + caption, event -> {
            languagePage.getTabs().setSelectedIndex(3);
            languagePage.startTest(langId, type);
        });
        testBtn.setIcon(VaadinIcon.PLAY.create());
        btnLayout.add(testBtn);

        return btnLayout;
    }

    private void changeLangOfItems(Set<LanguageItemTO> items, Grid<LanguageItemTO> grid) {
        Dialog w = new WebDialog("Změna jazyka");

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        w.add(layout);

        List<LanguageTO> langs = languageFacade.getLanguages();
        Grid<LanguageTO> targatGrid = new Grid<>(LanguageTO.class);
        targatGrid.setItems(langs);
        targatGrid.addColumn(LanguageTO::getName).setHeader("Název");
        layout.add(targatGrid);

        targatGrid.addSelectionListener(se -> se.getFirstSelectedItem().ifPresent(lang -> items.forEach(item -> {
            languageFacade.moveLanguageItemTo(item, lang);
            targatGrid.getDataProvider().refreshAll();
            w.close();
            grid.getDataProvider().refreshAll();
        })));
        w.open();
    }

    private void populate(Grid<LanguageItemTO> grid, LanguageItemTO filterTO) {
        FetchCallback<LanguageItemTO, LanguageItemTO> fetchCallback =
                q -> languageFacade.getLanguageItems(filterTO, q.getOffset(), q.getLimit(), q.getSortOrders()).stream();
        CountCallback<LanguageItemTO, LanguageItemTO> countCallback = q -> languageFacade.countLanguageItems(filterTO);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
    }
}