package cz.gattserver.grass.hw.ui.dialogs;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

public abstract class HWItemEditDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	@Autowired
	private HWService hwService;

	public HWItemEditDialog(Long originalId) {
		init(originalId == null ? null : hwService.getHWItem(originalId));
	}

	public HWItemEditDialog() {
		init(null);
	}

	public HWItemEditDialog(HWItemTO originalDTO) {
		init(originalDTO);
	}

	/**
	 * @param originalTO opravuji údaje existující položky, nebo vytvářím novou (
	 *                   {@code null}) ?
	 */
	private void init(HWItemTO originalTO) {
		SpringContextHelper.inject(this);
		setWidth("900px");

		HWItemTO formDTO = new HWItemTO();
		formDTO.setName("");
		formDTO.setPrice(new BigDecimal(0));
		formDTO.setWarrantyYears(0);
		formDTO.setState(HWItemState.NEW);
		formDTO.setPurchaseDate(LocalDate.now());
		if (originalTO != null) {
			formDTO.setUsedIn(originalTO.getUsedIn());
			formDTO.setUsedInName(originalTO.getUsedInName());
		}

		Binder<HWItemTO> binder = new Binder<>(HWItemTO.class);
		binder.setBean(formDTO);

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
			formDTO.setUsedIn(to);
			formDTO.setUsedInName(to == null ? null : to.getName());
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
				HWItemTO writeDTO = originalTO == null ? new HWItemTO() : originalTO;
				binder.writeBean(writeDTO);
				writeDTO.setUsedIn(binder.getBean().getUsedIn());
				writeDTO.setUsedInName(binder.getBean().getUsedInName());
				writeDTO.setTypes(keywords.getValues());
				writeDTO.setId(hwService.saveHWItem(writeDTO));
				onSuccess(writeDTO);
				close();
			} catch (Exception ve) {
				throw new GrassException("Uložení se nezdařilo", ve);
			}
		}, e -> close());

		add(buttons);

		if (originalTO != null)
			binder.readBean(originalTO);
	}

	protected abstract void onSuccess(HWItemTO dto);

}
