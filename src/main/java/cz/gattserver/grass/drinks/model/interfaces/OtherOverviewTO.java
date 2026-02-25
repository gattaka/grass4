package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public class OtherOverviewTO extends DrinkOverviewTO {

    @Serial
    private static final long serialVersionUID = 3516142660371457378L;

    /**
	 * DB id
	 */
	private Long infoId;

	/**
	 * Z čeho nápoj je
	 */
	private String ingredient;

	public OtherOverviewTO() {
	}

	@QueryProjection
	public OtherOverviewTO(Long id, String name, DrinkType type, Double rating, Double alcohol, String country,
			Long infoId, String ingredient) {
		super(id, name, type, rating, alcohol, country);
		this.infoId = infoId;
		this.ingredient = ingredient;
	}

}
