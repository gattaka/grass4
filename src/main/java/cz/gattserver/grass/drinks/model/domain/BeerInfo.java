package cz.gattserver.grass.drinks.model.domain;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "DRINKS_BEERINFO")
public class BeerInfo {

	/**
	 * DB id
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Pivovar
	 */
	private String brewery;

	/**
	 * Hořkost -- International Bitterness Units scale (IBU)
	 */
	private Integer ibu;

	/**
	 * Stupňovitost
	 */
	private Double degrees;

	/**
	 * Kategorie -- IPA, APA, Ležák apod.
	 */
	private String category;

	/**
	 * Druh sladu
	 */
	private MaltType maltType;

	/**
	 * Použité slady
	 */
	private String malts;

	/**
	 * Použité chmely
	 */
	private String hops;

	public BeerInfo() {
	}

	public BeerInfo(String brewery, Integer ibu, Double degrees, String category, MaltType maltType, String malts,
			String hops) {
		super();
		this.brewery = brewery;
		this.ibu = ibu;
		this.degrees = degrees;
		this.category = category;
		this.maltType = maltType;
		this.malts = malts;
		this.hops = hops;
	}

}
