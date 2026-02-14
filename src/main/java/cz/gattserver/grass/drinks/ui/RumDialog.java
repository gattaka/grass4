package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import cz.gattserver.grass.drinks.model.domain.RumType;
import cz.gattserver.grass.drinks.model.interfaces.RumTO;
import cz.gattserver.common.ui.RatingStars;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.io.Serial;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public abstract class RumDialog extends DrinkDialog<RumTO> {

	public RumDialog(RumTO to) {
		super(to);
	}

	public RumDialog() {
		super();
	}

	@Override
	protected RumTO createNewInstance() {
		RumTO formTO = new RumTO();
		formTO.setRumType(RumType.DARK);
		formTO.setAlcohol(0d);
        formTO.setRating(2.5);
		return formTO;
	}

	@Override
	protected FormLayout createForm(Binder<RumTO> binder) {
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("100px", 3));

		TextField nameField = new TextField("Název");
		nameField.setWidth("320px");
		binder.forField(nameField).asRequired().bind(RumTO::getName, RumTO::setName);
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		layout.add(nameField);

		TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(RumTO::getCountry, RumTO::setCountry);
		countryField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		layout.add(countryField);

		RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(RumTO::getRating, RumTO::setRating);
		layout.add(ratingStars);

		TextField yearsField = new TextField("Stáří (roky)");
		binder.forField(yearsField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(null, "Stáří (roky) musí být celé číslo"))
				.bind(RumTO::getYears, RumTO::setYears);
		layout.add(yearsField);

		TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField)
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {

					@Serial
                    private static final long serialVersionUID = -300373818173873640L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}

				}).asRequired(new DoubleRangeValidator("Obsah alkoholu je mimo rozsah (1-100)", 1d, 100d))
				.bind(RumTO::getAlcohol, RumTO::setAlcohol);
		alcoholField.setWidth("80px");
		layout.add(alcoholField);

		ComboBox<RumType> rumTypeField = new ComboBox<>("Typ rumu", Arrays.asList(RumType.values()));
		rumTypeField.setItemLabelGenerator(RumType::getCaption);
		binder.forField(rumTypeField).asRequired().bind(RumTO::getRumType, RumTO::setRumType);
		layout.add(rumTypeField);

		TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(RumTO::getDescription, RumTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");
		layout.add(descriptionField);
		layout.setColspan(descriptionField, 3);

		return layout;
	}

}
