package cz.gattserver.grass.hw.ui.dialogs;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import cz.gattserver.common.FieldUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.exception.GrassException;
import cz.gattserver.grass.core.ui.components.SaveCloseLayout;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import cz.gattserver.grass.hw.interfaces.HWItemState;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass.hw.service.HWService;
import cz.gattserver.grass.hw.ui.UsedInChooser;
import org.springframework.beans.factory.annotation.Autowired;

public class HWItemEditDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	@Autowired
	private HWService hwService;

	private Consumer<HWItemTO> onSuccessConsumer;

	public HWItemEditDialog(Long originalId, Consumer<HWItemTO> onSuccessConsumer) {
		init(originalId == null ? null : hwService.getHWItem(originalId), onSuccessConsumer);
	}

	public HWItemEditDialog(HWItemTO originalTO, Consumer<HWItemTO> onSuccessConsumer) {
		init(originalTO, onSuccessConsumer);
	}

	/**
	 * @param originalTO opravuji údaje existující položky, nebo vytvářím novou (
	 *                   {@code null}) ?
	 */
	private void init(HWItemTO originalTO, Consumer<HWItemTO> onSuccessConsumer) {
		SpringContextHelper.inject(this);
		setWidth("900px");

		HWItemTO formTO = new HWItemTO();
		formTO.setName("");
		formTO.setPrice(new BigDecimal(0));
		formTO.setWarrantyYears(0);
		formTO.setState(HWItemState.NEW);
		formTO.setPurchaseDate(LocalDate.now());
		if (originalTO != null) {
			formTO.setUsedIn(originalTO.getUsedIn());
			formTO.setUsedInName(originalTO.getUsedInName());
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

		TextField priceField = new TextField("Cena");
		priceField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
		priceField.setWidth("100px");
		binder.forField(priceField).withNullRepresentation("").withConverter(toModel -> {
			try {
				if (StringUtils.isBlank(toModel))
					return null;
				DecimalFormat df = new DecimalFormat();
				df.setParseBigDecimal(true);
				return (BigDecimal) df.parse(toModel);
			} catch (ParseException e1) {
				throw new IllegalArgumentException();
			}
		}, FieldUtils::formatMoney, "Cena musí být číslo").bind("price");
		baseLayout.add(priceField);

		ComboBox<HWItemState> stateComboBox = new ComboBox<>("Stav", Arrays.asList(HWItemState.values()));
		stateComboBox.setWidth("150px");
		stateComboBox.setItemLabelGenerator(HWItemState::getName);
		binder.forField(stateComboBox).asRequired("Stav položky je povinný").bind(HWItemTO::getState,
				HWItemTO::setState);
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
			formTO.setUsedIn(to);
			formTO.setUsedInName(to == null ? null : to.getName());
		}));

		TextArea descriptionArea = new TextArea("Popis");
		descriptionArea.setTabIndex(-1);
		descriptionArea.setWidthFull();
		descriptionArea.getStyle().set("font-family", "monospace").set("tab-size", "4").set("font-size", "12px");
		binder.bind(descriptionArea, HWItemTO::getDescription, HWItemTO::setDescription);
		add(descriptionArea);
		descriptionArea.setHeight("300px");

		Map<String, HWItemTypeTO> tokens = new HashMap<>();
		hwService.getAllHWTypes().forEach(to -> tokens.put(to.getName(), to));

		TokenField keywords = new TokenField(tokens.keySet());
		keywords.setAllowNewItems(true);
		keywords.getInputField().setPlaceholder("klíčové slovo");

		if (originalTO != null)
			keywords.setValues(originalTO.getTypes());
		add(keywords);

		SaveCloseLayout buttons = new SaveCloseLayout(e -> {
			try {
				HWItemTO writeTO = originalTO == null ? new HWItemTO() : originalTO;
				binder.writeBean(writeTO);
				writeTO.setUsedIn(binder.getBean().getUsedIn());
				writeTO.setUsedInName(binder.getBean().getUsedInName());
				writeTO.setTypes(keywords.getValues());
				writeTO.setId(hwService.saveHWItem(writeTO));
				onSuccessConsumer.accept(writeTO);
				close();
			} catch (Exception ve) {
				throw new GrassException("Uložení se nezdařilo", ve);
			}
		}, e -> close());

		add(buttons);

		if (originalTO != null)
			binder.readBean(originalTO);
	}
}