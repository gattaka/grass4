package cz.gattserver.grass.drinks.model.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "DRINKS_BEERINFO")
public class BeerInfo {

	/**
	 * DB id
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMalts() {
		return malts;
	}

	public void setMalts(String malts) {
		this.malts = malts;
	}

	public String getHops() {
		return hops;
	}

	public void setHops(String hops) {
		this.hops = hops;
	}

	public String getBrewery() {
		return brewery;
	}

	public void setBrewery(String brewery) {
		this.brewery = brewery;
	}

	public Integer getIbu() {
		return ibu;
	}

	public void setIbu(Integer ibu) {
		this.ibu = ibu;
	}

	public Double getDegrees() {
		return degrees;
	}

	public void setDegrees(Double degrees) {
		this.degrees = degrees;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public MaltType getMaltType() {
		return maltType;
	}

	public void setMaltType(MaltType maltType) {
		this.maltType = maltType;
	}

}
