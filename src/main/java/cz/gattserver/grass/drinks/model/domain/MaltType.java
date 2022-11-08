package cz.gattserver.grass.drinks.model.domain;

/**
 * Typ sladu
 * 
 * @author gattaka
 *
 */
public enum MaltType {

	BARLEY("Ječmen"), WHEAT("Pšenice"), RYE("Žito"), BARLEY_WHEAT("Ječmen a pšenice"), BARLEY_RYE("Ječmen a žito");

	private String caption;

	private MaltType(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

}
