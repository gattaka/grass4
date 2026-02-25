package cz.gattserver.grass.articles.ui.pages;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.grass.articles.events.ArticlesProcessProgressEvent;
import cz.gattserver.grass.articles.events.ArticlesProcessResultEvent;
import cz.gattserver.grass.articles.events.ArticlesProcessStartEvent;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.core.ui.util.UIUtils;
import net.engio.mbassy.listener.Handler;

import java.util.UUID;

public class ArticlesSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

    private final ArticleService articleService;
    private final EventBus eventBus;

    private ProgressDialog progressIndicatorDialog;
    private UUID uuid;

    private Button reprocessButton;

    public ArticlesSettingsPageFragmentFactory() {
        this.articleService = SpringContextHelper.getBean(ArticleService.class);
        this.eventBus = SpringContextHelper.getBean(EventBus.class);
    }

    @Override
    public void createFragment(Div div) {
        div.add(new H2("Nastavení článků"));

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setPadding(false);
        div.add(layout);

        ComponentFactory componentFactory = new ComponentFactory();

        Div buttonLayout = componentFactory.createButtonLayout();
        layout.add(buttonLayout);

        reprocessButton = new Button("Přegenerovat všechny články", VaadinIcon.REFRESH.create(), event -> {
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
        buttonLayout.add(reprocessButton);
    }

    @Handler
    protected void onProcessStart(final ArticlesProcessStartEvent event) {
        progressIndicatorDialog.runInUI(() -> {
            progressIndicatorDialog.setTotal(event.steps());
            progressIndicatorDialog.open();
        });
    }

    @Handler
    protected void onProcessProgress(ArticlesProcessProgressEvent event) {
        progressIndicatorDialog.runInUI(() -> progressIndicatorDialog.indicateProgress(event.description()));
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