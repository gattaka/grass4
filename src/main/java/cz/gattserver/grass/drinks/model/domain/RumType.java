package cz.gattserver.grass.drinks.model.domain;

import lombok.Getter;

@Getter
public enum RumType {

	BLANCO("Světlý"), DARK("Tmavý");

	private final String caption;

	RumType(String caption) {
		this.caption = caption;
	}

}