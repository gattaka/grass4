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
    @Column(name = "INSTITUTION_ID")
    private Long institutionId;

    /**
     * Lékař - ošetřující
     */
    @Column(name = "PHYSICIAN_ID")
    private Long physicianId;

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

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public Long getPhysicianId() {
        return physicianId;
    }

    public void setPhysicianId(Long physicianId) {
        this.physicianId = physicianId;
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
