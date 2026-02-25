package cz.gattserver.grass.drinks.model.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
}