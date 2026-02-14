package cz.gattserver.grass.drinks.model.interfaces;

import cz.gattserver.grass.drinks.model.domain.DrinkType;

import java.io.Serializable;

public abstract class DrinkTO implements Serializable {

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
	 * Obrázek
	 */
	private byte[] image;

	/**
	 * Text
	 */
	private String description;

	/**
	 * % alkoholu
	 */
	private Double alcohol;

	/**
	 * Země původu
	 */
	private String country;

	public DrinkTO() {
	}

	public DrinkTO(Long id, String name, DrinkType type, Double rating, byte[] image, String description,
			Double alcohol, String country) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.rating = rating;
		this.image = image;
		this.description = description;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DrinkType getType() {
		return type;
	}

	public void setType(DrinkType type) {
		this.type = type;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DrinkTO))
			return false;
		return ((DrinkTO) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
