package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.WineType;

public class WineOverviewTO extends DrinkOverviewTO {

	private static final long serialVersionUID = -8209033845061405698L;

	/**
	 * DB id
	 */
	private Long infoId;

	/**
	 * Vinařství
	 */
	private String winery;

	/**
	 * Ročník
	 */
	private Integer year;

	/**
	 * Typ vína
	 */
	private WineType wineType;

	public WineOverviewTO() {
	}

	@QueryProjection
	public WineOverviewTO(Long id, String name, DrinkType type, Double rating, Double alcohol, String country,
			Long infoId, String winery, Integer year, WineType wineType) {
		super(id, name, type, rating, alcohol, country);
		this.infoId = infoId;
		this.winery = winery;
		this.year = year;
		this.wineType = wineType;
	}

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
	}

	public String getWinery() {
		return winery;
	}

	public void setWinery(String winery) {
		this.winery = winery;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public WineType getWineType() {
		return wineType;
	}

	public void setWineType(WineType wineType) {
		this.wineType = wineType;
	}

}
