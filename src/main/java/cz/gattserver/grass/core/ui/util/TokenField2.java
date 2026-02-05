package cz.gattserver.grass.core.ui.util;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import cz.gattserver.common.Identifiable;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class TokenField2 extends CustomField<Set<String>> {

    private static final long serialVersionUID = -4556540987839489629L;

    private Map<String, Button> tokens = new HashMap<>();
    private Div tokensLayout;
    private boolean allowNewItems = true;

    private Consumer<String> addTokenListener;
    private Consumer<String> removeTokenListener;

    private Div chooseElementsDiv;
    private ComboBox<String> comboBox;

    public TokenField2(String label, Collection<String> items) {
        setLabel(label);
        ComponentFactory componentFactory = new ComponentFactory();
        tokensLayout = componentFactory.createButtonLayout(false);
        add(tokensLayout);

        chooseElementsDiv = new Div();
        chooseElementsDiv.addClassName(UIUtils.FLEX_DIV_CLASS);
        tokensLayout.add(chooseElementsDiv);

        comboBox = new ComboBox<>(null, items);
        comboBox.addCustomValueSetListener(e -> {
            if (allowNewItems) commitValue(e.getDetail());
        });
        comboBox.addValueChangeListener(e -> commitValue(e.getValue()));
        tokensLayout.add(comboBox);
    }

    @Override
    protected Set<String> generateModelValue() {
        return tokens.keySet();
    }

    @Override
    protected void setPresentationValue(Set<String> value) {
        tokens.clear();
        chooseElementsDiv.removeAll();
        chooseElementsDiv.setVisible(false);
        addTokens(value);
    }

    public TokenField2 setPlaceholder(String placeholder) {
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

    public void addTokens(Collection<String> tokens) {
        if (tokens == null) return;
        for (String token : tokens)
            addToken(token);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        setPresentationValue(getValue());
        comboBox.setVisible(!readOnly);
    }

    public void addToken(String token) {
        if (!tokens.containsKey(token)) {
            if (isReadOnly()) {
                Div div = new Div(token);
                div.addClassName("token-field-item");
                chooseElementsDiv.add(div);
            } else {
                Button tokenButton = new Button(token, e -> deleteToken(token));
                tokenButton.setIcon(VaadinIcon.CLOSE.create());
                tokens.put(token, tokenButton);
                chooseElementsDiv.add(tokenButton);
            }
            chooseElementsDiv.setVisible(true);
            comboBox.focus();
            if (addTokenListener != null) addTokenListener.accept(token);
        }
    }

    public void deleteToken(String token) {
        Button tokenComponent = tokens.get(token);
        if (tokenComponent != null) {
            chooseElementsDiv.remove(tokenComponent);
            tokens.remove(token);
            chooseElementsDiv.setVisible(tokens.size() > 0);
            if (removeTokenListener != null) removeTokenListener.accept(token);
        }
    }

    public TokenField2 setValues(Collection<String> tokens) {
        for (String s : tokens)
            addToken(s);
        return this;
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

    public Div getChooseElementsDiv() {
        return chooseElementsDiv;
    }

    public void addTokenAddListener(Consumer<String> addTokenListener) {
        this.addTokenListener = addTokenListener;
    }

    public void addTokenRemoveListener(Consumer<String> removeTokenListener) {
        this.removeTokenListener = removeTokenListener;
    }
}