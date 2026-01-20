package cz.gattserver.grass.campgames.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import cz.gattserver.common.vaadin.dialogs.EditWebDialog;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.service.CampgamesService;
import cz.gattserver.grass.core.ui.components.button.CloseButton;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;

public abstract class CampgameCreateDialog extends EditWebDialog {

	private static final long serialVersionUID = -6773027334692911384L;

	private transient CampgamesService campgamesService;

	public CampgameCreateDialog(Long originalId) {
		init(originalId == null ? null : getCampgameService().getCampgame(originalId));
	}

	public CampgameCreateDialog() {
		init(null);
	}

	public CampgameCreateDialog(CampgameTO originalDTO) {
		init(originalDTO);
	}

	private CampgamesService getCampgameService() {
		if (campgamesService == null)
			campgamesService = SpringContextHelper.getBean(CampgamesService.class);
		return campgamesService;
	}

	/**
	 * @param originalDTO
	 *            opravuji údaje existující položky, nebo vytvářím novou (
	 *            {@code null}) ?
	 */
	private void init(CampgameTO originalDTO) {
		CampgameTO formDTO = new CampgameTO();
		formDTO.setName("");

		FormLayout winLayout = new FormLayout();
		layout.add(winLayout);
		winLayout.setWidth("600px");

		Binder<CampgameTO> binder = new Binder<>(CampgameTO.class);
		binder.setBean(formDTO);

		TextField nameField = new TextField("Název");
		nameField.setWidthFull();
		nameField.addClassName(UIUtils.TOP_CLEAN_CSS_CLASS);
		binder.forField(nameField).asRequired("Název položky je povinný").bind("name");
		winLayout.add(nameField, 2);

		TokenField keywords = new TokenField(getCampgameService().getAllCampgameKeywordNames());
		keywords.isEnabled();
		keywords.setAllowNewItems(true);
		keywords.getInputField().setPlaceholder("klíčové slovo");

		if (originalDTO != null)
			for (String keyword : originalDTO.getKeywords())
				keywords.addToken(keyword);
		winLayout.add(keywords, 2);

		TextField originField = new TextField("Původ hry");
		originField.setWidthFull();
		binder.forField(originField).bind("origin");
		winLayout.add(originField);

		TextField playersField = new TextField("Počet hráčů");
		playersField.setWidthFull();
		binder.forField(playersField).bind("players");
		winLayout.add(playersField);

		TextField playTimeField = new TextField("Délka hry");
		playTimeField.setWidthFull();
		binder.forField(playTimeField).bind("playTime");
		winLayout.add(playTimeField);

		TextField preparationTimeField = new TextField("Délka přípravy");
		preparationTimeField.setWidthFull();
		binder.forField(preparationTimeField).bind("preparationTime");
		winLayout.add(preparationTimeField);

		TextArea descriptionField = new TextArea("Popis");
		descriptionField.setHeight("300px");
		binder.forField(descriptionField).bind("description");
		winLayout.add(descriptionField, 2);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		buttonLayout.setSpacing(false);
		buttonLayout.setWidthFull();
		layout.add(buttonLayout);

		Button createBtn = componentFactory.createSaveButton(e -> {
			try {
				CampgameTO writeDTO = originalDTO == null ? new CampgameTO() : originalDTO;
				binder.writeBean(writeDTO);
				writeDTO.setKeywords(keywords.getValues());
				writeDTO.setId(getCampgameService().saveCampgame(writeDTO));
				onSuccess(writeDTO);
				close();
			} catch (Exception ve) {
				if (!(ve instanceof ValidationException))
					new ErrorDialog("Uložení se nezdařilo" + ve.getMessage()).open();
			}
		});
		buttonLayout.add(createBtn);

		CloseButton closeBtn = new CloseButton(e -> close());
		buttonLayout.add(closeBtn);

		if (originalDTO != null)
			binder.readBean(originalDTO);

		setCloseOnEsc(false);
	}

	protected abstract void onSuccess(CampgameTO dto);

}
