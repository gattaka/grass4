package cz.gattserver.grass.core.ui.dialogs;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;

import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.grass.core.interfaces.QuoteTO;

public class QuoteDialog extends EditWebDialog {

	private static final long serialVersionUID = -8494081277784752858L;

	public interface SaveAction {
		void onSave(QuoteTO quoteDTO);
	}

	public QuoteDialog(SaveAction saveAction) {
		this(null, saveAction);
	}

	public QuoteDialog(QuoteTO quote, SaveAction saveAction) {
        super("Hláška");
		init(quote, saveAction);
	}

	private void init(QuoteTO quote, SaveAction saveAction) {
		final int maxLength = 90;
		final TextArea newQuoteText = new TextArea();
		newQuoteText.setMaxLength(maxLength);

		final Binder<QuoteTO> binder = new Binder<>();
		binder.setBean(new QuoteTO());
		binder.forField(newQuoteText)
				.withValidator(new StringLengthValidator(
						"Text hlášky nesmí být prázdný a může mít maximálně " + maxLength + " znaků", 1, maxLength))
				.bind(QuoteTO::getName, QuoteTO::setName);
		newQuoteText.setWidth("400px");
		addComponent(newQuoteText);

		if (quote != null)
			binder.readBean(quote);

		addComponent(componentFactory.createDialogSubmitOrStornoLayout(e -> {
			QuoteTO targetTO = quote == null ? new QuoteTO() : quote;
			if (binder.writeBeanIfValid(targetTO)) {
				saveAction.onSave(targetTO);
				close();
			}
		}, e -> close()));
	}

}
