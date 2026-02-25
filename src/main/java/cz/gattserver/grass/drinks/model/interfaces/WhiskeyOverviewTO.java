package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.WhiskeyType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public class WhiskeyOverviewTO extends DrinkOverviewTO {

    @Serial
    private static final long serialVersionUID = -4691987028031098551L;

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
}