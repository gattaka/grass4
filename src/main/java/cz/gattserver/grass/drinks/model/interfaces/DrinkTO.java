package cz.gattserver.grass.drinks.model.interfaces;

import cz.gattserver.grass.drinks.model.domain.DrinkType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class DrinkTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6849271991826979044L;

    @EqualsAndHashCode.Include
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
}