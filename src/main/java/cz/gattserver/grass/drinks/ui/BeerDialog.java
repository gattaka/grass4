package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import cz.gattserver.grass.drinks.model.domain.MaltType;
import cz.gattserver.grass.drinks.model.interfaces.BeerTO;
import cz.gattserver.common.ui.RatingStars;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.io.Serial;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public abstract class BeerDialog extends DrinkDialog<BeerTO> {

	private static final long serialVersionUID = -3221345832430984490L;

	public BeerDialog(BeerTO to) {
		super(to);
	}

	public BeerDialog() {
		super();
	}

	@Override
	protected BeerTO createNewInstance() {
		BeerTO formTO = new BeerTO();
		formTO.setCountry("ČR");
        formTO.setRating(2.5);
		formTO.setMaltType(MaltType.BARLEY);
		return formTO;
	}

	@Override
	protected FormLayout createForm(Binder<BeerTO> binder) {
		FormLayout layout = new FormLayout();
		layout.setResponsiveSteps(new FormLayout.ResponsiveStep("100px", 6));

		TextField breweryField = new TextField("Pivovar");
		binder.forField(breweryField).asRequired().bind(BeerTO::getBrewery, BeerTO::setBrewery);
		layout.add(breweryField);
		layout.setColspan(breweryField, 2);
		breweryField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

		TextField nameField = new TextField("Název");
		binder.forField(nameField).asRequired().bind(BeerTO::getName, BeerTO::setName);
		layout.add(nameField);
		layout.setColspan(nameField, 2);
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

		TextField countryField = new TextField("Země");
		binder.forField(countryField).asRequired().bind(BeerTO::getCountry, BeerTO::setCountry);
		layout.add(countryField);
		layout.setColspan(countryField, 2);
		countryField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

		TextField categoryField = new TextField("Kategorie (APA, IPA, ...)");
		binder.forField(categoryField).asRequired().bind(BeerTO::getCategory, BeerTO::setCategory);
		layout.add(categoryField);
		layout.setColspan(categoryField, 2);

		ComboBox<MaltType> maltTypeField = new ComboBox<>("Typ sladu", Arrays.asList(MaltType.values()));
		maltTypeField.setItemLabelGenerator(MaltType::getCaption);
		binder.forField(maltTypeField).asRequired().bind(BeerTO::getMaltType, BeerTO::setMaltType);
		layout.add(maltTypeField);
		layout.setColspan(maltTypeField, 2);

		RatingStars ratingStars = new RatingStars();
		binder.forField(ratingStars).asRequired().bind(BeerTO::getRating, BeerTO::setRating);
		layout.add(ratingStars);
		layout.setColspan(ratingStars, 2);

		TextField degreeField = new TextField("Stupně (°)");
		binder.forField(degreeField).withNullRepresentation("")
				.withConverter(new StringToDoubleConverter(null, "Stupně (°) musí být celé číslo") {

					@Serial
                    private static final long serialVersionUID = 6515529337922820217L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}

				}).bind(BeerTO::getDegrees, BeerTO::setDegrees);
		degreeField.setWidth("80px");
		layout.add(degreeField);
		layout.setColspan(degreeField, 2);

		TextField alcoholField = new TextField("Alkohol (%)");
		binder.forField(alcoholField).withNullRepresentation("")
				.withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {

					@Serial
                    private static final long serialVersionUID = -2812838817126081930L;

					@Override
					protected NumberFormat getFormat(Locale locale) {
						return NumberFormat.getNumberInstance(new Locale("cs", "CZ"));
					}

				}).bind(BeerTO::getAlcohol, BeerTO::setAlcohol);
		alcoholField.setWidth("80px");
		layout.add(alcoholField);
		layout.setColspan(alcoholField, 2);

		TextField ibuField = new TextField("Hořkost (IBU)");
		binder.forField(ibuField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(null, "Hořkost (IBU) musí být celé číslo"))
				.bind(BeerTO::getIbu, BeerTO::setIbu);
		ibuField.setWidth("80px");
		layout.add(ibuField);
		layout.setColspan(ibuField, 2);

		TextField maltsField = new TextField("Slady");
		binder.forField(maltsField).bind(BeerTO::getMalts, BeerTO::setMalts);
		maltsField.setWidth("290px");
		layout.add(maltsField);
		layout.setColspan(maltsField, 3);

		TextField hopsField = new TextField("Chmely");
		binder.forField(hopsField).bind(BeerTO::getHops, BeerTO::setHops);
		hopsField.setWidth("290px");
		layout.add(hopsField);
		layout.setColspan(hopsField, 3);

		TextArea descriptionField = new TextArea("Popis");
		binder.forField(descriptionField).asRequired().bind(BeerTO::getDescription, BeerTO::setDescription);
		descriptionField.setWidth("600px");
		descriptionField.setHeight("200px");
		layout.add(descriptionField);
		layout.setColspan(descriptionField, 6);

		return layout;
	}
}