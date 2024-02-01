package cz.gattserver.grass.medic.web.tabs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.IconRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.HtmlDiv;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.ui.components.button.*;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitState;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;
import cz.gattserver.grass.medic.util.MedicUtil;
import cz.gattserver.grass.medic.web.MedicalRecordCreateDialog;
import cz.gattserver.grass.medic.web.Operation;
import cz.gattserver.grass.medic.web.ScheduledVisitsCreateDialog;
import cz.gattserver.grass.medic.web.SchuduledVisitDetailDialog;

public class ScheduledVisitsTab extends Div {

	private static final long serialVersionUID = -5013459007975657195L;

	private transient MedicService medicService;

	private Grid<ScheduledVisitTO> toBePlannedGrid = new Grid<>();
	private Grid<ScheduledVisitTO> plannedGrid = new Grid<>();

	public ScheduledVisitsTab() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.forLanguageTag("CS"));
		Div div = new HtmlDiv("<strong>Dnes je: </strong>" + LocalDate.now().format(formatter));
		div.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(div);

		createPlannedGrid();
		createToBePlannedTable();
	}

	private void openCreateWindow(final boolean planned, ScheduledVisitTO scheduledVisitDTO) {
		new ScheduledVisitsCreateDialog(planned ? Operation.PLANNED : Operation.TO_BE_PLANNED, scheduledVisitDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				populateContainer(planned);
			}
		}.open();
	}

	private void openCompletedWindow(final ScheduledVisitTO scheduledVisitDTO) {
		new MedicalRecordCreateDialog(scheduledVisitDTO) {
			private static final long serialVersionUID = -7566950396535469316L;

			@Override
			protected void onSuccess() {
				try {
					getMedicFacade().deleteScheduledVisit(scheduledVisitDTO);
				} catch (Exception e) {
					new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
				}
				populateContainer(true);
			}
		}.open();
	}

	private void openDeleteWindow(final ScheduledVisitTO visit, final boolean planned) {
		try {
			getMedicFacade().deleteScheduledVisit(visit);
			populateContainer(planned);
		} catch (Exception e) {
			new ErrorDialog("Nezdařilo se smazat vybranou položku").open();
		}
	}

	private void populateContainer(boolean planned) {
		Grid<ScheduledVisitTO> grid = planned ? plannedGrid : toBePlannedGrid;
		if (planned) {
			plannedGrid.setItems(getMedicFacade().getAllScheduledVisits(planned));
		} else {
			toBePlannedGrid.setItems(getMedicFacade().getAllScheduledVisits(planned));
		}
		grid.getDataProvider().refreshAll();
		grid.deselectAll();
		Column<ScheduledVisitTO> dateColumn = grid.getColumnByKey("date");
		grid.sort(Arrays.asList(new GridSortOrder<ScheduledVisitTO>(dateColumn, SortDirection.ASCENDING)));
	}

	private void createPlannedGrid() {

		/**
		 * Přehled
		 */
		Div headerDiv = new Div(new Strong("Naplánované návštěvy"));
		headerDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(headerDiv);

		prepareGrid(plannedGrid, true);
		add(plannedGrid);
		plannedGrid.addItemDoubleClickListener(e -> new SchuduledVisitDetailDialog(e.getItem().getId()).open());

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		/**
		 * Založení nové návštěvy
		 */
		final Button newTypeBtn = new CreateButton("Naplánovat návštěvu", event -> openCreateWindow(true, null));
		buttonLayout.add(newTypeBtn);

		/**
		 * Úprava návštěvy
		 */
		final Button modifyBtn = new ModifyGridButton<ScheduledVisitTO>(to -> openCreateWindow(true, to), plannedGrid);
		buttonLayout.add(modifyBtn);

		/**
		 * Smazání návštěvy
		 */
		final Button deleteBtn = new DeleteGridButton<ScheduledVisitTO>(
				set -> openDeleteWindow(set.iterator().next(), true), plannedGrid);
		buttonLayout.add(deleteBtn);

		/**
		 * Absolvování návštěvy
		 */
		final Button completedBtn = new GridButton<ScheduledVisitTO>("Absolvováno",
				set -> openCompletedWindow(set.iterator().next()), plannedGrid);
		completedBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "Upravit"));
		buttonLayout.add(completedBtn);

		/**
		 * Detail
		 */
		final Button detailBtn = new DetailGridButton<>(
				item -> new SchuduledVisitDetailDialog(item.getId()).open(), plannedGrid);
		buttonLayout.add(detailBtn);

		populateContainer(true);
	}

	private void prepareGrid(Grid<ScheduledVisitTO> grid, boolean fullTime) {
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		UIUtils.applyGrassDefaultStyle(grid);
		grid.addColumn(new IconRenderer<>(item -> {
			if (item.getState().equals(ScheduledVisitState.MISSED)) {
				Image img = new Image(ImageIcon.WARNING_16_ICON.createResource(), "Zmeškáno");
				img.addClassName(UIUtils.GRID_ICON_CSS_CLASS);
				return img;
			} else {
				if (MedicUtil.isVisitPending(item))
					return new Image(ImageIcon.CLOCK_16_ICON.createResource(), "Blíží se");
			}
			return new Span();
		}, c -> "")).setFlexGrow(0).setWidth("31px").setHeader("").setTextAlign(ColumnTextAlign.CENTER);

		grid.addColumn(ScheduledVisitTO::getPurpose).setKey("purpose").setHeader("Účel");
		if (fullTime)
			grid.addColumn(new LocalDateTimeRenderer<>(to -> to.getDate().atTime(to.getTime()),
							() -> DateTimeFormatter.ofPattern("d. MMMM yyyy H:mm", Locale.forLanguageTag("CS")))).setKey("date")
					.setHeader("Datum").setSortable(false).setWidth("200px").setFlexGrow(0);
		else
			grid.addColumn(new LocalDateTimeRenderer<>(to -> to.getDate().atStartOfDay(),
							() -> DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("CS")))).setKey(
							"date")
					.setHeader("Datum").setSortable(false).setWidth("150px").setFlexGrow(0);
		grid.addColumn(new TextRenderer<>(
						to -> to.getInstitution() == null ? "" : to.getInstitution().getName())).setKey("institution")
				.setHeader("Instituce");

		grid.setWidthFull();
		grid.setHeight("250px");
		grid.setSelectionMode(SelectionMode.SINGLE);
	}

	private void createToBePlannedTable() {

		/**
		 * Přehled
		 */
		Div headerDiv = new Div(new Strong("K objednání"));
		headerDiv.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		add(headerDiv);

		prepareGrid(toBePlannedGrid, false);
		add(toBePlannedGrid);
		toBePlannedGrid.addItemDoubleClickListener(e -> new SchuduledVisitDetailDialog(e.getItem().getId()).open());

		ButtonLayout buttonLayout = new ButtonLayout();
		add(buttonLayout);

		/**
		 * Naplánovat objednání
		 */
		final Button newBtn = new CreateButton("Naplánovat objednání", e -> openCreateWindow(false, null));
		buttonLayout.add(newBtn);

		/**
		 * Detail
		 */
		final Button detailBtn = new DetailGridButton<>(
				item -> new SchuduledVisitDetailDialog(item.getId()).open(), toBePlannedGrid);
		buttonLayout.add(detailBtn);

		/**
		 * Objednat návštěvy
		 */
		final Button planBtn = new GridButton<>("Objednáno", toBePlannedGrid);
		planBtn.setIcon(new Image(ImageIcon.CALENDAR_16_ICON.createResource(), "Objednáno"));
		planBtn.addClickListener(event -> {
			final ScheduledVisitTO toBePlannedVisitDTO = toBePlannedGrid.getSelectedItems()
					.iterator().next();

			ScheduledVisitTO newDto = getMedicFacade().createPlannedScheduledVisitFromToBePlanned(toBePlannedVisitDTO);
			new ScheduledVisitsCreateDialog(Operation.PLANNED_FROM_TO_BE_PLANNED, newDto) {
				private static final long serialVersionUID = -7566950396535469316L;

				@Override
				protected void onSuccess() {
					try {
						if (toBePlannedVisitDTO.getPeriod() > 0) {
							// posuň plánování a ulož úpravu
							toBePlannedVisitDTO
									.setDate(toBePlannedVisitDTO.getDate().plusMonths(toBePlannedVisitDTO.getPeriod()));
							getMedicFacade().saveScheduledVisit(toBePlannedVisitDTO);
						} else {
							// nemá pravidelnost - návštěva byla objednána,
							// plánování návštěvy lze smazat
							getMedicFacade().deleteScheduledVisit(toBePlannedVisitDTO);
						}

						populateContainer(true);
						populateContainer(false);
					} catch (Exception ex) {
						new ErrorDialog("Nezdařilo se naplánovat příští objednání").open();
					}
				}
			}.open();
		});
		buttonLayout.add(planBtn);

		/**
		 * Úprava naplánování
		 */
		final Button modifyBtn = new ModifyGridButton<>(to -> openCreateWindow(false, to),
				toBePlannedGrid);
		buttonLayout.add(modifyBtn);

		/**
		 * Smazání naplánování
		 */
		final Button deleteBtn = new DeleteGridButton<>("Smazat",
				set -> openDeleteWindow(set.iterator().next(), false),
				set -> "Opravdu smazat '" + set.iterator().next().getPurpose() + "' ?", toBePlannedGrid);
		buttonLayout.add(deleteBtn);

		populateContainer(false);
	}

	protected MedicService getMedicFacade() {
		if (medicService == null)
			medicService = SpringContextHelper.getBean(MedicService.class);
		return medicService;
	}

}