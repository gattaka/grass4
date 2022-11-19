package cz.gattserver.grass.hw.interfaces;

public enum HWItemState {

	NEW("Nový"), FIXED("Opraven"), FAULTY("Poruchový"), BROKEN("Nefunkční"), DISASSEMBLED("Rozebrán"), NOT_USED(
			"Nepoužíván");

	private String name;

	private HWItemState(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
