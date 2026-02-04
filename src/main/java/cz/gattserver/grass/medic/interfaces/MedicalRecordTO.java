package cz.gattserver.grass.medic.interfaces;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import cz.gattserver.common.Identifiable;

public class MedicalRecordTO implements Identifiable<Long> {

    private Long id;

    /**
     * Místo ošetření
     */
    @NotNull
    private Long institutionId;
    private String institutionName;

    /**
     * Lékař - ošetřující
     */
    @NotNull
    private Long physicianId;
    private String physicianName;

    /**
     * Kdy se to stalo
     */
    @NotNull
    private LocalDateTime dateTime;

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

    public MedicalRecordTO() {
    }

    public MedicalRecordTO(Long id) {
        this.id = id;
    }

    @QueryProjection
    public MedicalRecordTO(Long id, Long institutionId, String institutionName, Long physicianId, String physicianName,
                           LocalDateTime dateTime, String record) {
        this.id = id;
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.physicianId = physicianId;
        this.physicianName = physicianName;
        this.dateTime = dateTime;
        this.record = record;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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
        return dateTime.format(DateTimeFormatter.ofPattern("d. M. yyyy HH:mm")) + " " + getPhysicianName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MedicalRecordTO) {
            MedicalRecordTO dto = (MedicalRecordTO) obj;
            if (dto.getId() == null) return id == null;
            else return dto.getId().equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public MedicalRecordTO copy() {
        MedicalRecordTO to =
                new MedicalRecordTO(id, institutionId, institutionName, physicianId, physicianName, dateTime, record);
        to.medicaments = new HashSet<>();
        if (medicaments != null) for (MedicamentTO mTO : medicaments)
            to.medicaments.add(mTO.copy());
        return to;
    }

}
