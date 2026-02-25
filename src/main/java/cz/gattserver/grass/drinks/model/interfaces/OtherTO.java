package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public class OtherTO extends DrinkTO {

    @Serial
    private static final long serialVersionUID = -1730430981752587289L;

    /**
	 * DB id
	 */
	private Long infoId;

	/**
	 * Z čeho nápoj je
	 */
	private String ingredient;

	public OtherTO() {
	}

	@QueryProjection
	public OtherTO(Long id, String name, DrinkType type, Double rating, byte[] image, String description,
			Double alcohol, String country, Long infoId, String ingredient) {
		super(id, name, type, rating, image, description, alcohol, country);
		this.infoId = infoId;
		this.ingredient = ingredient;
	}
}