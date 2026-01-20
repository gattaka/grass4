package cz.gattserver.grass.drinks.model.domain;

import jakarta.persistence.*;

@Entity(name = "DRINKS_DRINK")
public class Drink {

	/**
	 * DB id
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
	@Lob
	private byte[] image;

	/**
	 * Text
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * % alkoholu
	 */
	private Double alcohol;

	/**
	 * Země původu
	 */
	private String country;

	/**
	 * Další informace k nápoji, dle typu
	 */
	private Long drinkInfo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getAlcohol() {
		return alcohol;
	}

	public void setAlcohol(Double alcohol) {
		this.alcohol = alcohol;
	}

	public Long getDrinkInfo() {
		return drinkInfo;
	}

	public void setDrinkInfo(Long drinkInfo) {
		this.drinkInfo = drinkInfo;
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
		if (!(obj instanceof Drink))
			return false;
		return ((Drink) obj).getId() == getId();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
