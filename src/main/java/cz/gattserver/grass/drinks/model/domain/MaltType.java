package cz.gattserver.grass.drinks.model.domain;

import lombok.Getter;

/**
 * Typ sladu
 * 
 * @author gattaka
 *
 */
@Getter
public enum MaltType {

	BARLEY("Ječmen"), WHEAT("Pšenice"), RYE("Žito"), BARLEY_WHEAT("Ječmen a pšenice"), BARLEY_RYE("Ječmen a žito");

	private final String caption;

	MaltType(String caption) {
		this.caption = caption;
	}

}