package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.RumType;

public class RumTO extends DrinkTO {

	/**
	 * DB id
	 */
	private Long infoId;

	/**
	 * Stáří
	 */
	private Integer years;

	/**
	 * Typ rumu
	 */
	private RumType rumType;

	public RumTO() {
	}

	@QueryProjection
	public RumTO(Long id, String name, DrinkType type, Double rating, byte[] image, String description, Double alcohol,
			String country, Long infoId, Integer years, RumType rumType) {
		super(id, name, type, rating, image, description, alcohol, country);
		this.infoId = infoId;
		this.years = years;
		this.rumType = rumType;
	}

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
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
