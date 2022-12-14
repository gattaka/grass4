package cz.gattserver.grass.print3d.ui.pages;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox.FetchItemsCallback;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.common.vaadin.Breakline;
import cz.gattserver.common.vaadin.LinkButton;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.components.button.CloseButton;
import cz.gattserver.grass.core.ui.components.button.SaveButton;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.print3d.interfaces.Print3dPayloadTO;
import cz.gattserver.grass.print3d.interfaces.Print3dTO;
import cz.gattserver.grass.print3d.interfaces.Print3dViewItemTO;
import cz.gattserver.grass.print3d.service.Print3dService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Route("print3d-editor")
@PageTitle("Editor 3D projektu")
public class Print3dEditorPage extends OneColumnPage implements HasUrlParameter<String> {

	private static final long serialVersionUID = 8685208356478891386L;

	private static final Logger logger = LoggerFactory.getLogger(Print3dEditorPage.class);

	private static final String CLOSE_JS_DIV_ID = "close-js-div";

	@Autowired
	private Print3dService print3dService;

	@Autowired
	private ContentTagService contentTagFacade;

	@Resource(name = "print3dViewerPageFactory")
	private PageFactory print3dViewerPageFactory;

	@Autowired
	private EventBus eventBus;

	private NodeOverviewTO node;
	private Print3dTO project;

	private TokenField keywords;
	private TextField nameField;
	private Checkbox publicatedCheckBox;

	private String projectDir;
	private boolean editMode;
	private boolean stayInEditor = false;

	/**
	 * Soubory, kter?? byly nahr??ny od posledn??ho ulo??en??. V p????pad??, ??e budou
	 * ??pravy zru??eny, je pot??eba tyto soubory smazat.
	 */
	private Set<Print3dViewItemTO> newFiles = new HashSet<>();

	private String operationToken;
	private String identifierToken;

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		String[] chunks = parameter.split("/");
		if (chunks.length > 0)
			operationToken = chunks[0];
		if (chunks.length > 1)
			identifierToken = chunks[1];

		init();

		UI.getCurrent().getPage().executeJs(
				"window.onbeforeunload = function() { return \"Opravdu si p??ejete ukon??it editor a odej??t - rozpracovan?? data nejsou ulo??ena ?\" };");
	}

	@Override
	protected void createColumnContent(Div editorLayout) {
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null) {
			logger.debug("Nezda??ilo se vyt????it URL identifik??tor z ??et??zce: '{}'", identifierToken);
			throw new GrassPageException(404);
		}

		FetchItemsCallback<String> fetchItemsCallback = (filter, offset, limit) -> contentTagFacade
				.findByFilter(filter, offset, limit).stream();
		SerializableFunction<String, Integer> serializableFunction = filter -> contentTagFacade.countByFilter(filter);
		keywords = new TokenField(fetchItemsCallback, serializableFunction);

		nameField = new TextField();
		publicatedCheckBox = new Checkbox();

		// operace ?
		if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
			editMode = false;
			node = nodeFacade.getNodeByIdForOverview(identifier.getId());
			nameField.setValue("");
			publicatedCheckBox.setValue(true);
		} else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
			editMode = true;
			project = print3dService.getProjectForDetail(identifier.getId());

			if (project == null)
				throw new GrassPageException(404);

			nameField.setValue(project.getContentNode().getName());
			for (ContentTagOverviewTO tagDTO : project.getContentNode().getContentTags())
				keywords.addToken(tagDTO.getName());

			publicatedCheckBox.setValue(project.getContentNode().isPublicated());

			// nem?? opr??vn??n?? upravovat tento obsah
			if (!project.getContentNode().getAuthor().getName().equals(getUser().getName()) && !getUser().isAdmin())
				throw new GrassPageException(403);
		} else {
			logger.debug("Nezn??m?? operace: '{}'", operationToken);
			throw new GrassPageException(404);
		}

		try {
			projectDir = editMode ? project.getPrint3dProjectPath() : print3dService.createProjectDir();
		} catch (IOException e) {
			throw new GrassPageException(500);
		}

		editorLayout.add(new H2("N??zev projektu"));
		editorLayout.add(nameField);
		nameField.setWidthFull();

		// label
		editorLayout.add(new H2("Kl????ov?? slova"));

		// menu tag?? + textfield tag??
		keywords.addClassName(UIUtils.TOP_PULL_CSS_CLASS);
		editorLayout.add(keywords);

		keywords.isEnabled();
		keywords.setAllowNewItems(true);
		keywords.getInputField().setPlaceholder("kl????ov?? slovo");

		HorizontalLayout gridLayout = new HorizontalLayout();
		gridLayout.setPadding(false);
		gridLayout.setSpacing(true);
		gridLayout.setWidthFull();
		gridLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		editorLayout.add(gridLayout);

		final Grid<Print3dViewItemTO> grid = new Grid<>();
		final List<Print3dViewItemTO> items;
		if (editMode) {
			try {
				items = print3dService.getItems(projectDir);
			} catch (IOException e) {
				throw new GrassPageException(500, e);
			}
		} else {
			items = new ArrayList<>();
		}
		UIUtils.applyGrassDefaultStyle(grid);
		grid.setItems(items);

		grid.setWidthFull();
		grid.setHeight("400px");

		grid.addColumn(new TextRenderer<Print3dViewItemTO>(p -> p.getFile().getFileName().toString()))
				.setHeader("N??zev").setFlexGrow(100);

		grid.addColumn(new TextRenderer<Print3dViewItemTO>(p -> p.getSize())).setHeader("Velikost").setWidth("80px")
				.setTextAlign(ColumnTextAlign.END).setFlexGrow(0);

		grid.addColumn(new ComponentRenderer<Anchor, Print3dViewItemTO>(itemTO -> {
			String file = itemTO.getFile().getFileName().toString();
			Anchor anchor = new Anchor(new StreamResource(file, () -> {
				try {
					return Files.newInputStream(print3dService.getFullImage(projectDir, file));
				} catch (IOException e1) {
					UIUtils.showWarning("Obr??zek nelze zobrazit");
					return null;
				}
			}), "Zobrazit");
			anchor.setTarget("_blank");
			return anchor;
		})).setHeader("Zobrazit").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

		grid.addColumn(new ComponentRenderer<LinkButton, Print3dViewItemTO>(itemTO -> new LinkButton("Smazat", be -> {
			new ConfirmDialog("Opravdu smazat?", e -> {
				try {
					print3dService.deleteFile(itemTO, projectDir);
					items.remove(itemTO);
				} catch (Exception ex) {
					UIUtils.showWarning("Nezda??ilo se smazat n??kter?? soubory");
				}
				grid.getDataProvider().refreshAll();
			}).open();
		}))).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

		gridLayout.add(grid);

		Print3dMultiUpload upload = new Print3dMultiUpload(projectDir) {
			private static final long serialVersionUID = 8317049226635860025L;

			@Override
			protected void fileUploadSuccess(String fileName, long size) {
				Print3dViewItemTO itemTO = new Print3dViewItemTO();
				String sizeText = null;
				sizeText = HumanBytesSizeFormatter.format(size);
				itemTO.setSize(sizeText);
				itemTO.setFile(Paths.get(fileName));
				newFiles.add(itemTO);
				items.add(itemTO);
				grid.setItems(items);
			}
		};
		upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		editorLayout.add(upload);

		editorLayout.add(new H2("Nastaven??"));

		publicatedCheckBox.setLabel("Publikovat projekt");
		editorLayout.add(publicatedCheckBox);
		editorLayout.add(new Breakline());

		ButtonLayout buttonsLayout = new ButtonLayout();
		editorLayout.add(buttonsLayout);

		populateButtonsLayout(buttonsLayout);
	}

	private void populateButtonsLayout(ButtonLayout buttonLayout) {
		// Ulo??it
		SaveButton saveButton = new SaveButton(event -> {
			if (!isFormValid())
				return;
			stayInEditor = true;
			saveOrUpdateProject();
		});
		buttonLayout.add(saveButton);

		// Ulo??it a zav????t
		SaveButton saveAndCloseButton = new SaveButton("Ulo??it a zav????t", event -> {
			if (!isFormValid())
				return;
			stayInEditor = false;
			saveOrUpdateProject();
		});
		buttonLayout.add(saveAndCloseButton);
		saveAndCloseButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL).setBrowserDefaultAllowed(false);

		// Zru??it
		CloseButton cancelButton = new CloseButton("Zru??it", ev -> new ConfirmDialog(
				"Opravdu si p??ejete zav????t editor projektu ? Ve??ker?? neulo??en?? zm??ny budou ztraceny.", e -> {
					cleanAfterCancelEdit();
					if (editMode)
						returnToProject();
					else
						returnToNode();
				}).open());
		buttonLayout.add(cancelButton);
	}

	private void cleanAfterCancelEdit() {
		if (editMode) {
			print3dService.deleteFiles(newFiles, projectDir);
		} else {
			try {
				print3dService.deleteDraft(projectDir);
			} catch (Exception e) {
				logger.error("Nezda??ilo se smazat zru??en?? rozpracovan?? projekt", e);
				throw new GrassPageException(500, e);
			}
		}
	}

	private boolean isFormValid() {
		String name = nameField.getValue();
		if (name == null || name.isEmpty()) {
			UIUtils.showWarning("N??zev projektu nem????e b??t pr??zdn??");
			return false;
		}
		return true;
	}

	private void saveOrUpdateProject() {
		Print3dPayloadTO payloadTO = new Print3dPayloadTO(nameField.getValue(), projectDir, keywords.getValues(),
				publicatedCheckBox.getValue());

		eventBus.subscribe(Print3dEditorPage.this);

		if (editMode) {
			print3dService.modifyProject(project.getId(), payloadTO);
			onModifyResult(project.getId());
		} else {
			onSaveResult(print3dService.saveProject(payloadTO, node.getId(), getUser().getId()));
		}
	}

	/**
	 * Zavol?? vr??cen?? se na obsah
	 */
	private void returnToProject() {
		Div closeJsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void closeCallback() {
				UIUtils.redirect(getPageURL(print3dViewerPageFactory,
						URLIdentifierUtils.createURLIdentifier(project.getId(), project.getContentNode().getName())));
			}
		};
		closeJsDiv.setId(CLOSE_JS_DIV_ID);
		add(closeJsDiv);

		UI.getCurrent().getPage()
				.executeJs("window.onbeforeunload = null; setTimeout(function(){ document.getElementById('"
						+ CLOSE_JS_DIV_ID + "').$server.closeCallback() }, 10);");
	}

	/**
	 * zavol??n?? vr??cen?? se na kategorii
	 */
	private void returnToNode() {
		Div closeJsDiv = new Div() {
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void closeCallback() {
				UIUtils.redirect(getPageURL(nodePageFactory,
						URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
			}
		};
		closeJsDiv.setId(CLOSE_JS_DIV_ID);
		add(closeJsDiv);

		UI.getCurrent().getPage()
				.executeJs("window.onbeforeunload = null; setTimeout(function(){ document.getElementById('"
						+ CLOSE_JS_DIV_ID + "').$server.closeCallback() }, 10);");
	}

	private void onSaveResult(Long id) {
		if (id != null) {
			project = print3dService.getProjectForDetail(id);
			// soubory byly ulo??eny a nepodl??haj??
			// podm??n??n??mu smaz??n??
			newFiles.clear();
			if (!stayInEditor)
				returnToProject();
			// odte?? budeme editovat
			editMode = true;
			UIUtils.showInfo("Ulo??en?? projektu prob??hlo ??sp????n??");
		} else {
			UIUtils.showWarning("Ulo??en?? projektu se nezda??ilo");
		}
	}

	private void onModifyResult(Long id) {
		if (id != null) {
			// soubory byly ulo??eny a nepodl??haj??
			// podm??n??n??mu smaz??n??
			newFiles.clear();
			if (!stayInEditor)
				returnToProject();
			UIUtils.showInfo("??prava projektu prob??hla ??sp????n??");
		} else {
			UIUtils.showWarning("??prava projektu se nezda??ila");
		}
	}

}
