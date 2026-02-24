package cz.gattserver.grass.hw.ui.pages;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.hw.service.HWService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;

public class HWSettingsPageFragmentFactory extends AbstractPageFragmentFactory {

    private final HWService hwService;

    public HWSettingsPageFragmentFactory() {
        this.hwService = SpringContextHelper.getBean(HWService.class);
    }

    @Override
    public void createFragment(Div div) {
        div.add(new H2("Nastavení evidence HW"));

        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);
        layout.setPadding(false);
        div.add(layout);

        ComponentFactory componentFactory = new ComponentFactory();
        Div buttonLayout = componentFactory.createButtonLayout();
        layout.add(buttonLayout);

        Button reprocessButton =
                new Button("Přegenerovat miniatury", VaadinIcon.REFRESH.create(), e -> hwService.processMiniatures());

        buttonLayout.add( reprocessButton);
    }
}