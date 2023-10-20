package cz.gattserver.grass.medic.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import cz.gattserver.common.Identifiable;

public class MedicalRecordTO implements Identifiable {

	private Long id;

	/**
	 * Místo ošetření
	 */
	@NotNull
	private MedicalInstitutionTO institution;

	// filter
	private String institutionName;

	/**
	 * Lékař - ošetřující
	 */
	@NotNull
	private PhysicianTO physician;

	// filter
	private String physicianName;

	/**
	 * Kdy se to stalo
	 */
	@NotNull
	private LocalDate date;

	@NotNull
	private LocalTime time;

	/**
	 * Záznam o vyšetření
	 */
	@NotNull
	@Size(min = 1)
	private String record = "";

	/**
	 * Napsané léky
	 */
	private Set<MedicamentTO> medicaments = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MedicalInstitutionTO getInstitution() {
		return institution;
	}

	public void setInstitution(MedicalInstitutionTO institution) {
		this.institution = institution;
	}

	public PhysicianTO getPhysician() {
		return physician;
	}

	public void setPhysician(PhysicianTO physician) {
		this.physician = physician;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public LocalDateTime getDateTime() {
		return date.atTime(time);
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public Set<MedicamentTO> getMedicaments() {
		return medicaments;
	}

	public void setMedicaments(Set<MedicamentTO> medicaments) {
		this.medicaments = medicaments;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public String getPhysicianName() {
		return physicianName;
	}

	public void setPhysicianName(String physicianName) {
		this.physicianName = physicianName;
	}

	@Override
	public String toString() {
		return getDateTime().format(DateTimeFormatter.ofPattern("d. M. yyyy HH:mm")) + " " + physician.getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MedicalRecordTO) {
			MedicalRecordTO dto = (MedicalRecordTO) obj;
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
