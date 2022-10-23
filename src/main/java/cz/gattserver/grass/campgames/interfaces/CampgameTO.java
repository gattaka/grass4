package cz.gattserver.grass.campgames.interfaces;

import java.util.Collection;

/**
 * Campgame
 */
public class CampgameTO extends CampgameOverviewTO {

	private static final long serialVersionUID = 4661359528372859703L;

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
	private Collection<String> keywords;

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

	public Collection<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Collection<String> keywords) {
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
