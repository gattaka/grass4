package cz.gattserver.grass.hw.ui.tabs;

import java.util.Arrays;

import com.vaadin.flow.component.button.Button;
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
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemRecordTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.pages.HWItemPage;
import cz.gattserver.grass.hw.ui.dialogs.HWItemRecordEditDialog;

public class HWItemRecordsTab extends Div {

    private static final long serialVersionUID = -3236939739462367881L;

    private static final String DEFAULT_NOTE_LABEL_VALUE = "- Zvolte servisní záznam -";

    private transient HWService hwService;
    private transient SecurityService securityFacade;

    private Column<HWItemRecordTO> serviceDateColumn;
    private Grid<HWItemRecordTO> serviceNotesGrid;
    private HWItemTO hwItem;
    private HWItemPage hwItemPage;

    public HWItemRecordsTab(HWItemTO hwItem, HWItemPage hwItemPage) {
        SpringContextHelper.inject(this);
        this.hwItem = hwItem;
        this.hwItemPage = hwItemPage;
        init();
    }

    private HWService getHWService() {
        if (hwService == null) hwService = SpringContextHelper.getBean(HWService.class);
        return hwService;
    }

    private UserInfoTO getUser() {
        if (securityFacade == null) securityFacade = SpringContextHelper.getBean(SecurityService.class);
        return securityFacade.getCurrentUser();
    }

    private void init() {
        serviceNotesGrid = new Grid<>();
        add(serviceNotesGrid);

        UIUtils.applyGrassDefaultStyle(serviceNotesGrid);
        serviceNotesGrid.setSelectionMode(SelectionMode.SINGLE);
        serviceNotesGrid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        Column<HWItemRecordTO> idColumn =
                serviceNotesGrid.addColumn(new TextRenderer<>(to -> String.valueOf(to.getId())));
        serviceDateColumn = serviceNotesGrid.addColumn(new LocalDateRenderer<>(HWItemRecordTO::getDate, "d.M.yyyy"))
                .setHeader("Datum").setTextAlign(ColumnTextAlign.END).setWidth("80px").setFlexGrow(0);
        serviceNotesGrid.addColumn(hw -> hw.getState().getName()).setHeader("Stav").setWidth("110px").setFlexGrow(0);
        serviceNotesGrid.addColumn(
                        new TextRenderer<>(to -> to.getUsedInName() == null ? "" : String.valueOf(to.getUsedInName())))
                .setHeader("Je součástí").setWidth("180px").setFlexGrow(0);
        serviceNotesGrid.addColumn(new TextRenderer<>(to -> String.valueOf(to.getDescription()))).setHeader("Obsah");
        idColumn.setVisible(false);
        serviceNotesGrid.setHeight("300px");

        serviceNotesGrid.sort(Arrays.asList(new GridSortOrder<>(serviceDateColumn, SortDirection.ASCENDING),
                new GridSortOrder<>(idColumn, SortDirection.ASCENDING)));

        populateServiceNotesGrid();

        final Div serviceNoteDescription = new Div();
        serviceNoteDescription.setId("hw-description-div");
        serviceNoteDescription.add(DEFAULT_NOTE_LABEL_VALUE);
        serviceNoteDescription.setHeight("300px");
        serviceNoteDescription.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(serviceNoteDescription);

        serviceNotesGrid.addSelectionListener(selection -> {
            if (selection.getFirstSelectedItem().isPresent()) {
                HWItemRecordTO serviceNoteDTO = selection.getFirstSelectedItem().get();
                serviceNoteDescription.setText((String) serviceNoteDTO.getDescription());
            } else {
                serviceNoteDescription.setText(DEFAULT_NOTE_LABEL_VALUE);
            }
        });

        if (getUser().isAdmin()) {
            ComponentFactory componentFactory = new ComponentFactory();
            Div operationsLayout = componentFactory.createButtonLayout();
            add(operationsLayout);

            Button newNoteBtn = componentFactory.createCreateButton(e -> new HWItemRecordEditDialog(hwItem) {
                private static final long serialVersionUID = -5582822648042555576L;

                @Override
                protected void onSuccess(HWItemRecordTO noteDTO) {
                    hwItem.getServiceNotes().add(noteDTO);
                    populateServiceNotesGrid();
                    hwItemPage.refreshItem();
                    hwItemPage.switchServiceNotesTab();
                    serviceNotesGrid.select(noteDTO);
                }
            }.open());

            Button editNoteBtn = componentFactory.createEditGridButton(event -> {
                if (serviceNotesGrid.getSelectedItems().isEmpty()) return;
                new HWItemRecordEditDialog(hwItem, serviceNotesGrid.getSelectedItems().iterator().next()) {
                    private static final long serialVersionUID = -5582822648042555576L;

                    @Override
                    protected void onSuccess(HWItemRecordTO noteDTO) {
                        populateServiceNotesGrid();
                    }
                }.open();
            }, serviceNotesGrid);

            Button deleteNoteBtn = componentFactory.createDeleteGridButton(item -> {
                getHWService().deleteServiceNote(item.getId());
                hwItem.getServiceNotes().remove(item);
                populateServiceNotesGrid();
                hwItemPage.refreshTabLabels();
            }, serviceNotesGrid);

            operationsLayout.add(newNoteBtn);
            operationsLayout.add(editNoteBtn);
            operationsLayout.add(deleteNoteBtn);
        }
    }

    private void populateServiceNotesGrid() {
        serviceNotesGrid.setItems(hwItem.getServiceNotes());
        serviceNotesGrid.sort(Arrays.asList(new GridSortOrder<>(serviceDateColumn, SortDirection.DESCENDING)));
    }
}
