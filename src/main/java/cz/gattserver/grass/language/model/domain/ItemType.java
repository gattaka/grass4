package cz.gattserver.grass.language.model.domain;

/**
 * Typ jazykového záznamu
 * 
 * @author Hynek
 *
 */
public enum ItemType {

	/**
	 * Slovíčko
	 */
	WORD("Slovíčka"),

	/**
	 * Fráze
	 */
	PHRASE("Fráze");

	String caption;

	ItemType(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

}
