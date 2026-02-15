package cz.gattserver.grass.hw.ui;

import com.vaadin.flow.component.Component;
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
        HorizontalLayout navigatorLayout = new HorizontalLayout();
        RouterLink itemsLink = new RouterLink("Přehled", HWItemsPage.class);
        navigatorLayout.add(itemsLink);

        // Typy HW nejsou veřejné, aby nenapovídaly, co vše host nevidí
        if (SpringContextHelper.getBean(SecurityService.class).getCurrentUser().isAdmin()) {
            RouterLink typesLink = new RouterLink("Typy zařízení", HWTypesPage.class);
            navigatorLayout.add(typesLink);
        }

        return navigatorLayout;
    }
}