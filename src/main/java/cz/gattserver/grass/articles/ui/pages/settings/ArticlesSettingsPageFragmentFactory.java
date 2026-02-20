package cz.gattserver.grass.articles.ui.pages.settings;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.grass.articles.config.ArticlesConfiguration;
import cz.gattserver.grass.articles.events.impl.ArticlesProcessProgressEvent;
import cz.gattserver.grass.articles.events.impl.ArticlesProcessResultEvent;
import cz.gattserver.grass.articles.events.impl.ArticlesProcessStartEvent;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.core.ui.util.DoubleToIntegerConverter;
import cz.gattserver.grass.core.ui.util.UIUtils;
import net.engio.mbassy.listener.Handler;

import java.util.UUID;

public class ArticlesSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

    private final ArticleService articleService;
    private final ConfigurationService configurationService;
    private final EventBus eventBus;

    private ProgressDialog progressIndicatorDialog;
    private UUID uuid;

    private Button reprocessButton;

    public ArticlesSettingsPageFragmentFactory() {
        this.articleService = SpringContextHelper.getBean(ArticleService.class);
        this.configurationService = SpringContextHelper.getBean(ConfigurationService.class);
        this.eventBus = SpringContextHelper.getBean(EventBus.class);
    }

    @Override
    public void createFragment(Div div) {
        final ArticlesConfiguration configuration = loadConfiguration();

        div.add(new H2("Nastavení článků"));

        Binder<ArticlesConfiguration> binder = new Binder<>();
        binder.setBean(new ArticlesConfiguration());
        binder.readBean(configuration);

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(false);
        div.add(layout);

        // Délka tabulátoru ve znacích
        final NumberField tabLengthField = new NumberField("Délka tabulátoru");
        tabLengthField.setStep(1);
        tabLengthField.setWidth("200px");
        binder.forField(tabLengthField).withConverter(new DoubleToIntegerConverter())
                .bind(ArticlesConfiguration::getTabLength, ArticlesConfiguration::setTabLength);
        tabLengthField.setValue((double) configuration.getTabLength());
        layout.add(tabLengthField);

        // Prodleva mezi průběžnými zálohami článku
        final NumberField backupTimeoutField = new NumberField("Prodleva mezi zálohami (minuty)");
        backupTimeoutField.setStep(1);
        backupTimeoutField.setWidth("250px");
        binder.forField(backupTimeoutField).withConverter(new DoubleToIntegerConverter())
                .bind(ArticlesConfiguration::getBackupTimeout, ArticlesConfiguration::setBackupTimeout);
        backupTimeoutField.setValue((double) configuration.getTabLength());
        layout.add(backupTimeoutField);

        // Kořenový adresář
        final TextField outputPathField = new TextField("Nastavení adresáře příloh");
        outputPathField.setWidth("300px");
        binder.forField(outputPathField)
                .bind(ArticlesConfiguration::getAttachmentsDir, ArticlesConfiguration::setAttachmentsDir);
        outputPathField.setValue(configuration.getAttachmentsDir());
        layout.add(outputPathField);

        ComponentFactory componentFactory = new ComponentFactory();

        Div buttonLayout = componentFactory.createButtonLayout();
        layout.add(buttonLayout);

        Button saveButton = componentFactory.createSaveButton(event -> {
            if (binder.writeBeanIfValid(configuration)) storeConfiguration(configuration);
        });

        Button renameAttachmentDirsButton =
                new Button("Přejmenovat adresáře příloh", VaadinIcon.COG_O.create(), event -> {
                    UIUtils.showSilentInfo(
                            "Bylo přejmenováno " + articleService.renameAttachmentDirs(UIUtils.getContextPath()) +
                                    " adresářů příloh");
                });

        reprocessButton = new Button("Přegenerovat všechny články", VaadinIcon.COG_O.create(), event -> {
            ConfirmDialog dialog = new ConfirmDialog(
                    "Přegenerování všech článků může zabrat delší čas a dojde během něj zřejmě k mnoha drobným změnám - opravdu přegenerovat ?",
                    e -> {
                        eventBus.subscribe(ArticlesSettingsPageFragmentFactory.this);
                        progressIndicatorDialog = new ProgressDialog();
                        uuid = UUID.randomUUID();
                        articleService.reprocessAllArticles(uuid, UIUtils.getContextPath());
                    });
            dialog.setWidth("460px");
            dialog.setHeight("230px");
            dialog.open();
        });
        buttonLayout.add(saveButton, renameAttachmentDirsButton, reprocessButton);
    }

    private ArticlesConfiguration loadConfiguration() {
        ArticlesConfiguration configuration = new ArticlesConfiguration();
        configurationService.loadConfiguration(configuration);
        return configuration;
    }

    private void storeConfiguration(ArticlesConfiguration configuration) {
        configurationService.saveConfiguration(configuration);
    }

    @Handler
    protected void onProcessStart(final ArticlesProcessStartEvent event) {
        progressIndicatorDialog.runInUI(() -> {
            progressIndicatorDialog.setTotal(event.getCountOfStepsToDo());
            progressIndicatorDialog.open();
        });
    }

    @Handler
    protected void onProcessProgress(ArticlesProcessProgressEvent event) {
        progressIndicatorDialog.runInUI(() -> progressIndicatorDialog.indicateProgress(event.getDescription()));
    }

    @Handler
    protected void onProcessResult(final ArticlesProcessResultEvent event) {
        progressIndicatorDialog.runInUI(() -> {
            if (progressIndicatorDialog != null) progressIndicatorDialog.close();
            reprocessButton.setEnabled(true);

            if (event.success()) {
                UIUtils.showInfo("Přegenerování článků proběhlo úspěšně");
            } else {
                UIUtils.showWarning("Přegenerování článků se nezdařilo");
            }
        });
        eventBus.unsubscribe(ArticlesSettingsPageFragmentFactory.this);
    }

}
