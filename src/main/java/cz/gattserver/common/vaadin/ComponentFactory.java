package cz.gattserver.common.vaadin;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;

import java.sql.Time;
import java.util.Arrays;
import java.util.Locale;

public class ComponentFactory {

	public DatePicker.DatePickerI18n createDatePickerI18n() {
		DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
		i18n.setDateFormats("d. M. yyyy", "d.M.yyyy", "d. M.yyyy", "d. M.yyyy");
		i18n.setFirstDayOfWeek(1);
		i18n.setMonthNames(Arrays.asList("Leden", "Únor", "Březen", "Duben", "Květen", "Červen", "Červenec", "Srpen",
				"Září", "Říjen", "Listopad", "Prosinec"));
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

	public HorizontalLayout createDialogButtonLayout() {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
		horizontalLayout.setSpacing(true);
		horizontalLayout.setSizeFull();
		return horizontalLayout;
	}
}
