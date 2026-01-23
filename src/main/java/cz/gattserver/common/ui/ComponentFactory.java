package cz.gattserver.common.ui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.grass.core.ui.components.button.ImageButton;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

public class ComponentFactory {

    // https://vaadin.com/docs/latest/components/icons/default-icons

    /* Tlačítka */

    public Button createZipButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Zabalit do ZIP", clickListener);
        btn.setIcon(VaadinIcon.FILE_ZIP.create());
        return btn;
    }

    public Button createUnmarkFavouriteButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Odebrat z oblíbených", clickListener);
        btn.setIcon(VaadinIcon.HEART_O.create());
        return btn;
    }

    public Button createMarkFavouriteButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Přidat do oblíbených", clickListener);
        btn.setIcon(VaadinIcon.HEART.create());
        return btn;
    }

    public Button createEditButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Upravit", clickListener);
        btn.setIcon(VaadinIcon.EDIT.create());
        return btn;
    }

    public Button createCreateButton(String caption, ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button(caption, clickListener);
        btn.setIcon(VaadinIcon.FILE_ADD.create());
        return btn;
    }

    public Button createCreateButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        return createCreateButton("Vytvořit", clickListener);
    }

    public Button createMoveButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Přesunout", clickListener);
        btn.setIcon(VaadinIcon.RECORDS.create());
        return btn;
    }

    public Button createPreviewButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Náhled", clickListener);
        btn.setIcon(VaadinIcon.PRESENTATION.create());
        return btn;
    }

    public Button createCopyFromContentButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Kopírovat z obsahu", clickListener);
        btn.setIcon(VaadinIcon.COPY.create());
        return btn;
    }

    public Button createSaveButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Uložit", clickListener);
        btn.setIcon(VaadinIcon.CLIPBOARD_CHECK.create());
        return btn;
    }

    public Button createSaveAndCloseButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = createSaveButton(clickListener);
        btn.setText("Uložit a zavřít");
        return btn;
    }

    public Button createSubmitButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Potvrdit", clickListener);
        btn.setIcon(VaadinIcon.CHECK.create());
        btn.addThemeVariants(ButtonVariant.AURA_PRIMARY);
        return btn;
    }

    public Button createStornoButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Storno", clickListener);
        btn.setIcon(VaadinIcon.CLOSE.create());
        return btn;
    }

    public Button createDeleteButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Smazat",
                e -> new ConfirmDialog("Smazat záznam?", ee -> clickListener.onComponentEvent(e)).open());
        btn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btn.setIcon(VaadinIcon.TRASH.create());
        return btn;
    }

    /* Input pole */

    public DatePicker.DatePickerI18n createDatePickerI18n() {
        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setDateFormats("d. M. yyyy", "d.M.yyyy", "d. M.yyyy", "d. M.yyyy");
        i18n.setFirstDayOfWeek(1);
        i18n.setMonthNames(
                Arrays.asList("Leden", "Únor", "Březen", "Duben", "Květen", "Červen", "Červenec", "Srpen", "Září",
                        "Říjen", "Listopad", "Prosinec"));
        i18n.setWeekdays(Arrays.asList("Neděle", "Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota"));
        i18n.setWeekdaysShort(Arrays.asList("Ne", "Po", "Út", "St", "Čt", "Pá", "So"));
        i18n.setToday("Dnes");
        i18n.setCancel("Storno");
        return i18n;
    }

    public DatePicker createDatePicker(String label) {
        DatePicker datePicker = new DatePicker(label);
        datePicker.setLocale(Locale.forLanguageTag("CS"));
        datePicker.setWidthFull();
        datePicker.setI18n(createDatePickerI18n());
        return datePicker;
    }

    public TimePicker createTimePicker(String label) {
        TimePicker timePicker = new TimePicker(label);
        timePicker.setLocale(Locale.forLanguageTag("CS"));
        timePicker.setWidthFull();
        return timePicker;
    }

    public DateTimePicker createDateTimePicker(String label) {
        DateTimePicker dateTimeField = new DateTimePicker(label);
        dateTimeField.setLocale(Locale.forLanguageTag("CS"));
        dateTimeField.setWidthFull();
        dateTimeField.setDatePickerI18n(createDatePickerI18n());
        return dateTimeField;
    }

    /**
     * Layouty
     */

    public HorizontalLayout createDialogButtonLayout() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        horizontalLayout.setSpacing(true);
        horizontalLayout.setWidthFull();
        horizontalLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        return horizontalLayout;
    }

    public HorizontalLayout createDialogCloseLayout(ComponentEventListener<ClickEvent<Button>> closeClickListener) {
        HorizontalLayout layout = createDialogButtonLayout();

        Button stornoButton = createStornoButton(closeClickListener);
        layout.add(stornoButton);

        return layout;
    }

    public HorizontalLayout createDialogSubmitOrCloseLayout(
            ComponentEventListener<ClickEvent<Button>> saveClickListener,
            ComponentEventListener<ClickEvent<Button>> closeClickListener, Consumer<Button> submitButtonDecorator) {
        HorizontalLayout layout = createDialogButtonLayout();

        Button submitButton = createSubmitButton(saveClickListener);
        Button stornoButton = createStornoButton(closeClickListener);
        layout.add(submitButton, stornoButton);

        if (submitButtonDecorator != null) submitButtonDecorator.accept(submitButton);

        return layout;
    }

    public HorizontalLayout createDialogSubmitOrCloseLayout(
            ComponentEventListener<ClickEvent<Button>> saveClickListener,
            ComponentEventListener<ClickEvent<Button>> closeClickListener) {
        return createDialogSubmitOrCloseLayout(saveClickListener, closeClickListener, null);
    }
}