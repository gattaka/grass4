package cz.gattserver.grass.hw.ui.tabs;

import java.util.Arrays;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemRecordTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;
import cz.gattserver.grass.hw.ui.dialogs.HWItemRecordDialog;

public class HWItemRecordsTab extends Div {

    private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";

    private final HWService hwService;
    private final SecurityService securityService;

    private Column<HWItemRecordTO> dateColumn;
    private Grid<HWItemRecordTO> grid;
    private HWItemTO hwItem;
    private HWItemPage hwItemPage;

    public HWItemRecordsTab(HWItemTO hwItem, HWItemPage hwItemPage) {
        securityService = SpringContextHelper.getBean(SecurityService.class);
        hwService = SpringContextHelper.getBean(HWService.class);

        this.hwItem = hwItem;
        this.hwItemPage = hwItemPage;
        init();
    }

    private void init() {
        grid = new Grid<>();
        add(grid);

        UIUtils.applyGrassDefaultStyle(grid);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        Column<HWItemRecordTO> idColumn = grid.addColumn(new TextRenderer<>(to -> String.valueOf(to.getId())));
        dateColumn = grid.addColumn(new LocalDateRenderer<>(HWItemRecordTO::getDate, "d. M. yyyy")).setHeader("Datum")
                .setTextAlign(ColumnTextAlign.END).setWidth("90px").setFlexGrow(0);
        grid.addColumn(hw -> hw.getState().getName()).setHeader("Stav").setWidth("110px").setFlexGrow(0);
        grid.addColumn(new TextRenderer<>(to -> String.valueOf(to.getDescription()))).setHeader("Obsah");
        idColumn.setVisible(false);
        grid.setHeight("300px");

        grid.sort(Arrays.asList(new GridSortOrder<>(dateColumn, SortDirection.ASCENDING),
                new GridSortOrder<>(idColumn, SortDirection.ASCENDING)));

        populateServiceNotesGrid();

        final Div serviceNoteDescription = new Div();
        serviceNoteDescription.setId("hw-description-div");
        serviceNoteDescription.add(DEFAULT_NOTE_LABEL_VALUE);
        serviceNoteDescription.setHeight("300px");
        serviceNoteDescription.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(serviceNoteDescription);

        grid.addSelectionListener(selection -> {
            if (selection.getFirstSelectedItem().isPresent()) {
                HWItemRecordTO serviceNoteDTO = selection.getFirstSelectedItem().get();
                serviceNoteDescription.setText(serviceNoteDTO.getDescription());
            } else {
                serviceNoteDescription.setText(DEFAULT_NOTE_LABEL_VALUE);
            }
        });

        grid.addItemDoubleClickListener(e -> HWItemRecordDialog.detail(e.getItem()).open());

        if (securityService.getCurrentUser().isAdmin()) {
            ComponentFactory componentFactory = new ComponentFactory();
            Div operationsLayout = componentFactory.createButtonLayout();
            add(operationsLayout);

            operationsLayout.add(
                    componentFactory.createCreateButton(e -> HWItemRecordDialog.create(hwItem.getId(), to -> {
                        hwService.saveHWItemRecord(hwItem, to);
                        populateServiceNotesGrid();
                        hwItemPage.refreshItem();
                        hwItemPage.switchServiceNotesTab();
                        grid.select(to);
                    }).open()));

            operationsLayout.add(
                    componentFactory.createDetailGridButton(item -> HWItemRecordDialog.detail(item).open(), grid));

            operationsLayout.add(componentFactory.createEditGridButton(item -> HWItemRecordDialog.edit(item, to -> {
                hwService.saveHWItemRecord(hwItem, to);
                populateServiceNotesGrid();
            }).open(), grid));

            operationsLayout.add(componentFactory.createDeleteGridButton(item -> {
                hwService.deleteHWItemRecord(item.getId());
                hwItem.getItemRecords().remove(item);
                populateServiceNotesGrid();
                hwItemPage.refreshTabLabels();
            }, grid));

        }
    }

    private void populateServiceNotesGrid() {
        grid.setItems(hwItem.getItemRecords());
    }
}