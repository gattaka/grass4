package cz.gattserver.grass.language.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import cz.gattserver.grass.language.model.dto.CrosswordHintTO;

public class CrosswordField extends TextField {

	private CrosswordHintTO hintTO;
	private List<Input> cellsFields;

	public CrosswordField(CrosswordHintTO hintTO) {
		this.hintTO = hintTO;
		cellsFields = new ArrayList<>(Arrays.asList(new Input[hintTO.getWordLength()]));
		setValueChangeMode(ValueChangeMode.EAGER);
		addValueChangeListener(e -> {
			String value = e.getValue();
			for (int i = 0; i < cellsFields.size(); i++) {
				Input tf = cellsFields.get(i);
				if (value.length() > i)
					tf.setValue(String.valueOf(value.charAt(i)));
				else
					tf.setValue("");
			}
		});
	}

	public CrosswordHintTO getHintTO() {
		return hintTO;
	}

	public void tryRegisterCellField(Input cellField, int x, int y) {
		if (hintTO.isHorizontally() && y == hintTO.getFromY() && x >= hintTO.getFromX() && x <= hintTO.getToX())
			cellsFields.set(x - hintTO.getFromX(), cellField);

		if (!hintTO.isHorizontally() && x == hintTO.getFromX() && y >= hintTO.getFromY() && y <= hintTO.getToY())
			cellsFields.set(y - hintTO.getFromY(), cellField);
	}

}
