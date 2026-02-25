package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.WineType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public class WineOverviewTO extends DrinkOverviewTO {

    @Serial
    private static final long serialVersionUID = 2639491741912954571L;

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
}