package cz.gattserver.grass.drinks.model.domain;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "DRINKS_WINEINFO")
public class WineInfo {

	/**
	 * DB id
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Vinařství
	 */
	private String winery;

	/**
	 * Ročník
	 */
	private Integer year;

	/**
	 * Typ vína
	 */
	private WineType wineType;

	public WineInfo() {
	}

	public WineInfo(String winery, Integer year, WineType wineType) {
		super();
		this.winery = winery;
		this.year = year;
		this.wineType = wineType;
	}
}