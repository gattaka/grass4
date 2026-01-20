package cz.gattserver.grass.drinks.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "DRINKS_WHISKEYINFO")
public class WhiskeyInfo {

	/**
	 * DB id
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Stáří
	 */
	private Integer years;

	/**
	 * Typ whisky
	 */
	private WhiskeyType whiskeyType;

	public WhiskeyInfo() {
	}

	public WhiskeyInfo(Integer years, WhiskeyType whiskeyType) {
		super();
		this.years = years;
		this.whiskeyType = whiskeyType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public WhiskeyType getWhiskeyType() {
		return whiskeyType;
	}

	public void setWhiskeyType(WhiskeyType whiskeyType) {
		this.whiskeyType = whiskeyType;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

}
