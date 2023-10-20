package cz.gattserver.grass.medic.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import cz.gattserver.common.Identifiable;

public class MedicamentTO implements Identifiable{

	private Long id;

	/**
	 * Název léku
	 */
	@NotNull
	@Size(min = 1)
	private String name = "";

	/**
	 * Snášenlivost
	 */
	@NotNull
	@Size(min = 1)
	private String tolerance = "V pořádku";

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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MedicamentTO) {
			MedicamentTO dto = (MedicamentTO) obj;
			if (dto.getId() == null)
				return id == null;
			else
				return dto.getId().equals(id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
