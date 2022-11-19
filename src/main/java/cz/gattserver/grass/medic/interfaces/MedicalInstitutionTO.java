package cz.gattserver.grass.medic.interfaces;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cz.gattserver.common.Identifiable;

public class MedicalInstitutionTO implements Identifiable {

	private Long id;

	/**
	 * Jméno institutu
	 */
	@NotNull
	@Size(min = 1)
	private String name = "";

	/**
	 * Adresa
	 */
	@NotNull
	@Size(min = 1)
	private String address = "";

	/**
	 * Otevírací hodiny
	 */
	@NotNull
	@Size(min = 1)
	private String hours = "";

	/**
	 * Webové stránky
	 */
	private String web = "";

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MedicalInstitutionTO) {
			MedicalInstitutionTO dto = (MedicalInstitutionTO) obj;
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
