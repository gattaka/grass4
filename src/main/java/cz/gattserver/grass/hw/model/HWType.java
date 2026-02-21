package cz.gattserver.grass.hw.model;

import com.vaadin.copilot.shaded.checkerframework.checker.units.qual.A;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Typ hw
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "HW_TYPE")
public class HWType {

	/**
	 * Identifikátor hw
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Název
	 */
	private String name;

	public HWType(String name) {
		this.name = name;
	}

}