package cz.gattserver.grass.medic.web;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

public class ScheduledVisitDetailDialog extends Dialog {

    private static final long serialVersionUID = -1240133390770972624L;

    public ScheduledVisitDetailDialog(ScheduledVisitTO to) {
        MedicService medicService = SpringContextHelper.getBean(MedicService.class);

        setWidth("400px");

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(false);
        add(layout);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.forLanguageTag("CS"));
        layout.add(new Strong("Datum"));
        layout.add(to.getDate().atTime(to.getTime()).format(formatter));

        layout.add(new Strong("Účel"));
        layout.add(to.getPurpose());

        ComponentFactory componentFactory = new ComponentFactory();

        layout.add(new Strong("Instituce"));
        final Div instButton = componentFactory.createInlineButton(to.getInstitution().getName(),
                e -> MedicalInstitutionDialog.detail(
                        medicService.getMedicalInstitutionById(to.getInstitution().getId())).open());
        instButton.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        layout.add(instButton);

        layout.add(new Strong("Navazuje na"));
        if (to.getRecord() != null) {
            final Div recordButton = componentFactory.createInlineButton(to.getRecord().toString(),
                    e -> MedicalRecordDialog.detail(medicService.getMedicalRecordById(to.getRecord().getId())).open());
            recordButton.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
            layout.add(recordButton);
        } else {
            layout.add("-");
        }

        layout.add(new Strong("Pravidelnost (měsíce)"));
        layout.add(String.valueOf(to.getPeriod()));

        layout.add(new Strong("Stav"));
        layout.add(String.valueOf(to.getState()));
    }
}
