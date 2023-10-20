package cz.gattserver.grass.language.web.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;

import cz.gattserver.grass.language.facades.LanguageFacade;
import cz.gattserver.grass.language.model.domain.ItemType;
import cz.gattserver.grass.language.model.dto.CrosswordCell;
import cz.gattserver.grass.language.model.dto.CrosswordHintTO;
import cz.gattserver.grass.language.model.dto.CrosswordTO;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;
import cz.gattserver.grass.language.web.CrosswordField;

public class CrosswordTab extends Div {

	private static final long serialVersionUID = 6332893829812704996L;

	@Autowired
	private LanguageFacade languageFacade;

	public CrosswordTab(Long langId) {
		SpringContextHelper.inject(this);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		btnLayout.setPadding(false);
		add(btnLayout);

		Map<Input, String> fieldMap = new HashMap<>();

		Button giveUpTestBtn = new Button("Vzdát to", event -> {
			for (Map.Entry<Input, String> entry : fieldMap.entrySet())
				entry.getKey().setValue(entry.getValue());
		});
		giveUpTestBtn.setIcon(new Image(ImageIcon.FLAG_16_ICON.createResource(), "giveup"));
		btnLayout.add(giveUpTestBtn);

		NumberField numberField = new NumberField();
		// TODO v24
		//numberField.setHasControls(true);
		numberField.setMin(5);
		numberField.setMax(30);
		btnLayout.add(numberField);

		VerticalLayout mainLayout = new VerticalLayout();
		add(mainLayout);

		Button newCrosswordBtn = new Button("",
				event -> generateNewCrossword(numberField.getValue().intValue(), langId, fieldMap, mainLayout));
		newCrosswordBtn.setIcon(new Image(ImageIcon.RIGHT_16_ICON.createResource(), "start"));
		btnLayout.add(newCrosswordBtn);

		numberField.addValueChangeListener(e -> newCrosswordBtn
				.setText("Nová křížovka " + e.getValue().intValue() + "x" + e.getValue().intValue()));

		numberField.setValue(15.0);
	}

	private void generateNewCrossword(int size, long langId, Map<Input, String> fieldMap, VerticalLayout mainLayout) {

		// clear
		fieldMap.clear();
		mainLayout.removeAll();

		LanguageItemTO filterTO = new LanguageItemTO();
		filterTO.setLanguage(langId);
		filterTO.setType(ItemType.WORD);

		CrosswordTO crosswordTO = languageFacade.prepareCrossword(filterTO, size);

		if (crosswordTO.getHints().isEmpty()) {
			mainLayout.add("Nezdařilo se sestavit křížovku");
			return;
		}

		List<CrosswordField> writeFields = new ArrayList<>();

		FormLayout hintsLayout = new FormLayout();
		hintsLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("100px", 4));
		hintsLayout.setWidthFull();
		for (CrosswordHintTO to : crosswordTO.getHints()) {
			hintsLayout.add(new Span(to.getId() + ". " + to.getHint()));
			CrosswordField tf = new CrosswordField(to);
			writeFields.add(tf);
			tf.setMaxLength(to.getWordLength());
			hintsLayout.add(tf);
		}

		Div crosswordLayout = constructCrossword(crosswordTO, writeFields, fieldMap);

		mainLayout.add(crosswordLayout);
		mainLayout.setHorizontalComponentAlignment(Alignment.CENTER, crosswordLayout);
		mainLayout.add(hintsLayout);
	}

	private Div constructCrossword(CrosswordTO crosswordTO, List<CrosswordField> writeFields,
			Map<Input, String> fieldMap) {
		Div crosswordLayout = new Div();

		for (int y = 0; y < crosswordTO.getHeight(); y++) {
			Div line = new Div();
			line.getStyle().set("display", "flex");
			crosswordLayout.add(line);
			for (int x = 0; x < crosswordTO.getWidth(); x++) {
				CrosswordCell cell = crosswordTO.getCell(x, y);
				if (cell != null) {
					Input t = new Input();
					t.addClassName("crossword-cell");
					t.setWidth("23px");
					t.setHeight("23px");
					t.getStyle().set("padding", "0").set("margin", "0").set("border", "1px solid #bbb")
							.set("text-align", "center");
					t.setEnabled(cell.isWriteAllowed());
					if (!cell.isWriteAllowed()) {
						t.setValue(cell.getValue());
					} else {
						t.getElement().setAttribute("maxlength", "1");
						connectField(t, cell, x, y, writeFields, fieldMap);
					}
					line.add(t);
				} else {
					Div spacer = new Div();
					spacer.setWidth("25px");
					spacer.setHeight("25px");
					spacer.getStyle().set("display", "inline-block");
					line.add(spacer);
				}
			}
		}
		return crosswordLayout;
	}

	private void connectField(Input t, CrosswordCell cell, int x, int y, List<CrosswordField> writeFields,
			Map<Input, String> fieldMap) {
		// logika pro zapipsování skrz postranní pole
		for (CrosswordField cf : writeFields)
			cf.tryRegisterCellField(t, x, y);

		// logika pro kontrolu správného výsledku
		fieldMap.put(t, cell.getValue());
		t.addValueChangeListener(e -> checkCrossword(fieldMap));
	}

	private void checkCrossword(Map<Input, String> fieldMap) {
		for (Map.Entry<Input, String> entry : fieldMap.entrySet()) {
			String is = entry.getKey().getValue();
			String shouldBe = entry.getValue();
			if (StringUtils.isNotBlank(shouldBe) && !shouldBe.equalsIgnoreCase(is)
					|| StringUtils.isBlank(shouldBe) && StringUtils.isNotBlank(is))
				return;
		}
		for (Input tf : fieldMap.keySet()) {
			tf.addClassName("crossword-done");
			tf.setEnabled(false);
		}
	}

}
