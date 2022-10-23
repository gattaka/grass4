package cz.gattserver.grass.campgames.interfaces;

/**
 * Klíčové slovo
 */
public class CampgameKeywordTO {

	/**
	 * Identifikátor
	 */
	private Long id;

	/**
	 * Název
	 */
	private String name;

	public CampgameKeywordTO(String name) {
		this.name = name;
	}

	public CampgameKeywordTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
