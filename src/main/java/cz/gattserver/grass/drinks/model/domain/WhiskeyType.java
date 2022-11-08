package cz.gattserver.grass.drinks.model.domain;

public enum WhiskeyType {

	SINGLE_MALT("Jednodruhová"), BLEND("Směsná");

	private String caption;

	private WhiskeyType(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

}
