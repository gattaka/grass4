package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;
import cz.gattserver.grass.drinks.model.domain.RumType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Setter
@Getter
public class RumTO extends DrinkTO {

    @Serial
    private static final long serialVersionUID = 6381119207408555272L;

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
}