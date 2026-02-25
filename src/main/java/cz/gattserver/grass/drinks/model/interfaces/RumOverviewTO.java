package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.RumType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public class RumOverviewTO extends DrinkOverviewTO {

    @Serial
    private static final long serialVersionUID = 4656488873038057357L;

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

	public RumOverviewTO() {
	}

	@QueryProjection
	public RumOverviewTO(Long id, String name, DrinkType type, Double rating, Double alcohol, String country,
			Long infoId, Integer years, RumType rumType) {
		super(id, name, type, rating, alcohol, country);
		this.infoId = infoId;
		this.years = years;
		this.rumType = rumType;
	}
}