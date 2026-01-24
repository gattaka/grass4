package cz.gattserver.common.ui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.BeforeLeaveEvent;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class ComponentFactory {

    // https://vaadin.com/docs/latest/components/icons/default-icons

    /* Tlačítka */

    private <T> Button createGridSetButton(Function<ComponentEventListener<ClickEvent<Button>>, Button> buttonFactory,
                                           Consumer<Set<T>> clickListener, Grid<T> grid) {
        Button btn = buttonFactory.apply(e -> clickListener.accept(grid.getSelectedItems()));
        btn.setEnabled(false);
        Function<Set<T>, Boolean> finalEnableResolver = t -> !grid.getSelectedItems().isEmpty();
        grid.addSelectionListener(e -> btn.setEnabled(finalEnableResolver.apply(e.getAllSelectedItems())));
        return btn;
    }

    private <T> Button createGridSingleButton(
            Function<ComponentEventListener<ClickEvent<Button>>, Button> buttonFactory, Consumer<T> clickListener,
            Grid<T> grid) {
        Button btn = buttonFactory.apply(e -> clickListener.accept(grid.getSelectedItems().iterator().next()));
        btn.setEnabled(false);
        Function<Set<T>, Boolean> finalEnableResolver = set -> set.size() == 1;
        grid.addSelectionListener(e -> btn.setEnabled(finalEnableResolver.apply(e.getAllSelectedItems())));
        return btn;
    }

    public <T> Button createGridButton(String caption, Component icon, Consumer<Set<T>> clickListener, Grid<T> grid,
                                       Function<Set<T>, Boolean> enableResolver) {
        Button btn = new Button(caption, e -> clickListener.accept(grid.getSelectedItems()));
        btn.setIcon(icon);
        btn.setEnabled(false);
        if (enableResolver == null) enableResolver = t -> !grid.getSelectedItems().isEmpty();
        Function<Set<T>, Boolean> finalEnableResolver = enableResolver;
        grid.addSelectionListener(e -> btn.setEnabled(finalEnableResolver.apply(e.getAllSelectedItems())));
        return btn;
    }

    public Button createCopyButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Kopírovat", clickListener);
        btn.setIcon(VaadinIcon.COPY.create());
        return btn;
    }

    public <T> Button createCopyGridButton(Consumer<T> clickListener, Grid<T> grid) {
        return createGridSingleButton(l -> createCopyButton(l), clickListener, grid);
    }

    public Button createDetailButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Detail", clickListener);
        btn.setIcon(VaadinIcon.EYE.create());
        return btn;
    }

    public <T> Button createDetailGridButton(Consumer<T> clickListener, Grid<T> grid) {
        return createGridSingleButton(l -> createDetailButton(l), clickListener, grid);
    }

    public Button createDownloadButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Stáhnout", clickListener);
        btn.setIcon(VaadinIcon.DOWNLOAD.create());
        return btn;
    }

    public <T> Button createDownloadGridButton(Consumer<T> clickListener, Grid<T> grid) {
        return createGridSingleButton(l -> createDownloadButton(l), clickListener, grid);
    }

    public Button createZipButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Zabalit do ZIP", clickListener);
        btn.setIcon(VaadinIcon.FILE_ZIP.create());
        return btn;
    }

    public Button createUnmarkFavouriteButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Odebrat z oblíbených", clickListener);
        btn.setIcon(VaadinIcon.HEART.create());
        return btn;
    }

    public Button createMarkFavouriteButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Přidat do oblíbených", clickListener);
        btn.setIcon(VaadinIcon.HEART_O.create());
        return btn;
    }

    public Button createEditButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Upravit", clickListener);
        btn.setIcon(VaadinIcon.EDIT.create());
        return btn;
    }

    public <T> Button createEditGridButton(Consumer<T> clickListener, Grid<T> grid) {
        return createGridSingleButton(l -> createEditButton(l), clickListener, grid);
    }

    public Button createCreateDirButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        Button btn = new Button("Vytvořit adresář", clickListener);
        btn.setIcon(VaadinIcon.FOLDER_ADD.create());
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

    public <T> Button createMoveGridButton(Consumer<Set<T>> clickListener, Grid<T> grid) {
        return createGridSetButton(l -> createMoveButton(l), clickListener, grid);
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

    public <T> Button createDeleteGridButton(Consumer<T> clickListener, Grid<T> grid) {
        return createGridSingleButton(l -> createDeleteButton(l), clickListener, grid);
    }

    public <T> Button createDeleteGridSetButton(Consumer<Set<T>> clickListener, Grid<T> grid) {
        return createGridSetButton(l -> createDeleteButton(l), clickListener, grid);
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

    public Div createButtonLayout(boolean topMargin) {
        Div div = new Div();
        if (topMargin) div.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        div.setWidthFull();
        div.addClassName(UIUtils.FLEX_DIV_CLASS);
        return div;
    }

    public Div createButtonLayout() {
        return createButtonLayout(true);
    }

    /*
     * Dialogy
     */

    public Dialog createBeforeLeaveConfirmDialog(BeforeLeaveEvent beforeLeaveEvent) {
        return new ConfirmDialog(UIUtils.ON_BEFORE_UNLOAD_WARNING,
                e -> beforeLeaveEvent.getContinueNavigationAction().proceed());
    }

    /*
     * Hlášení, texty
     */
    public String createRequiredLabel() {
        return "Toto pole je povinné";
    }
}