package cz.gattserver.grass.campgames.ui.dialogs;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import cz.gattserver.grass.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass.campgames.service.CampgamesService;
import cz.gattserver.grass.core.ui.components.button.CloseButton;
import cz.gattserver.grass.core.ui.components.button.SaveButton;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.dialogs.EditWebDialog;
import cz.gattserver.web.common.ui.dialogs.ErrorDialog;

public abstract class CampgameKeywordDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient CampgamesService campgamesService;

	public CampgameKeywordDialog(CampgameKeywordTO originalDTO) {
		super("Úprava klíčového slova");
		init(originalDTO);
	}

	public CampgameKeywordDialog() {
		super("Nové klíčové slovo");
		init(null);
	}

	private CampgamesService getCampgamesService() {
		if (campgamesService == null)
			campgamesService = SpringContextHelper.getBean(CampgamesService.class);
		return campgamesService;
	}

	public void init(CampgameKeywordTO originalDTO) {
		VerticalLayout winLayout = new VerticalLayout();
		winLayout.setSpacing(true);
		winLayout.setPadding(false);
		winLayout.setMinWidth("200px");

		CampgameKeywordTO formDTO = new CampgameKeywordTO();
		formDTO.setName("");
		Binder<CampgameKeywordTO> binder = new Binder<>(CampgameKeywordTO.class);
		binder.setBean(formDTO);

		final TextField nameField = new TextField();
		nameField.setSizeFull();
		binder.bind(nameField, "name");
		winLayout.add(nameField);

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		btnLayout.setSpacing(false);
		btnLayout.setSizeFull();
		winLayout.add(btnLayout);

		btnLayout.add(new SaveButton(e -> {
			try {
				CampgameKeywordTO writeDTO = originalDTO == null ? new CampgameKeywordTO() : originalDTO;
				binder.writeBean(writeDTO);
				getCampgamesService().saveCampgameKeyword(writeDTO);
				onSuccess(writeDTO);
				close();
			} catch (ValidationException ex) {
				new ErrorDialog("Chybná vstupní data\n\n   " + ex.getBeanValidationErrors().iterator().next()).open();
			} catch (Exception ex) {
				new ErrorDialog("Uložení se nezdařilo").open();
			}
		}));

		btnLayout.add(new CloseButton(e -> close()));

		if (originalDTO != null)
			binder.readBean(originalDTO);

		add(winLayout);
	}

	protected abstract void onSuccess(CampgameKeywordTO campgameKeywordTO);

}
