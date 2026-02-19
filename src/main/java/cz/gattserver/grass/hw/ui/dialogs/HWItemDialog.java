package cz.gattserver.grass.hw.ui.dialogs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemState;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWTypeBasicTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.UsedInChooser;

public class HWItemDialog extends EditWebDialog {

    private final HWService hwService;

    public HWItemDialog(HWItemTO originalTO, Consumer<HWItemTO> onSave) {
        super("Záznam");
        this.hwService = SpringContextHelper.getBean(HWService.class);

        layout.setSizeFull();
        setWidth(900, Unit.PIXELS);
        setHeight(700, Unit.PIXELS);
        setResizable(true);

        HWItemTO formTO = originalTO == null ? new HWItemTO() : originalTO.copy();
        if (originalTO == null) {
            formTO.setName("");
            formTO.setPrice(new BigDecimal(0));
            formTO.setWarrantyYears(0);
            formTO.setState(HWItemState.NEW);
            formTO.setPurchaseDate(LocalDate.now());
        }

        Binder<HWItemTO> binder = new Binder<>(HWItemTO.class);
        binder.setBean(formTO);

        TextField nameField = new TextField("Název");
        nameField.setWidthFull();
        nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
        binder.forField(nameField).asRequired("Název položky je povinný").bind(HWItemTO::getName, HWItemTO::setName);
        layout.add(nameField);

        FormLayout topLayout = new FormLayout();
        topLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0px", 6));
        layout.add(topLayout);

        DatePicker purchaseDateField = componentFactory.createDatePicker("Získáno");
        purchaseDateField.setWidth(130, Unit.PIXELS);
        binder.bind(purchaseDateField, HWItemTO::getPurchaseDate, HWItemTO::setPurchaseDate);
        topLayout.add(purchaseDateField);

        BigDecimalField priceField = new BigDecimalField("Cena");
        priceField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        priceField.setWidth(100, Unit.PIXELS);
        priceField.setLocale(new Locale("cs", "CZ"));
        binder.forField(priceField).withNullRepresentation(BigDecimal.ZERO)
                .bind(HWItemTO::getPrice, HWItemTO::setPrice);
        topLayout.add(priceField);

        ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
        stateComboBox.setWidth(150, Unit.PIXELS);
        stateComboBox.setItemLabelGenerator(HWItemState::getName);
        binder.forField(stateComboBox).asRequired("Stav položky je povinný")
                .bind(HWItemTO::getState, HWItemTO::setState);
        topLayout.add(stateComboBox);

        TextField warrantyYearsField = new TextField("Záruka (roky)");
        binder.forField(warrantyYearsField).withNullRepresentation("")
                .withConverter(new StringToIntegerConverter(null, "Záruka musí být celé číslo"))
                .bind(HWItemTO::getWarrantyYears, HWItemTO::setWarrantyYears);
        warrantyYearsField.setWidth(100, Unit.PIXELS);
        topLayout.add(warrantyYearsField);

        TextField supervizedForField = new TextField("Spravováno pro");
        supervizedForField.setWidthFull();
        binder.bind(supervizedForField, HWItemTO::getSupervizedFor, HWItemTO::setSupervizedFor);
        topLayout.add(supervizedForField);

        Checkbox publicItemCheckBox = new Checkbox("Veřejné");
        binder.bind(publicItemCheckBox, HWItemTO::getPublicItem, HWItemTO::setPublicItem);
        topLayout.add(publicItemCheckBox);
        topLayout.setWidthFull();

        UsedInChooser usedInChooser = new UsedInChooser(formTO.getId());
        binder.bind(usedInChooser,
                to -> to.getUsedInId() == null ? null : new HWItemOverviewTO(to.getUsedInId(), to.getUsedInName()),
                (to, val) -> {
                    if (val == null) {
                        to.setUsedInId(null);
                        to.setUsedInName(null);
                    } else {
                        to.setUsedInId(val.getId());
                        to.setUsedInName(val.getName());
                    }
                });
        usedInChooser.setWidthFull();
        layout.add(usedInChooser);

        TextArea descriptionArea = componentFactory.createTextArea("Popis");
        binder.bind(descriptionArea, HWItemTO::getDescription, HWItemTO::setDescription);
        layout.add(descriptionArea);
        descriptionArea.setSizeFull();
        descriptionArea.setMinHeight(300, Unit.PIXELS);

        Map<String, HWTypeBasicTO> typeNameMap = new HashMap<>();
        for (HWTypeBasicTO to : hwService.findAllHWTypes())
            typeNameMap.put(to.getName(), to);

        TokenField tokenField = new TokenField("Klíčová slova", typeNameMap.keySet());
        tokenField.setAllowNewItems(true);
        binder.forField(tokenField)
                .bind(to -> to.getTypes().stream().map(HWTypeBasicTO::getName).collect(Collectors.toSet()),
                        (to, val) -> to.setTypes(val.stream().map(name -> {
                            HWTypeBasicTO typeTO = typeNameMap.get(name);
                            if (typeTO != null) return typeTO;
                            return new HWTypeBasicTO(name);
                        }).collect(Collectors.toSet())));
        layout.add(tokenField);

        getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            try {
                binder.writeBean(formTO);
                onSave.accept(formTO);
                close();
            } catch (ValidationException ve) {
                // Chyby z validátoru se zobrazují rovnou v UI
            }
        }, e -> close(), true));

        if (originalTO != null) binder.readBean(originalTO);
    }
}