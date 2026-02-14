package cz.gattserver.grass.campgames.interfaces;

import java.io.Serializable;

public class CampgameOverviewTO implements Serializable {

	private Long id;
	
	/**
	 * Název
	 */
	private String name;
	
	/**
	 * Počet hráčů
	 */
	private String players;
	
	/**
	 * Čas na přípravu
	 */
	private String preparationTime;
	
	/**
	 * Délka hry
	 */
	private String playTime;

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

	public String getPlayers() {
		return players;
	}

	public void setPlayers(String players) {
		this.players = players;
	}

	public String getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(String preparationTime) {
		this.preparationTime = preparationTime;
	}

	public String getPlayTime() {
		return playTime;
	}

	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}

	@Override
	public int hashCode() {
		return id == null ? super.hashCode() : id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof CampgameOverviewTO) {
			CampgameOverviewTO to = (CampgameOverviewTO) obj;
			if (getId() != null)
				return getId().equals(to.getId());
			return super.equals(to);
		}
		return false;
	}

}
