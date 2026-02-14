package cz.gattserver.grass.drinks.model.interfaces;

import cz.gattserver.grass.drinks.model.domain.DrinkType;

import java.io.Serializable;

public class DrinkOverviewTO implements Serializable {

	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Typ
	 */
	private DrinkType type;

	/**
	 * Hodnocení
	 */
	private Double rating;

	/**
	 * % alkoholu
	 */
	private Double alcohol;

	/**
	 * Země původu
	 */
	private String country;

	public DrinkOverviewTO() {
	}

	public DrinkOverviewTO(Long id, String name, DrinkType type, Double rating, Double alcohol, String country) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.rating = rating;
		this.alcohol = alcohol;
		this.country = country;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAlcohol() {
		return alcohol;
	}

	public void setAlcohol(Double alcohol) {
		this.alcohol = alcohol;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public DrinkType getType() {
		return type;
	}

	public void setType(DrinkType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DrinkOverviewTO))
			return false;
		return ((DrinkOverviewTO) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
