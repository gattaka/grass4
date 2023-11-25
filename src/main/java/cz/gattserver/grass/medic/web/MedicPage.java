package cz.gattserver.grass.medic.web;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.ui.pages.template.AccessDeniedErrorPage;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.medic.MedicSection;
import cz.gattserver.grass.medic.web.tabs.MedicalInstitutionsTab;
import cz.gattserver.grass.medic.web.tabs.MedicalRecordsTab;
import cz.gattserver.grass.medic.web.tabs.MedicamentsTab;
import cz.gattserver.grass.medic.web.tabs.PhysiciansTab;
import cz.gattserver.grass.medic.web.tabs.ScheduledVisitsTab;

@Route("medic")
@PageTitle("Medic")
public class MedicPage extends OneColumnPage implements BeforeEnterObserver {

	private static final long serialVersionUID = -7969964922025344992L;

	private Tabs tabSheet;
	private Div pageLayout;

	private Tab scheduledVisitsTab;
	private Tab medicalRecordsTab;
	private Tab medicalInstitutionsTab;
	private Tab medicamentsTab;
	private Tab physiciansTab;

	public MedicPage() {
		init();
	}

	private ScheduledVisitsTab switchScheduledVisitsTab() {
		pageLayout.removeAll();
		ScheduledVisitsTab tab = new ScheduledVisitsTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(scheduledVisitsTab);
		return tab;
	}

	private MedicalRecordsTab switchMedicalRecordsTab() {
		pageLayout.removeAll();
		MedicalRecordsTab tab = new MedicalRecordsTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(medicalRecordsTab);
		return tab;
	}

	private MedicalInstitutionsTab switchMedicalInstitutionsTab() {
		pageLayout.removeAll();
		MedicalInstitutionsTab tab = new MedicalInstitutionsTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(medicalInstitutionsTab);
		return tab;
	}

	private MedicamentsTab switchMedicamentsTab() {
		pageLayout.removeAll();
		MedicamentsTab tab = new MedicamentsTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(medicamentsTab);
		return tab;
	}

	private PhysiciansTab switchPhysiciansTab() {
		pageLayout.removeAll();
		PhysiciansTab tab = new PhysiciansTab();
		pageLayout.add(tab);
		tabSheet.setSelectedTab(physiciansTab);
		return tab;
	}

	@Override
	protected void createColumnContent(Div layout) {
		tabSheet = new Tabs();
		layout.add(tabSheet);

		scheduledVisitsTab = new Tab("Plánované návštěvy");
		medicalRecordsTab = new Tab("Záznamy");
		medicalInstitutionsTab = new Tab("Instituce");
		medicamentsTab = new Tab("Medikamenty");
		physiciansTab = new Tab("Doktoři");
		tabSheet.add(scheduledVisitsTab, medicalRecordsTab, medicalInstitutionsTab, medicalInstitutionsTab,
				medicamentsTab, physiciansTab);

		pageLayout = new Div();
		layout.add(pageLayout);

		tabSheet.addSelectedChangeListener(e -> {
			pageLayout.removeAll();
			switch (tabSheet.getSelectedIndex()) {
			default:
			case 0:
				switchScheduledVisitsTab();
				break;
			case 1:
				switchMedicalRecordsTab();
				break;
			case 2:
				switchMedicalInstitutionsTab();
				break;
			case 3:
				switchMedicamentsTab();
				break;
			case 4:
				switchPhysiciansTab();
				break;
			}
		});

		switchScheduledVisitsTab();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		if (!SpringContextHelper.getBean(MedicSection.class).isVisibleForRoles(getUser().getRoles()))
			event.rerouteTo(AccessDeniedErrorPage.class);
	}
}