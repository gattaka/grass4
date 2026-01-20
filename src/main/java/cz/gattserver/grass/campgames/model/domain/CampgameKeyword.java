package cz.gattserver.grass.campgames.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Klíčové slovo her
 */
@Entity(name = "CAMPGAME_KEYWORD")
public class CampgameKeyword {

	/**
	 * Identifikátor
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Název
	 */
	private String name;

	public CampgameKeyword(String name) {
		this.name = name;
	}

	public CampgameKeyword() {
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
