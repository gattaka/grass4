package cz.gattserver.grass.drinks.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "DRINKS_OTHERINFO")
public class OtherInfo {

	/**
	 * DB id
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Z čeho nápoj je
	 */
	private String ingredient;

	public OtherInfo() {
	}

	public OtherInfo(String ingredient) {
		super();
		this.ingredient = ingredient;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIngredient() {
		return ingredient;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

}
