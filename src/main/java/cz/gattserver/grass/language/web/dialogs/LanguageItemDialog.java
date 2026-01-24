package cz.gattserver.grass.language.web.dialogs;

import java.util.Arrays;

import com.vaadin.flow.component.html.Div;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.renderer.TextRenderer;

import cz.gattserver.grass.language.facades.LanguageFacade;
import cz.gattserver.grass.language.model.domain.ItemType;

public class LanguageItemDialog extends EditWebDialog {

    private static final long serialVersionUID = 6803519662032576371L;

    @Autowired
    private LanguageFacade languageFacade;

    public interface SaveAction {
        void onSave(LanguageItemTO itemTO);
    }

    public LanguageItemDialog(SaveAction action, Long langId, ItemType asType) {
        this(null, action, langId, asType);
    }

    public LanguageItemDialog(final LanguageItemTO to, SaveAction action, Long langId, ItemType asType) {
        super("Záznam");
        SpringContextHelper.inject(this);

        setWidth("600px");

        if (asType == null) asType = ItemType.values()[0];

        LanguageItemTO targetTO;
        if (to == null) {
            targetTO = new LanguageItemTO();
            targetTO.setType(asType);
        } else {
            targetTO = to;
        }

        Binder<LanguageItemTO> binder = new Binder<>();

        RadioButtonGroup<ItemType> typeRadio = new RadioButtonGroup<>();
        typeRadio.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        typeRadio.setItems(Arrays.asList(ItemType.values()));
        typeRadio.setRenderer(new TextRenderer<>(ItemType::getCaption));
        binder.forField(typeRadio).bind(LanguageItemTO::getType, LanguageItemTO::setType);
        add(typeRadio);

        TextField contentField = new TextField("Obsah");
        contentField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);

        TextField translationField = new TextField("Překlad");

        typeRadio.addValueChangeListener(e -> contentField.focus());

        Validator<String> validator = (value, context) -> {
            LanguageItemTO itemTO = languageFacade.getLanguageItemByContent(langId, value);
            if (itemTO != null && itemTO.getContent().equals(value) &&
                    (to == null || !itemTO.getId().equals(to.getId()))) {
                translationField.setPlaceholder(itemTO.getTranslation());
                return ValidationResult.error("Položka již existuje");
            } else {
                translationField.setPlaceholder("");
                return ValidationResult.ok();
            }
        };

        contentField.setWidthFull();
        binder.forField(contentField).asRequired().withValidator(validator)
                .bind(LanguageItemTO::getContent, LanguageItemTO::setContent);
        add(contentField);
        contentField.focus();

        translationField.setWidthFull();
        binder.forField(translationField).asRequired()
                .bind(LanguageItemTO::getTranslation, LanguageItemTO::setTranslation);
        add(translationField);

        Shortcuts.addShortcutListener(this, () -> onSave(action, binder, targetTO), Key.ENTER);

        binder.readBean(targetTO);

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        btnLayout.setSpacing(false);
        btnLayout.setPadding(false);
        btnLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        add(btnLayout);

        if (to != null) {
            btnLayout.add(componentFactory.createEditButton(e -> onSave(action, binder, targetTO)));
        } else {
            Div buttonLayout = componentFactory.createButtonLayout();
            btnLayout.add(buttonLayout);
            buttonLayout.add(componentFactory.createCreateButton(e -> onSave(action, binder, targetTO)));
            Button createAndContinueBtn = componentFactory.createCreateButton("Vytvořit a pokračovat",
                    e -> onSaveAndContinue(action, binder, targetTO, langId, typeRadio.getValue()));
            buttonLayout.add(createAndContinueBtn);
        }

        btnLayout.add(componentFactory.createStornoButton(e -> close()));
    }

    private void onSave(SaveAction action, Binder<LanguageItemTO> binder, LanguageItemTO targetTO) {
        if (binder.writeBeanIfValid(targetTO)) {
            checkAndThen(targetTO, () -> {
                action.onSave(targetTO);
                close();
            });
        }
    }

    private void onSaveAndContinue(SaveAction action, Binder<LanguageItemTO> binder, LanguageItemTO targetTO,
                                   Long langId, ItemType asType) {
        if (binder.writeBeanIfValid(targetTO)) {
            checkAndThen(targetTO, () -> {
                action.onSave(targetTO);
                new LanguageItemDialog(action, langId, asType).open();
                close();
            });
        }
    }

    private void checkAndThen(LanguageItemTO targetTO, Runnable r) {
        if (targetTO.getContent().split(" ").length > 2 && ItemType.WORD.equals(targetTO.getType())) {
            new ConfirmDialog("Opravdu uložit slovní spojení jako '" + ItemType.WORD.getCaption() + "' ?", e -> {
                r.run();
            }).open();
        } else if (targetTO.getContent().split(" ").length == 1 && ItemType.PHRASE.equals(targetTO.getType())) {
            new ConfirmDialog("Opravdu uložit jedno slovo '" + ItemType.PHRASE.getCaption() + "' ?", e -> {
                r.run();
            }).open();
        } else {
            r.run();
        }
    }

}
