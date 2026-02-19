package cz.gattserver.grass.medic.web.tabs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitOverviewTO;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;
import cz.gattserver.grass.medic.util.MedicUtil;
import cz.gattserver.grass.medic.web.MedicalRecordDialog;
import cz.gattserver.grass.medic.web.ScheduledVisitDialog;

public class ScheduledVisitsTab extends Div {

    private final MedicService medicService;

    private final ComponentFactory componentFactory;

    private Grid<ScheduledVisitOverviewTO> toBePlannedGrid = new Grid<>();
    private Grid<ScheduledVisitOverviewTO> plannedGrid = new Grid<>();

    public ScheduledVisitsTab() {
        medicService = SpringContextHelper.getBean(MedicService.class);
        componentFactory = new ComponentFactory();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", componentFactory.createLocale());
        Div div = new HtmlDiv("<strong>Dnes je: </strong>" + LocalDate.now().format(formatter));
        div.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(div);

        createPlannedGrid();
        createToBePlannedTable();
    }

    private void populateContainer(boolean planned) {
        Grid<ScheduledVisitOverviewTO> grid = planned ? plannedGrid : toBePlannedGrid;
        if (planned) {
            plannedGrid.setItems(medicService.getAllScheduledVisits(planned));
        } else {
            toBePlannedGrid.setItems(medicService.getAllScheduledVisits(planned));
        }
        grid.getDataProvider().refreshAll();
        grid.deselectAll();
        Column<ScheduledVisitOverviewTO> dateColumn = grid.getColumnByKey("date");
        grid.sort(Arrays.asList(new GridSortOrder<ScheduledVisitOverviewTO>(dateColumn, SortDirection.ASCENDING)));
    }

    private void createPlannedGrid() {
        ComponentFactory componentFactory = new ComponentFactory();

        Consumer<ScheduledVisitTO> onSave = to -> {
            medicService.saveScheduledVisit(to);
            populateContainer(true);
        };

        Consumer<Long> onDelete = id -> {
            medicService.deleteScheduledVisit(id);
            populateContainer(true);
        };

        Consumer<ScheduledVisitOverviewTO> createEditDialog =
                to -> ScheduledVisitDialog.edit(medicService.getScheduledVisitById(to.getId()), onSave).open();

        /**
         * Přehled
         */
        Div headerDiv = new Div(new Strong("Naplánované návštěvy"));
        headerDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(headerDiv);

        prepareGrid(plannedGrid, true);
        add(plannedGrid);
        plannedGrid.addItemDoubleClickListener(e -> createEditDialog.accept(e.getItem()));

        Div buttonLayout = componentFactory.createButtonLayout();
        add(buttonLayout);

        /**
         * Založení nové návštěvy
         */
        buttonLayout.add(componentFactory.createCreateButton(event -> ScheduledVisitDialog.create(onSave).open()));

        /**
         * Úprava návštěvy
         */
        buttonLayout.add(componentFactory.createEditGridButton(createEditDialog, plannedGrid));

        /**
         * Smazání návštěvy
         */
        buttonLayout.add(componentFactory.createDeleteGridButton(to -> onDelete.accept(to.getId()), plannedGrid));

        /**
         * Absolvování návštěvy
         */
        buttonLayout.add(componentFactory.createGridSingleButton("Absolvováno", VaadinIcon.ARCHIVE.create(),
                to -> MedicalRecordDialog.create(medicService.getScheduledVisitById(to.getId()), to2 -> {
                    medicService.saveMedicalRecord(to2);
                    medicService.deleteScheduledVisit(to.getId());
                    populateContainer(true);
                }).open(), plannedGrid));

        /**
         * Detail
         */
        buttonLayout.add(componentFactory.createDetailGridButton(
                item -> ScheduledVisitDialog.detail(medicService.getScheduledVisitById(item.getId())).open(),
                plannedGrid));

        populateContainer(true);
    }

    private void prepareGrid(Grid<ScheduledVisitOverviewTO> grid, boolean fullTime) {
        ComponentFactory componentFactory = new ComponentFactory();
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        UIUtils.applyGrassDefaultStyle(grid);
        grid.addColumn(new IconRenderer<>(to -> {
            if (LocalDateTime.now().compareTo(to.getDateTime()) > 0) {
                return ImageIcon.WARNING_16_ICON.createImage("Zmeškáno");
            } else if (MedicUtil.isVisitPending(to.getDateTime())) {
                return ImageIcon.CLOCK_16_ICON.createImage("Blíží se");
            } else {
                return new Span();
            }
        }, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

        grid.addColumn(ScheduledVisitOverviewTO::getPurpose).setKey("purpose").setHeader("Účel");
        if (fullTime) grid.addColumn(new LocalDateTimeRenderer<>(to -> to.getDateTime(),
                        () -> DateTimeFormatter.ofPattern("d. MMMM yyyy H:mm", componentFactory.createLocale()))).setKey("date")
                .setHeader("Datum").setSortable(false).setWidth("200px").setFlexGrow(0);
        else grid.addColumn(to -> componentFactory.formatMonthYear(to.getDateTime())).setKey("date").setHeader("Datum")
                .setSortable(false).setWidth("150px").setFlexGrow(0);
        grid.addColumn(to -> to.getInstitutionCaption()).setKey("institution").setHeader("Instituce");

        grid.setWidthFull();
        grid.setHeight("250px");
        grid.setSelectionMode(SelectionMode.SINGLE);
    }

    private void createToBePlannedTable() {
        Consumer<ScheduledVisitTO> onSave = to -> {
            medicService.saveScheduledVisit(to);
            populateContainer(false);
        };

        Consumer<Long> onDelete = id -> {
            medicService.deleteScheduledVisit(id);
            populateContainer(false);
        };

        Consumer<ScheduledVisitOverviewTO> createEditDialog =
                to -> ScheduledVisitDialog.edit(medicService.getScheduledVisitById(to.getId()), onSave).open();

        /**
         * Přehled
         */
        Div headerDiv = new Div(new Strong("K objednání"));
        headerDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        add(headerDiv);

        prepareGrid(toBePlannedGrid, false);
        add(toBePlannedGrid);
        toBePlannedGrid.addItemDoubleClickListener(e -> createEditDialog.accept(e.getItem()));

        ComponentFactory componentFactory = new ComponentFactory();
        Div buttonLayout = componentFactory.createButtonLayout();
        add(buttonLayout);

        /**
         * Naplánovat objednání
         */
        buttonLayout.add(componentFactory.createCreateButton("Naplánovat objednání",
                e -> ScheduledVisitDialog.createToBePlanned(onSave).open()));

        /**
         * Detail
         */
        buttonLayout.add(componentFactory.createDetailGridButton(
                item -> ScheduledVisitDialog.detail(medicService.getScheduledVisitById(item.getId())).open(),
                toBePlannedGrid));

        /**
         * Objednat návštěvy
         */
        buttonLayout.add(
                componentFactory.createGridSingleButton("Objednáno", VaadinIcon.CALENDAR.create(), toBePlannedTO -> {
                    ScheduledVisitTO toBePlannedVisitTO = medicService.getScheduledVisitById(toBePlannedTO.getId());
                    ScheduledVisitDialog.create(toBePlannedVisitTO, to -> {
                        medicService.saveScheduledVisit(to);
                        if (toBePlannedVisitTO.getPeriod() > 0) {
                            // posuň plánování a ulož úpravu
                            toBePlannedVisitTO.setDateTime(
                                    toBePlannedVisitTO.getDateTime().plusMonths(toBePlannedVisitTO.getPeriod()));
                            medicService.saveScheduledVisit(toBePlannedVisitTO);
                        } else {
                            // nemá pravidelnost - návštěva byla objednána,
                            // plánování návštěvy lze smazat
                            medicService.deleteScheduledVisit(toBePlannedVisitTO.getId());
                        }

                        populateContainer(true);
                        populateContainer(false);
                    }).open();
                }, toBePlannedGrid));

        /**
         * Úprava naplánování
         */
        buttonLayout.add(componentFactory.createEditGridButton(
                to -> ScheduledVisitDialog.edit(medicService.getScheduledVisitById(to.getId()), onSave).open(),
                toBePlannedGrid));

        /**
         * Smazání naplánování
         */
        buttonLayout.add(componentFactory.createDeleteGridButton(to -> onDelete.accept(to.getId()), toBePlannedGrid));

        populateContainer(false);
    }
}