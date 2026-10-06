package cz.gattserver.grass.medic.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;

import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.server.streams.UploadHandler;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.ui.CopyTextDialog;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.hw.HWRequestHandlerConfig;
import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.medic.MedicRequestHandlerConfig;
import cz.gattserver.grass.medic.interfaces.*;
import cz.gattserver.grass.medic.service.MedicService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MedicalRecordDialog extends EditWebDialog {

	public static MedicalRecordDialog detail(MedicalRecordTO originalTO) {
		return new MedicalRecordDialog(null, originalTO, null, true);
	}

	public static MedicalRecordDialog edit(MedicalRecordTO originalTO, Consumer<MedicalRecordTO> onSave) {
		return new MedicalRecordDialog(null, originalTO, onSave, false);
	}

	public static MedicalRecordDialog create(ScheduledVisitTO scheduledVisitDTO, Consumer<MedicalRecordTO> onSave) {
		return new MedicalRecordDialog(scheduledVisitDTO, null, onSave, false);
	}

	private MedicalRecordDialog(ScheduledVisitTO scheduledVisitTO, MedicalRecordTO originalTO,
			Consumer<MedicalRecordTO> onSave, boolean readOnly) {
		super("Záznam", readOnly);
		setWidth(800, Unit.PIXELS);

		MedicService medicService = SpringContextHelper.getBean(MedicService.class);

		Binder<MedicalRecordTO> binder = new Binder<>(MedicalRecordTO.class);
		MedicalRecordTO beanTO = originalTO == null ? new MedicalRecordTO() : originalTO.copy();
		if (scheduledVisitTO != null) {
			beanTO.setDateTime(scheduledVisitTO.getDateTime());
			beanTO.setInstitutionId(scheduledVisitTO.getInstitutionId());
		}
		binder.setBean(beanTO);

		List<MedicalInstitutionTO> medicalInstitutionTOList = medicService.getMedicalInstitutions();
		ComboBox<MedicalInstitutionTO> institutionComboBox = new ComboBox<>("Instituce", medicalInstitutionTOList);
		institutionComboBox.setWidthFull();
		institutionComboBox.setReadOnly(readOnly);
		componentFactory.attachLink(institutionComboBox, f -> MedicalInstitutionDialog.detail(
				medicService.getMedicalInstitutionById(originalTO.getInstitutionId())).open());
		componentFactory.bind(binder.forField(institutionComboBox).asRequired(componentFactory.createRequiredLabel()),
				medicalInstitutionTOList, MedicalRecordTO::getInstitutionId, MedicalRecordTO::setInstitutionId);

		List<PhysicianTO> physicians = medicService.getPhysicians();
		ComboBox<PhysicianTO> physicianComboBox = new ComboBox<>("Ošetřující lékař", physicians);
		physicianComboBox.setWidthFull();
		physicianComboBox.setReadOnly(readOnly);
		componentFactory.attachLink(physicianComboBox,
				f -> PhysicianDialog.detail(medicService.getPhysicianById(originalTO.getPhysicianId())).open());
		componentFactory.bind(binder.forField(physicianComboBox), physicians, MedicalRecordTO::getPhysicianId,
				MedicalRecordTO::setPhysicianId);

		institutionComboBox.addValueChangeListener(e -> {
			if (institutionComboBox.getValue() == null || physicianComboBox.getValue() != null)
				return;
			MedicalInstitutionTO to = institutionComboBox.getValue();
			PhysicianTO pTO = medicService.getPhysicianByLastVisit(to.getId());
			if (pTO != null)
				physicianComboBox.setValue(pTO);
		});

		HorizontalLayout line1 = new HorizontalLayout(institutionComboBox, physicianComboBox);
		line1.setWidthFull();
		line1.setPadding(false);
		layout.add(line1);

		ComponentFactory componentFactory = new ComponentFactory();

		DateTimePicker dateTimePicker = componentFactory.createDateTimePicker("Datum návštěvy");
		dateTimePicker.setReadOnly(readOnly);
		binder.forField(dateTimePicker).asRequired(componentFactory.createRequiredLabel())
				.bind(MedicalRecordTO::getDateTime, MedicalRecordTO::setDateTime);

		HorizontalLayout line2 = new HorizontalLayout(dateTimePicker);
		line2.setWidthFull();
		line2.setPadding(false);
		layout.add(line2);

		TextArea recordField = new TextArea("Záznam");
		layout.add(recordField);
		recordField.setWidthFull();
		recordField.setHeight(100, Unit.PIXELS);
		recordField.setReadOnly(readOnly);
		binder.forField(recordField).asRequired(componentFactory.createRequiredLabel())
				.bind(MedicalRecordTO::getRecord, MedicalRecordTO::setRecord);

		Map<String, Long> medicamentsNameToIdMap = new HashMap<>();
		Map<Long, String> medicamentIdToNameMap = new HashMap<>();
		for (MedicamentTO mto : medicService.getMedicaments()) {
			medicamentsNameToIdMap.put(mto.getName(), mto.getId());
			medicamentIdToNameMap.put(mto.getId(), mto.getName());
		}

		TokenField tokenField = new TokenField("Medikamenty", medicamentsNameToIdMap.keySet());
		tokenField.setAllowNewItems(false);
		tokenField.setReadOnly(readOnly);
		binder.forField(tokenField)
				.bind(to -> to.getMedicaments().stream().map(medicamentIdToNameMap::get).collect(Collectors.toSet()),
						(to, val) -> {
							to.setMedicaments(
									val.stream().map(medicamentsNameToIdMap::get).collect(Collectors.toSet()));
						});
		layout.add(tokenField);

		Grid<ReportFileTO> grid = new Grid<>();
		grid.setWidthFull();
		grid.setMinHeight(100, Unit.PIXELS);
		grid.setAllRowsVisible(true);
		grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		UIUtils.applyGrassDefaultStyle(grid);
		grid.addColumn(new ComponentRenderer<>(to -> {
			if (readOnly) {
				return componentFactory.createInlineButton(to.getName(),
						e -> downloadReport(originalTO.getId(), to.getName()));
			} else {
				return new Text(to.getName());
			}
		})).setHeader("Název");
		grid.addColumn(new LocalDateTimeRenderer<>(ReportFileTO::getLastModified, "d. MM. yyyy HH:mm")).setKey("datum")
				.setHeader("Datum").setWidth("150px").setFlexGrow(0);
		grid.addColumn(new TextRenderer<>(ReportFileTO::getHumanSize)).setHeader("Velikost")
				.setTextAlign(ColumnTextAlign.END).setWidth("100px").setFlexGrow(0);
		if (!readOnly) {
			grid.addColumn(new ComponentRenderer<>(item -> componentFactory.createDeleteInlineButton(e -> {
				beanTO.getFiles().remove(item);
				populateGrid(grid, beanTO.getFiles());
			}))).setHeader("Smazat").setTextAlign(ColumnTextAlign.END).setWidth("100px").setFlexGrow(0);
		}
		layout.add(grid);

		if (originalTO != null)
			populateGrid(grid, originalTO.getFiles());

		if (!readOnly) {
			VerticalLayout prilohaBoxLayout = new VerticalLayout();
			prilohaBoxLayout.setWidthFull();
			prilohaBoxLayout.setPadding(false);

			HorizontalLayout prilohyLayout = new HorizontalLayout();
			prilohaBoxLayout.add(prilohyLayout);

			Upload upload = new Upload();
			upload.setWidthFull();
			prilohaBoxLayout.add(upload);

			upload.addFileRejectedListener(
					e -> Notification.show(e.getErrorMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR));

			UploadHandler uploadHandler = UploadHandler.inMemory((metadata, bytes) -> {
				try {
					ReportFileTO fileTO = new ReportFileTO(metadata.fileName(), metadata.contentType(), bytes.length,
							bytes);
					beanTO.getFiles().add(fileTO);
					populateGrid(grid, beanTO.getFiles());
				} catch (Exception e) {
					log.error("Při čtení souboru {} došlo k chybě", metadata.fileName(), e);
					throw new RuntimeException(e);
				}
			});

			upload.setUploadHandler(uploadHandler);
			upload.addAllFinishedListener(e -> upload.clearFileList());
			layout.add(prilohaBoxLayout);
		}

		getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
			if (binder.writeBeanIfValid(beanTO)) {
				beanTO.setFiles(beanTO.getFiles());
				onSave.accept(beanTO);
				close();
			}
		}, e -> close(), !readOnly));

		if (originalTO != null) {
			binder.readBean(originalTO);
			beanTO.setFiles(medicService.getMedicalRecordReports(originalTO.getId()));
			populateGrid(grid, beanTO.getFiles());
		}
	}

	private void populateGrid(Grid<ReportFileTO> grid, Set<ReportFileTO> files) {
		grid.setItems(files);
		grid.getDataProvider().refreshAll();
	}

	private void downloadReport(Long recordId, String item) {
		UI.getCurrent().getPage().executeJs(
				"window.open('" + UIUtils.getContextPath() + "/" + MedicRequestHandlerConfig.MEDIC_PATH + "/" + recordId
						+ "/" + item + "', '_blank');");
	}

}