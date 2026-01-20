package cz.gattserver.grass.medic.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity(name = "MEDICAL_RECORD")
public class MedicalRecord {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Místo ošetření
	 */
	@ManyToOne
	private MedicalInstitution institution;

	/**
	 * Lékař - ošetřující
	 */
	@ManyToOne
	private Physician physician;

	/**
	 * Kdy se to stalo
	 */
	private LocalDateTime date;

	/**
	 * Záznam o vyšetření
	 */
	@Column(columnDefinition = "TEXT")
	private String record;

	/**
	 * Napsané léky
	 */
	@ManyToMany
	private List<Medicament> medicaments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MedicalInstitution getInstitution() {
		return institution;
	}

	public void setInstitution(MedicalInstitution institution) {
		this.institution = institution;
	}

	public Physician getPhysician() {
		return physician;
	}

	public void setPhysician(Physician physician) {
		this.physician = physician;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public List<Medicament> getMedicaments() {
		return medicaments;
	}

	public void setMedicaments(List<Medicament> medicaments) {
		this.medicaments = medicaments;
	}

}
