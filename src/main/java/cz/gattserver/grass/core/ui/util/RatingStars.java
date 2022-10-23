package cz.gattserver.grass.core.ui.util;

import java.util.Objects;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.shared.Registration;

public class RatingStars extends Div implements SingleSelect<RatingStars, Double> {

	private static final long serialVersionUID = -5094708366894280218L;

	private Double value;
	private HoverIcon[] icons = new HoverIcon[5];
	private DomListenerRegistration[] mouseOverListeners = new DomListenerRegistration[5];
	private DomListenerRegistration[] mouseOutListeners = new DomListenerRegistration[5];
	private Registration[] clickListeners = new Registration[5];
	private boolean readOnly = false;
	private boolean required = false;
	private ValueChangeListener<? super ComponentValueChangeEvent<RatingStars, Double>> valueChangeListener;

	private void showCurrentValue() {
		for (int j = 0; j < icons.length; j++)
			icons[j].changeIcon(value != null && j < value ? VaadinIcon.STAR : VaadinIcon.STAR_O);
	}

	private void changeValue(Double newValue, boolean userOriginated) {
		if (!Objects.equals(newValue, this.value)) {
			if (valueChangeListener != null)
				valueChangeListener.valueChanged(
						new ComponentValueChangeEvent<RatingStars, Double>(this, this, this.value, userOriginated));
			this.value = newValue;
			showCurrentValue();
		}
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		changeValue(value, false);
	}

	public void setSize(String size) {
		for (HoverIcon icon : icons)
			icon.setSize(size);
	}

	public RatingStars() {
		for (int i = 0; i < icons.length; i++) {
			HoverIcon icon = new HoverIcon(VaadinIcon.STAR_O);
			add(icon);
			icons[i] = icon;
		}
		setReadOnly(false);
		value = 0d;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		if (readOnly) {
			for (DomListenerRegistration reg : mouseOverListeners)
				reg.remove();
			for (DomListenerRegistration reg : mouseOutListeners)
				reg.remove();
			for (Registration reg : clickListeners)
				reg.remove();
			for (HoverIcon icon : icons)
				icon.getElement().getStyle().remove("cursor");
		} else {
			for (int i = 0; i < icons.length; i++) {
				HoverIcon icon = icons[i];
				int iconId = i;
				icon.getElement().getStyle().set("cursor", "pointer");
				mouseOverListeners[i] = icon.getElement().addEventListener("mouseover", e -> {
					for (int j = 0; j < icons.length; j++) {
						icons[j].changeIcon(j <= iconId ? VaadinIcon.STAR : VaadinIcon.STAR_O);
					}
				});
				mouseOutListeners[i] = icon.getElement().addEventListener("mouseout", e -> showCurrentValue());
				clickListeners[i] = icon.addClickListener(e -> changeValue(iconId + 1.0, true));
			}
		}
	}

	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
		this.required = requiredIndicatorVisible;
	}

	@Override
	public boolean isRequiredIndicatorVisible() {
		return required;
	}

	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<RatingStars, Double>> listener) {
		valueChangeListener = listener;
		return () -> valueChangeListener = null;
	}

}
