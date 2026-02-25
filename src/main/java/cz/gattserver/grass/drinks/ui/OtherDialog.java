package cz.gattserver.grass.drinks.ui;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import cz.gattserver.grass.drinks.model.interfaces.OtherTO;
import cz.gattserver.common.ui.RatingStars;
import cz.gattserver.grass.core.ui.util.UIUtils;

import java.io.Serial;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

public class OtherDialog extends DrinkDialog<OtherTO> {

    @Serial
    private static final long serialVersionUID = 4399783455948186639L;

    public OtherDialog(OtherTO to, Consumer<OtherTO> onSave) {
        super(to, onSave);
    }

    public OtherDialog(Consumer<OtherTO> onSave) {
        super(onSave);
    }

    @Override
    protected OtherTO createNewInstance() {
        OtherTO formTO = new OtherTO();
        formTO.setRating(2.5);
        return formTO;
    }

    @Override
    protected FormLayout createForm(Binder<OtherTO> binder) {
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("100px", 6));

        TextField wineryField = new TextField("Ingredience");
        binder.forField(wineryField).asRequired().bind(OtherTO::getIngredient, OtherTO::setIngredient);
        layout.add(wineryField);
        layout.setColspan(wineryField, 3);
        wineryField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

        TextField nameField = new TextField("Název");
        binder.forField(nameField).asRequired().bind(OtherTO::getName, OtherTO::setName);
        layout.add(nameField);
        layout.setColspan(nameField, 3);
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

        TextField countryField = new TextField("Země");
        binder.forField(countryField).asRequired().bind(OtherTO::getCountry, OtherTO::setCountry);
        layout.add(countryField);
        layout.setColspan(countryField, 4);

        RatingStars ratingStars = new RatingStars();
        binder.forField(ratingStars).asRequired().bind(OtherTO::getRating, OtherTO::setRating);
        layout.add(ratingStars);
        layout.setColspan(ratingStars, 2);

        TextField alcoholField = new TextField("Alkohol (%)");
        binder.forField(alcoholField).withNullRepresentation("")
                .withConverter(new StringToDoubleConverter(null, "Alkohol (%) musí být celé číslo") {

                    @Serial
                    private static final long serialVersionUID = -520043461673844302L;

                    @Override
                    protected NumberFormat getFormat(Locale locale) {
                        return NumberFormat.getNumberInstance(Locale.forLanguageTag("cs-CZ"));
                    }

                }).bind(OtherTO::getAlcohol, OtherTO::setAlcohol);
        layout.add(alcoholField);
        layout.setColspan(alcoholField, 2);

        TextArea descriptionField = new TextArea("Popis");
        binder.forField(descriptionField).asRequired().bind(OtherTO::getDescription, OtherTO::setDescription);
        descriptionField.setWidth(600, Unit.PIXELS);
        descriptionField.setHeight(200, Unit.PIXELS);
        layout.add(descriptionField);
        layout.setColspan(descriptionField, 6);

        return layout;
    }

}
