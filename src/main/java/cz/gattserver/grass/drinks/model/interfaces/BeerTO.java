package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.MaltType;

public class BeerTO extends DrinkTO {

	private static final long serialVersionUID = -5561736023732334389L;

	/**
	 * DB id
	 */
	private Long infoId;

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

	public BeerTO() {
	}

	@QueryProjection
	public BeerTO(Long id, String name, DrinkType type, Double rating, byte[] image, String description, Double alcohol,
			String country, Long infoId, String brewery, Integer ibu, Double degrees, String category,
			MaltType maltType, String malts, String hops) {
		super(id, name, type, rating, image, description, alcohol, country);
		this.infoId = infoId;
		this.brewery = brewery;
		this.ibu = ibu;
		this.degrees = degrees;
		this.category = category;
		this.maltType = maltType;
		this.malts = malts;
		this.hops = hops;
	}

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
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
