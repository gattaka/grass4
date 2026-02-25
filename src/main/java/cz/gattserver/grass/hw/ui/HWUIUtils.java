package cz.gattserver.grass.hw.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouterLink;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.ui.pages.HWItemsPage;
import cz.gattserver.grass.hw.ui.pages.HWTypesPage;

public class HWUIUtils {

    public static ImageIcon chooseImageIcon(HWItemOverviewTO to) {
        if (to.getState() == null) return null;
        return switch (to.getState()) {
            case FIXED -> ImageIcon.INFO_16_ICON;
            case FAULTY -> ImageIcon.WARNING_16_ICON;
            case BROKEN -> ImageIcon.DELETE_16_ICON;
            case DISASSEMBLED -> ImageIcon.TRASH_16_ICON;
            case NOT_USED -> ImageIcon.CLOCK_16_ICON;
            default -> null;
        };
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