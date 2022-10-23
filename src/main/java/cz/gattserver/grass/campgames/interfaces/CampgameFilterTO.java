package cz.gattserver.grass.campgames.interfaces;

import java.util.Collection;

public class CampgameFilterTO extends CampgameOverviewTO {

	private static final long serialVersionUID = 3678406951423588173L;

	/**
	 * Klíčová slova
	 */
	private Collection<String> keywords;

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
