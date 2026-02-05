package cz.gattserver.grass.medic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class MedicalRecordMedicamentId implements Serializable {

    @Column(name = "MEDICAL_RECORD_ID")
    private Long medicalRecordId;

    @Column(name = "MEDICAMENTS_ID")
    private Long medicamentId;

    public MedicalRecordMedicamentId(Long medicalRecordId, Long medicamentId) {
        this.medicalRecordId = medicalRecordId;
        this.medicamentId = medicamentId;
    }

    public MedicalRecordMedicamentId() {
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof MedicalRecordMedicamentId that)) return false;

        return medicalRecordId.equals(that.medicalRecordId) && medicamentId.equals(that.medicamentId);
    }

    @Override
    public int hashCode() {
        int result = medicalRecordId.hashCode();
        result = 31 * result + medicamentId.hashCode();
        return result;
    }
}