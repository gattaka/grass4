package cz.gattserver.grass.drinks.model.domain;

public enum WineType {

	RED("Červené"), WHITE("Bílé"), ROSE("Růžové"), FRUIT("Ovocné"), MEAD("Medové");

	private String caption;

	private WineType(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

}
