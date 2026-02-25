package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.WhiskeyType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public class WhiskeyTO extends DrinkTO {

    @Serial
    private static final long serialVersionUID = 9136627674830829308L;

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

	public WhiskeyTO() {
	}

	@QueryProjection
	public WhiskeyTO(Long id, String name, DrinkType type, Double rating, byte[] image, String description,
			Double alcohol, String country, Long infoId, Integer years, WhiskeyType whiskeyType) {
		super(id, name, type, rating, image, description, alcohol, country);
		this.infoId = infoId;
		this.years = years;
		this.whiskeyType = whiskeyType;
	}
}