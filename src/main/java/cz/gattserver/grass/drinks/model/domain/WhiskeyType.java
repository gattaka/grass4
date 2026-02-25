package cz.gattserver.grass.drinks.model.domain;

import lombok.Getter;

@Getter
public enum WhiskeyType {

	SINGLE_MALT("Jednodruhová"), BLEND("Směsná");

	private final String caption;

	 WhiskeyType(String caption) {
		this.caption = caption;
	}

}