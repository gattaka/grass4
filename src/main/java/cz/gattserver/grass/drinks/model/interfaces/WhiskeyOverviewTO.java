package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.WhiskeyType;

public class WhiskeyOverviewTO extends DrinkOverviewTO {

	/**
	 * DB id
	 */
	private Long infoId;

	/**
	 * Stáří
	 */
	private Integer years;

	/**
	 * Typ whisky
	 */
	private WhiskeyType whiskeyType;

	public WhiskeyOverviewTO() {
	}

	@QueryProjection
	public WhiskeyOverviewTO(Long id, String name, DrinkType type, Double rating, Double alcohol, String country,
			Long infoId, Integer years, WhiskeyType whiskeyType) {
		super(id, name, type, rating, alcohol, country);
		this.infoId = infoId;
		this.years = years;
		this.whiskeyType = whiskeyType;
	}

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
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
