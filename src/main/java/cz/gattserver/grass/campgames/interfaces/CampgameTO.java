package cz.gattserver.grass.campgames.interfaces;

import java.util.Set;

/**
 * Campgame
 */
public class CampgameTO extends CampgameOverviewTO {

	/**
	 * Popis hry
	 */
	private String description;

	/**
	 * Původ
	 */
	private String origin;

	/**
	 * Klíčová slova
	 */
	private Set<String> keywords;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}