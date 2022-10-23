package cz.gattserver.grass.campgames.model.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

/**
 * Klíčové slovo her
 */
@Entity(name = "CAMPGAME_KEYWORD")
public class CampgameKeyword {

	/**
	 * Identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
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
