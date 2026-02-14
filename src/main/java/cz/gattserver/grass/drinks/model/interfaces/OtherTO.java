package cz.gattserver.grass.drinks.model.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.drinks.model.domain.DrinkType;

public class OtherTO extends DrinkTO {

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

	public Long getInfoId() {
		return infoId;
	}

	public void setInfoId(Long infoId) {
		this.infoId = infoId;
	}

	public String getIngredient() {
		return ingredient;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

}
