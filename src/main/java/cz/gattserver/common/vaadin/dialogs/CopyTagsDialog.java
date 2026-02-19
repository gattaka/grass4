package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.ui.components.ContentsLazyGrid;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.function.Consumer;

public class CopyTagsDialog extends WebDialog {

    public CopyTagsDialog(Consumer<List<String>> onSubmit) {
        super("Výběr obsahu");
        setWidth("800px");
        setHeight("700px");
        layout.setHeightFull();

        ContentNodeService contentNodeService = SpringContextHelper.getBean(ContentNodeService.class);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Název obsahu");
        searchField.setWidthFull();
        layout.add(searchField);

        final ContentsLazyGrid searchResultsContentsGrid = new ContentsLazyGrid(false);
        searchResultsContentsGrid.setWidthFull();
        searchResultsContentsGrid.setDynamicHeight(false);
        searchResultsContentsGrid.setHeightFull();
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
                        q -> contentNodeService.getByFilter(createFilterTO(searchField), q.getOffset(), q.getLimit())
                                .stream(), q -> contentNodeService.getCountByFilter(createFilterTO(searchField)));
                contentTagsDiv.setVisible(true);
            }
            searchResultsContentsGrid.getDataProvider().refreshAll();
        });
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        Consumer<ContentNodeOverviewTO> onSelect = to -> {
            choosenTags.clear();
            contentTagsDiv.removeAll();
            if (to == null) return;
            List<String> tagList = contentNodeService.getTagsByContentId(to.id());
            for (String t : tagList) {
                contentTagsDiv.add(new Button(t));
                choosenTags.add(t);
            }
        };

        searchResultsContentsGrid.addSelectionListener(e -> onSelect.accept(e.getFirstSelectedItem().orElse(null)));

        searchResultsContentsGrid.addItemDoubleClickListener(e -> {
            onSelect.accept(e.getItem());
            onSubmit.accept(choosenTags);
            close();
        });

        ComponentFactory componentFactory = new ComponentFactory();
        layout.add(componentFactory.createDialogSubmitOrStornoLayout(e -> {
            onSubmit.accept(choosenTags);
            close();
        }, e -> close()));
    }

    private ContentNodeFilterTO createFilterTO(TextField searchField) {
        return new ContentNodeFilterTO().setName(searchField.getValue());
    }
}