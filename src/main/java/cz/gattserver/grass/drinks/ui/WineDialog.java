package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import cz.gattserver.grass.drinks.model.domain.WineType;
import cz.gattserver.grass.drinks.model.interfaces.WineTO;
import cz.gattserver.common.ui.RatingStars;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.io.Serial;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public abstract class WineDialog extends DrinkDialog<WineTO> {

	public WineDialog(WineTO to) {
		super(to);
	}

	public WineDialog() {
		super();
	}

	@Override
	protected WineTO createNewInstance() {
		WineTO formTO = new WineTO();
		formTO.setYear(0);
        formTO.setRating(2.5);
		return formTO;
	}

	@Override
	protected FormLayout createForm(Binder<WineTO> binder) {
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("100px", 6));

		TextField wineryField = new TextField("Vinařství");
		binder.forField(wineryField).asRequired().bind(WineTO::getWinery, WineTO::setWinery);
		layout.add(wineryField);
		layout.setColspan(wineryField, 3);
		wineryField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

		TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(WineTO::getName, WineTO::setName);
		layout.add(nameField);
		layout.setColspan(nameField, 3);
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

		TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(WineTO::getCountry, WineTO::setCountry);
		layout.add(countryField);
		layout.setColspan(countryField, 4);

		RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(WineTO::getRating, WineTO::setRating);
		layout.add(ratingStars);
		layout.setColspan(ratingStars, 2);

		TextField yearsField = new TextField("Rok");
		binder.forField(yearsField).withConverter(new StringToIntegerConverter(null, "Rok musí být celé číslo"))
				.asRequired(new IntegerRangeValidator("Rok vína je mimo rozsah (1000-3000)", 1000, 3000))
				.bind(WineTO::getYear, WineTO::setYear);
		layout.add(yearsField);
		layout.setColspan(yearsField, 2);

		TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField).withNullRepresentation("")
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {
					@Serial
                    private static final long serialVersionUID = -5270872105505337431L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}
				}).bind(WineTO::getAlcohol, WineTO::setAlcohol);
		layout.add(alcoholField);
		layout.setColspan(alcoholField, 2);

		ComboBox<WineType> wineTypeField = new ComboBox<>("Typ vína", Arrays.asList(WineType.values()));
		wineTypeField.setItemLabelGenerator(WineType::getCaption);
		binder.forField(wineTypeField).asRequired().bind(WineTO::getWineType, WineTO::setWineType);
		layout.add(wineTypeField);
		layout.setColspan(wineTypeField, 2);

		TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(WineTO::getDescription, WineTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");
		layout.add(descriptionField);
		layout.setColspan(descriptionField, 6);

		return layout;
	}

}
