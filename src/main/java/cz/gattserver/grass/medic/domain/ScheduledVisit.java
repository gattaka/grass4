package cz.gattserver.grass.medic.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity(name = "MEDICAL_VISIT")
public class ScheduledVisit {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Účel návštěvy
	 */
	@NotNull
	private String purpose;

	/**
	 * Místo, kam se dostavit
	 */
	@ManyToOne
	private MedicalInstitution institution;

	/**
	 * Záznam - návštěva, ze které vzešlo toto datum návštěvy
	 */
	@OneToOne
	private MedicalRecord record;

	/**
	 * Objednán ? Nebo je ještě potřeba se objednat ?
	 */
	private boolean planned;

	/**
	 * Datum kontroly
	 */
	private LocalDateTime date;

	/**
	 * Perioda v měsících
	 */
	private int period;

	public boolean isPlanned() {
		return planned;
	}

	public void setPlanned(boolean planned) {
		this.planned = planned;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

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

	public MedicalRecord getRecord() {
		return record;
	}

	public void setRecord(MedicalRecord record) {
		this.record = record;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

}
