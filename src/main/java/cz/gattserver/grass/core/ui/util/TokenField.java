package cz.gattserver.grass.core.ui.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.vaadin.flow.data.provider.CallbackDataProvider;
import cz.gattserver.grass.core.ui.components.button.DeleteButton;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBox.FetchItemsCallback;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.function.SerializableFunction;

public class TokenField extends Div {

	private static final long serialVersionUID = -4556540987839489629L;

	private Map<String, Button> tokens = new HashMap<>();
	private Div tokensLayout;
	private boolean allowNewItems = true;

	private Consumer<String> addTokenListener;
	private Consumer<String> removeTokenListener;

	private ComboBox<String> comboBox;

	public TokenField(
			CallbackDataProvider.FetchCallback<String, String> fetchItemsCallback,
			CallbackDataProvider.CountCallback<String, String> countCallback) {
		comboBox = new ComboBox<>();
		comboBox.setItems(fetchItemsCallback, countCallback);
		comboBox.addAttachListener(e -> comboBox.focus());
		init();
	}

	public TokenField(Collection<String> values) {
		comboBox = new ComboBox<>(null, values);
		init();
	}

	private void init() {
		tokensLayout = new ButtonLayout();
		add(tokensLayout);

		comboBox.addCustomValueSetListener(e -> {
			if (allowNewItems)
				commitValue(e.getDetail());
		});
		comboBox.addValueChangeListener(e -> commitValue(e.getValue()));
		tokensLayout.add(comboBox);
	}

	public TokenField setPlaceholder(String placeholder) {
		comboBox.setPlaceholder(placeholder);
		return this;
	}

	private void commitValue(String value) {
		if (StringUtils.isNotBlank(value)) {
			value = value.trim();
			if (!tokens.containsKey(value)) {
				addToken(value);
				// tohle funguje i u custom value, narozdíl od clear(),
				// které dělá nastavení na null, což value u custom-value
				// stále je, takže se pole nevyčistí, protože nedošlo ke změně
				// hodnot (null -> null)
				comboBox.setValue("");
			}
		}
	}

	public void addToken(String token) {
		if (!tokens.containsKey(token)) {
			Button tokenComponent = new DeleteButton(token, e -> deleteToken(token));
			tokens.put(token, tokenComponent);
			tokensLayout.add(tokenComponent);
			tokensLayout.remove(comboBox);
			tokensLayout.add(comboBox);
			comboBox.focus();
			if (addTokenListener != null)
				addTokenListener.accept(token);
		}
	}

	public void deleteToken(String token) {
		Button tokenComponent = tokens.get(token);
		if (tokenComponent != null) {
			tokensLayout.remove(tokenComponent);
			tokens.remove(token);
			if (removeTokenListener != null)
				removeTokenListener.accept(token);
		}
	}

	public TokenField setValues(Collection<String> tokens) {
		for (String s : tokens)
			addToken(s);
		return this;
	}

	public Set<String> getValues() {
		return new HashSet<>(tokens.keySet());
	}

	public void setAllowNewItems(boolean allowNewItems) {
		this.allowNewItems = allowNewItems;
	}

	public boolean isAllowNewItems() {
		return allowNewItems;
	}

	public ComboBox<String> getInputField() {
		return comboBox;
	}

	public void addTokenAddListener(Consumer<String> addTokenListener) {
		this.addTokenListener = addTokenListener;
	}

	public void addTokenRemoveListener(Consumer<String> removeTokenListener) {
		this.removeTokenListener = removeTokenListener;
	}

}
