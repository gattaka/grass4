package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.DoubleRangeValidator;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import cz.gattserver.grass.drinks.model.domain.WhiskeyType;
import cz.gattserver.grass.drinks.model.interfaces.WhiskeyTO;
import cz.gattserver.common.ui.RatingStars;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.io.Serial;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public abstract class WhiskeyDialog extends DrinkDialog<WhiskeyTO> {

	public WhiskeyDialog(WhiskeyTO to) {
		super(to);
	}

	public WhiskeyDialog() {
		super();
	}

	@Override
	protected WhiskeyTO createNewInstance() {
		WhiskeyTO formTO = new WhiskeyTO();
		formTO.setAlcohol(0d);
		formTO.setYears(0);
        formTO.setRating(2.5);
		return formTO;
	}

	@Override
	protected FormLayout createForm(Binder<WhiskeyTO> binder) {
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("100px", 3));

		TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(WhiskeyTO::getName, WhiskeyTO::setName);
		layout.add(nameField);
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

		TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(WhiskeyTO::getCountry, WhiskeyTO::setCountry);
		layout.add(countryField);
		countryField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

		RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(WhiskeyTO::getRating, WhiskeyTO::setRating);
		layout.add(ratingStars);

		TextField yearsField = new TextField("Stáří (roky)");
		binder.forField(yearsField)
				.withConverter(new StringToIntegerConverter(null, "Stáří (roky) musí být celé číslo"))
				.asRequired(new IntegerRangeValidator("Stáří je mimo rozsah (1-100)", 1, 100))
				.bind(WhiskeyTO::getYears, WhiskeyTO::setYears);
		layout.add(yearsField);

		TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField)
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {

                    @Serial
                    private static final long serialVersionUID = -3065677006005053078L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}

				}).asRequired(new DoubleRangeValidator("Obsah alkoholu je mimo rozsah (1-100)", 1d, 100d))
				.bind(WhiskeyTO::getAlcohol, WhiskeyTO::setAlcohol);
		layout.add(alcoholField);

		ComboBox<WhiskeyType> whiskeyTypeField = new ComboBox<>("Typ Whiskey", Arrays.asList(WhiskeyType.values()));
		whiskeyTypeField.setItemLabelGenerator(WhiskeyType::getCaption);
		binder.forField(whiskeyTypeField).asRequired().bind(WhiskeyTO::getWhiskeyType, WhiskeyTO::setWhiskeyType);
		layout.add(whiskeyTypeField);

		TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(WhiskeyTO::getDescription, WhiskeyTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("300px");
		layout.add(descriptionField);
		layout.setColspan(descriptionField, 3);

		return layout;
	}

}
