package cz.gattserver.grass.language.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.vaadin.flow.component.icon.VaadinIcon;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.UIUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass.language.facades.LanguageFacade;
import cz.gattserver.grass.language.model.domain.ItemType;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;
import cz.gattserver.grass.language.model.dto.LanguageTO;
import cz.gattserver.grass.language.web.dialogs.LanguageDialog;
import cz.gattserver.grass.language.web.tabs.CrosswordTab;
import cz.gattserver.grass.language.web.tabs.ItemsTab;
import cz.gattserver.grass.language.web.tabs.StatisticsTab;

@PageTitle("Jazyky")
@Route(value = "language", layout = MainView.class)
public class LanguagePage extends Div {

    private static final long serialVersionUID = 4767207674013382065L;

    public static final String PREKLAD_LABEL = "Překlad";

    @Autowired
    private LanguageFacade languageFacade;

    @Autowired
    private SecurityService securityService;

    private Tabs tabs;
    private Div tabLayout;
    private VerticalLayout testLayout;

    private List<Consumer<Long>> tabActions = new ArrayList<>();

    public LanguagePage() {
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        List<LanguageTO> langs = languageFacade.getLanguages();
        Grid<LanguageTO> grid = new Grid<>();
        UIUtils.applyGrassDefaultStyle(grid);
        grid.setItems(langs);
        grid.setWidthFull();
        grid.setHeight("150px");
        grid.addColumn(LanguageTO::getName).setHeader("Název");
        layout.add(grid);

        Div btnLayout = componentFactory.createButtonLayout();
        if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN)) layout.add(btnLayout);

        Div langLayout = new Div();
        layout.add(langLayout);

        tabLayout = new Div();
        tabLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(tabLayout);

        grid.addSelectionListener(se -> se.getFirstSelectedItem().ifPresent(item -> {
            langLayout.removeAll();

            long langId = item.getId();
            createTabSheet(langId);

            tabActions.add(this::createAllItemsTab);
            tabs.add(new Tab("Vše"));

            tabActions.add(this::createWordsItemsTab);
            tabs.add(new Tab("Slovíčka"));

            tabActions.add(this::createPhrasesItemsTab);
            tabs.add(new Tab("Fráze"));

            if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN)) {
                tabActions.add(this::createTestTab);
                tabs.add(new Tab("Zkoušení"));
            }

            tabActions.add(this::createCrosswordTab);
            tabs.add(new Tab("Křížovka"));

            if (securityService.getCurrentUser().getRoles().contains(CoreRole.ADMIN)) {
                tabActions.add(this::createStatisticsTab);
                tabs.add(new Tab("Statistiky"));
            }

            langLayout.add(tabs);
        }));

        btnLayout.add(componentFactory.createCreateButton(event -> new LanguageDialog(to -> {
            languageFacade.saveLanguage(to);
            langs.clear();
            langs.addAll(languageFacade.getLanguages());
            grid.getDataProvider().refreshAll();
        }).open()));

        btnLayout.add(componentFactory.createEditGridButton(item -> new LanguageDialog(item, to -> {
            languageFacade.saveLanguage(to);
            langs.clear();
            langs.addAll(languageFacade.getLanguages());
            grid.getDataProvider().refreshAll();
        }).open(), grid));
    }

    private void createTabSheet(long langId) {
        tabs = new Tabs();
        tabs.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        tabs.addSelectedChangeListener(e -> {
            tabLayout.removeAll();
            tabActions.get(tabs.getSelectedIndex()).accept(langId);
        });
    }

    private void createCrosswordTab(Long langId) {
        tabLayout.add(new CrosswordTab(langId));
    }

    private void createTestTab(Long langId) {
        ComponentFactory componentFactory = new ComponentFactory();
        Div buttonLayout = componentFactory.createButtonLayout();
        tabLayout.add(buttonLayout);

        Button allTestBtn = new Button("Spustit test všeho", event -> startTest(langId, null));
        allTestBtn.setIcon(VaadinIcon.PLAY_CIRCLE.create());
        buttonLayout.add(allTestBtn);

        Float wordsProgress = languageFacade.getSuccessRateOfLanguageAndType(ItemType.WORD, langId);
        String wordsProgressLabel = (int) (wordsProgress * 100) + "%";
        Button wordsTestBtn = new Button("Spustit test slovíček (" + wordsProgressLabel + ")",
                event -> startTest(langId, ItemType.WORD));
        wordsTestBtn.setIcon(VaadinIcon.PLAY_CIRCLE_O.create());
        buttonLayout.add(wordsTestBtn);

        Float phrasesProgress = languageFacade.getSuccessRateOfLanguageAndType(ItemType.PHRASE, langId);
        String phrasesProgressLabel = (int) (phrasesProgress * 100) + "%";
        Button phrasesTestBtn = new Button("Spustit test frází (" + phrasesProgressLabel + ")",
                event -> startTest(langId, ItemType.PHRASE));
        phrasesTestBtn.setIcon(VaadinIcon.PLAY_CIRCLE_O.create());
        buttonLayout.add(phrasesTestBtn);

        testLayout = new VerticalLayout();
        testLayout.setPadding(false);
        tabLayout.add(testLayout);
    }

    private void createGridLine(LanguageItemTO item, FormLayout gridLayout, Map<LanguageItemTO, TextField> answersMap) {
        Div label = new Div();
        label.add(item.getTranslation());
        label.setWidth(null);
        gridLayout.add(label);

        TextField answerField = UIUtils.asSmall(new TextField());
        answerField.setWidthFull();
        answerField.setPlaceholder("varianta;varianta;...");
        gridLayout.add(answerField);

        answersMap.put(item, answerField);
    }

    public void startTest(Long langId, ItemType type) {
        testLayout.removeAll();

        Map<LanguageItemTO, TextField> answersMap = new LinkedHashMap<>();

        List<LanguageItemTO> itemsToLearn = languageFacade.getLanguageItemsForTest(langId, 0, 0.1, 10, type);
        List<LanguageItemTO> itemsToImprove = languageFacade.getLanguageItemsForTest(langId, 0.1, 0.8, 5, type);
        List<LanguageItemTO> itemsToRefresh = languageFacade.getLanguageItemsForTest(langId, 0.8, 1.1, 4, type);

        FormLayout columnsLayout = new FormLayout();
        columnsLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("200px", 2));
        columnsLayout.setWidthFull();
        testLayout.add(columnsLayout);

        Div header = new Div(new Strong("Položka"));
        header.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        columnsLayout.add(header);
        columnsLayout.add(new Strong(PREKLAD_LABEL));

        header = new Div(new Strong("Nové"));
        header.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        columnsLayout.add(header, 2);
        for (LanguageItemTO item : itemsToLearn)
            createGridLine(item, columnsLayout, answersMap);

        header = new Div(new Strong("Ke zlepšení"));
        header.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        columnsLayout.add(header, 2);
        for (LanguageItemTO item : itemsToImprove)
            createGridLine(item, columnsLayout, answersMap);

        header = new Div(new Strong("Opakování"));
        header.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        columnsLayout.add(header, 2);
        for (LanguageItemTO item : itemsToRefresh)
            createGridLine(item, columnsLayout, answersMap);

        Button submitBtn = new Button("Zkontrolovat");
        submitBtn.addClickListener(e -> {
            testLayout.removeAll();

            FormLayout resultLayout = new FormLayout();
            resultLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("10px", 3));
            resultLayout.setWidthFull();
            testLayout.add(resultLayout);

            Div resultHeader = new Div(new Strong("Položka"));
            resultHeader.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
            resultHeader.addClassName("bottom-margin");
            resultLayout.add(resultHeader);

            resultHeader = new Div(new Strong(PREKLAD_LABEL));
            resultHeader.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
            resultHeader.addClassName("bottom-margin");
            resultLayout.add(resultHeader);

            resultHeader = new Div(new Strong("Odpověď"));
            resultHeader.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
            resultHeader.addClassName("bottom-margin");
            resultLayout.add(resultHeader);

            answersMap.keySet().forEach(item -> {
                TextField answerField = answersMap.get(item);
                String answer = answerField.getValue();
                String correctAnswer = item.getContent().toLowerCase().trim();

                boolean success = false;
                for (String variant : answer.toLowerCase().split(";")) {
                    if (variant.trim().equals(correctAnswer)) {
                        success = true;
                        break;
                    }
                }

                Div label = new Div();
                label.add(" ");
                label.add(item.getTranslation());
                label.setWidth(null);
                resultLayout.add(label);

                resultLayout.add(new Strong(item.getContent()));

                Div answerDiv = new Div();
                answerDiv.add(new Strong(StringUtils.isBlank(answer) ? "---" : answer));
                answerDiv.getStyle().set("color", success ? "hsl(122, 100%, 33%)" : "hsl(0, 100%, 49%)");
                resultLayout.add(answerDiv);

                languageFacade.updateItemAfterTest(item, success);

            });
        });
        testLayout.add(submitBtn);
    }

    private void createAllItemsTab(Long langId) {
        createItemsTab(langId, null);
    }

    private void createWordsItemsTab(Long langId) {
        createItemsTab(langId, ItemType.WORD);
    }

    private void createPhrasesItemsTab(Long langId) {
        createItemsTab(langId, ItemType.PHRASE);
    }

    private void createItemsTab(Long langId, ItemType type) {
        tabLayout.add(new ItemsTab(langId, type, this));
    }

    private void createStatisticsTab(Long langId) {
        tabLayout.add(new StatisticsTab(langId));
    }

    public Tabs getTabs() {
        return tabs;
    }

}
