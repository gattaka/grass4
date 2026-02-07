package cz.gattserver.grass.hw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Typ hw
 */
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

	public HWType() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
