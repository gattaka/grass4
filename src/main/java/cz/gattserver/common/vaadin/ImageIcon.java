package cz.gattserver.common.vaadin;

import com.vaadin.flow.server.StreamResource;

public enum ImageIcon {

	/** */
	ADDRESS_16_ICON("address_16.png"),
	/** */
	BLOCK_16_ICON("block_16.png"),
	/** */
	BOOKMARK_16_ICON("bookmark_16.png"),
	/** */
	BRIEFCASE_16_ICON("briefcase_16.png"),
	/** */
	BRIEFCASE_PLUS_16_ICON("briefcase_plus_16.png"),
	/** */
	BROKEN_HEART_16_ICON("broken_heart_16.png"),
	/** */
	BUBBLE_16_ICON("bubble_16.png"),
	/** */
	BUY_16_ICON("buy_16.png"),
	/** */
	CALENDAR_16_ICON("calendar_16.png"),
	/** */
	CLIPBOARD_16_ICON("clipboard_16.png"),
	/** */
	CLOCK_16_ICON("clock_16.png"),
	/** */
	DELETE_16_ICON("delete_16.png"),
	/** */
	DIAGRAM_16_ICON("diagram_16.png"),
	/** */
	DOCUMENT_16_ICON("document_16.png"),
	/** */
	DOWN_16_ICON("down_16.png"),
	/** */
	FLAG_16_ICON("flag_16.png"),
	/** */
	FOLDER_16_ICON("folder_16.png"),
	/** */
	GEAR2_16_ICON("gear2_16.png"),
	/** */
	GLOBE_16_ICON("globe_16.png"),
	/** */
	HEART_16_ICON("heart_16.png"),
	/** */
	HELP_16_ICON("help_16.png"),
	/** */
	HOME_16_ICON("home_16.png"),
	/** */
	IMG_16_ICON("img_16.png"),
	/** */
	INFO_16_ICON("info_16.png"),
	/** */
	KEY_16_ICON("key_16.png"),
	/** */
	LABEL_16_ICON("label_16.png"),
	/** */
	LEFT_16_ICON("left_16.png"),
	/** */
	LETTER_16_ICON("letter_16.png"),
	/** */
	MONITOR_16_ICON("monitor_16.png"),
	/** */
	MOVE_16_ICON("move_16.png"),
	/** */
	PENCIL_16_ICON("pencil_16.png"),
	/** */
	PLUS_16_ICON("plus_16.png"),
	/** */
	PRESENT_16_ICON("present_16.png"),
	/** */
	PRINT_16_ICON("print_16.png"),
	/** */
	QUICKEDIT_16_ICON("quickedit_16.png"),
	/** */
	RIGHT_16_ICON("right_16.png"),
	/** */
	SAVE_16_ICON("save_16.png"),
	/** */
	SEARCH_16_ICON("search_16.png"),
	/** */
	SHIELD_16_ICON("shield_16.png"),
	/** */
	STATISTICS_16_ICON("statistics_16.png"),
	/** */
	STOP_16_ICON("stop_16.png"),
	/** */
	TICK_16_ICON("tick_16.png"),
	/** */
	TRASH_16_ICON("trash_16.png"),
	/** */
	UP_16_ICON("up_16.png"),
	/** */
	USER_16_ICON("user_16.png"),
	/** */
	WALLET_16_ICON("wallet_16.png"),
	/** */
	WARNING_16_ICON("warning_16.png");

	private String image;

	public String getImage() {
		return image;
	}

	private ImageIcon(String name) {
		this.image = name;
	}
	
	public StreamResource createResource() {
		String name = "icons/" + image;
		return new StreamResource(image, () -> ImageIcon.class.getResourceAsStream(name));
	}
}
