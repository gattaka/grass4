package cz.gattserver.grass.language.model.dto;

public class CrosswordCharTO implements CrosswordCell {

	private String value;

	public CrosswordCharTO(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean isWriteAllowed() {
		return true;
	}

}
