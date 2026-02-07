package cz.gattserver.grass.hw.ui.dialogs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.ValidationException;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.exception.GrassException;
import cz.gattserver.grass.core.ui.util.TokenField2;
import cz.gattserver.grass.core.ui.util.UIUtils;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import cz.gattserver.grass.hw.interfaces.HWItemState;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWTypeBasicTO;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.UsedInChooser;

public class HWItemEditDialog extends EditWebDialog {

    private static final long serialVersionUID = -6773027334692911384L;

    private final HWService hwService;

    public HWItemEditDialog(HWItemTO originalTO, Consumer<HWItemTO> onSave) {
        super("Záznam");
        this.hwService = SpringContextHelper.getBean(HWService.class);

        setWidth("900px");

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
        add(nameField);

        HorizontalLayout baseLayout = new HorizontalLayout();
        baseLayout.setPadding(false);
        add(baseLayout);

        DatePicker purchaseDateField = new DatePicker("Získáno");
        purchaseDateField.setLocale(Locale.forLanguageTag("CS"));
        purchaseDateField.setWidth("130px");
        binder.bind(purchaseDateField, HWItemTO::getPurchaseDate, HWItemTO::setPurchaseDate);
        baseLayout.add(purchaseDateField);

        BigDecimalField priceField = new BigDecimalField("Cena");
        priceField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        priceField.setWidth("100px");
        priceField.setLocale(new Locale("cs", "CZ"));
        binder.forField(priceField).withNullRepresentation(BigDecimal.ZERO)
                .bind(HWItemTO::getPrice, HWItemTO::setPrice);
        baseLayout.add(priceField);

        ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
        stateComboBox.setWidth("150px");
        stateComboBox.setItemLabelGenerator(HWItemState::getName);
        binder.forField(stateComboBox).asRequired("Stav položky je povinný")
                .bind(HWItemTO::getState, HWItemTO::setState);
        baseLayout.add(stateComboBox);

        TextField warrantyYearsField = new TextField("Záruka (roky)");
        binder.forField(warrantyYearsField).withNullRepresentation("")
                .withConverter(new StringToIntegerConverter(null, "Záruka musí být celé číslo"))
                .bind(HWItemTO::getWarrantyYears, HWItemTO::setWarrantyYears);
        warrantyYearsField.setWidth("100px");
        baseLayout.add(warrantyYearsField);

        TextField supervizedForField = new TextField("Spravováno pro");
        supervizedForField.setWidthFull();
        binder.bind(supervizedForField, HWItemTO::getSupervizedFor, HWItemTO::setSupervizedFor);
        baseLayout.add(supervizedForField);

        Checkbox publicItemCheckBox = new Checkbox("Veřejné");
        binder.bind(publicItemCheckBox, HWItemTO::getPublicItem, HWItemTO::setPublicItem);
        baseLayout.add(publicItemCheckBox);
        baseLayout.setWidth(null);
        baseLayout.setVerticalComponentAlignment(Alignment.END, publicItemCheckBox);

        add(new UsedInChooser(originalTO, to -> {
            if (to != null) {
                formTO.setUsedInId(to.getId());
                formTO.setUsedInName(to.getName());
            }
        }));

        TextArea descriptionArea = new TextArea("Popis");
        descriptionArea.setTabIndex(-1);
        descriptionArea.setWidthFull();
        descriptionArea.getStyle().set("font-family", "monospace").set("tab-size", "4").set("font-size", "12px");
        binder.bind(descriptionArea, HWItemTO::getDescription, HWItemTO::setDescription);
        add(descriptionArea);
        descriptionArea.setHeight("300px");

        Map<String, HWTypeBasicTO> typeNameMap = new HashMap<>();
        for (HWTypeBasicTO to : hwService.getAllHWTypes())
            typeNameMap.put(to.getName(), to);

        TokenField2 tokenField = new TokenField2("Klíčová slova", typeNameMap.keySet());
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
                HWItemTO writeTO = originalTO == null ? new HWItemTO() : originalTO;
                try {
                    binder.writeBean(writeTO);
                } catch (ValidationException ve) {
                    UIUtils.showError("V údajích jsou chyby");
                    return;
                }
                // TODO
//                writeTO.setUsedIn(binder.getBean().getUsedIn());
//                writeTO.setUsedInName(binder.getBean().getUsedInName());
                onSave.accept(writeTO);
                close();
            } catch (Exception ve) {
                throw new GrassException("Uložení se nezdařilo", ve);
            }
        }, e -> close()));

        if (originalTO != null) binder.readBean(originalTO);
    }
}