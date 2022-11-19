package cz.gattserver.grass.hw.ui;

import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;

public class HWUIUtils {

	public static ImageIcon chooseImageIcon(HWItemOverviewTO to) {
		if (to.getState() == null)
			return null;
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
}
