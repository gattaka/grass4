package cz.gattserver.grass.hw.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemState;
import cz.gattserver.grass.hw.ui.pages.HWItemsPage;
import cz.gattserver.grass.hw.ui.pages.HWTypesPage;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HWUIUtils {

    public static ImageIcon chooseImageIcon(HWItemOverviewTO to) {
        if (to.getState() == null) return null;
        switch (to.getState()) {
            case FIXED:
                return ImageIcon.INFO_16_ICON;
            case FAULTY:
                return ImageIcon.WARNING_16_ICON;
            case BROKEN:
                return ImageIcon.DELETE_16_ICON;
            case DISASSEMBLED:
                return ImageIcon.TRASH_16_ICON;
            case NOT_USED:
                return ImageIcon.CLOCK_16_ICON;
            case NEW:
            default:
                return null;
        }
    }

    public static Component createNavigationLayout() {
        Div smallMenu = new Div();
        smallMenu.setId("small-menu");
        smallMenu.add(new RouterLink("Položky", HWItemsPage.class));

        // Typy HW nejsou veřejné, aby nenapovídaly, co vše host nevidí
        if (SpringContextHelper.getBean(SecurityService.class).getCurrentUser().isAdmin())
            smallMenu.add(new RouterLink("Typy", HWTypesPage.class));

        return smallMenu;
    }
}