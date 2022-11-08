package cz.gattserver.grass.drinks.model.domain;

public enum DrinkType {

	BEER("Pivo"), WINE("Víno"), RUM("Rum"), WHISKY("Whisky"), OTHER("Jiné");

	private String caption;

	private DrinkType(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

}
