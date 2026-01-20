package cz.gattserver.grass.drinks.model.domain;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWinery() {
		return winery;
	}

	public void setWinery(String winery) {
		this.winery = winery;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public WineType getWineType() {
		return wineType;
	}

	public void setWineType(WineType wineType) {
		this.wineType = wineType;
	}
}