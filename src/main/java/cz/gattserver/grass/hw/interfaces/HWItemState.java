package cz.gattserver.grass.hw.interfaces;

import lombok.Getter;

@Getter
public enum HWItemState {

	NEW("Nový"), FIXED("Opraven"), FAULTY("Poruchový"), BROKEN("Nefunkční"), DISASSEMBLED("Rozebrán"), NOT_USED(
			"Nepoužíván");

	private final String name;

	 HWItemState(String name) {
		this.name = name;
	}

    @Override
	public String toString() {
		return name;
	}

}
