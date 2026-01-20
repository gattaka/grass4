package cz.gattserver.grass.medic.domain;

import jakarta.persistence.*;

@Entity(name = "MEDICAL_MEDICAMENT")
public class Medicament {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Název léku
	 */
	private String name;

	/**
	 * Snášenlivost
	 */
	@Column(columnDefinition = "TEXT")
	private String tolerance;

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

	public String getTolerance() {
		return tolerance;
	}

	public void setTolerance(String tolerance) {
		this.tolerance = tolerance;
	}

}
