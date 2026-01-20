package cz.gattserver.grass.campgames.model.domain;

import java.util.Collection;

import jakarta.persistence.*;

/**
 * Campgame Objekt
 */
@Entity(name = "CAMPGAME")
public class Campgame {

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

	/**
	 * Popis
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * Původ
	 */
	private String origin;

	/**
	 * Klíčová slova
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAMPGAME_CAMPGAME_KEYWORD")
	private Collection<CampgameKeyword> keywords;

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

	public Collection<CampgameKeyword> getKeywords() {
		return keywords;
	}

	public void setKeywords(Collection<CampgameKeyword> keywords) {
		this.keywords = keywords;
	}

}
