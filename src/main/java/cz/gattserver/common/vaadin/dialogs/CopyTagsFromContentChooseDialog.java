package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.ComponentFactory;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.ui.components.ContentsLazyGrid;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CopyTagsFromContentChooseDialog extends WebDialog {

	public CopyTagsFromContentChooseDialog(Consumer<List<String>> onSelect) {
		super("Výběr obsahu");
		setWidth("800px");

		ContentNodeService contentNodeService = SpringContextHelper.getBean(ContentNodeService.class);

		TextField searchField = new TextField();
		searchField.setPlaceholder("Název obsahu");
		searchField.setWidthFull();
		layout.add(searchField);

		final ContentsLazyGrid searchResultsContentsGrid = new ContentsLazyGrid();
		searchResultsContentsGrid.setWidthFull();
		searchResultsContentsGrid.setDynamicHeight(false);
		searchResultsContentsGrid.setHeight("400px");
		searchResultsContentsGrid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		searchResultsContentsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
		layout.add(searchResultsContentsGrid);

		List<String> choosenTags = new ArrayList<>();

		Div contentTagsDiv = new Div();
		contentTagsDiv.setId("content-tags-div");
		contentTagsDiv.setVisible(false);
		layout.add(contentTagsDiv);

		searchField.addValueChangeListener(e -> {
			String value = searchField.getValue();
			if (StringUtils.isNotBlank(value) && !contentTagsDiv.isVisible()) {
				searchResultsContentsGrid.populate(true,
						q -> contentNodeService.getByFilter(createFilterTO(searchField), q.getOffset(), q.getLimit()).stream(),
						q -> contentNodeService.getCountByFilter(createFilterTO(searchField)));
				contentTagsDiv.setVisible(true);
			}
			searchResultsContentsGrid.getDataProvider().refreshAll();
		});
		searchField.setValueChangeMode(ValueChangeMode.EAGER);

		searchResultsContentsGrid.addSelectionListener(e -> {
			choosenTags.clear();
			contentTagsDiv.removeAll();
			if (e.getFirstSelectedItem().isEmpty())
				return;
			List<String> tagList = contentNodeService.getTagsByContentId(e.getFirstSelectedItem().get().getId());
			for (String t : tagList) {
				contentTagsDiv.add(new Button(t));
				choosenTags.add(t);
			}
		});

		ComponentFactory componentFactory = new ComponentFactory();
		HorizontalLayout btnLayout = componentFactory.createDialogButtonLayout();
		layout.add(btnLayout);

		Button chooseButton = new Button("Vybrat", e -> {
			onSelect.accept(choosenTags);
			close();
		});
		chooseButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		btnLayout.add(chooseButton);

		btnLayout.add(new Button("Storno", e -> close()));
	}

	private ContentNodeFilterTO createFilterTO(TextField searchField) {
		return new ContentNodeFilterTO().setName(searchField.getValue());
	}
}