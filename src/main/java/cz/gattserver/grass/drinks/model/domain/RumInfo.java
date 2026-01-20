package cz.gattserver.grass.drinks.model.domain;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity(name = "DRINKS_RUMINFO")
public class RumInfo {

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
	 * Typ rumu
	 */
	private RumType rumType;

	public RumInfo() {
	}

	public RumInfo(Integer years, RumType rumType) {
		super();
		this.years = years;
		this.rumType = rumType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RumType getRumType() {
		return rumType;
	}

	public void setRumType(RumType rumType) {
		this.rumType = rumType;
	}

	public Integer getYears() {
		return years;
	}

	public void setYears(Integer years) {
		this.years = years;
	}

}
