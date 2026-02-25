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
public class DrinkOverviewTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1882637563943332324L;

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
}