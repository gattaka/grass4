package cz.gattserver.grass.medic.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "MEDICAL_RECORD_MEDICAL_MEDICAMENT")
public class MedicalRecordMedicament {

    @EmbeddedId
    private MedicalRecordMedicamentId id;

    public MedicalRecordMedicament() {
    }

    public MedicalRecordMedicament(Long medicalRecordId, Long medicamentId) {
        this.id = new MedicalRecordMedicamentId(medicalRecordId, medicamentId);
    }

    public MedicalRecordMedicament(MedicalRecordMedicamentId id) {
        this.id = id;
    }

    public MedicalRecordMedicamentId getId() {
        return id;
    }

    public void setId(MedicalRecordMedicamentId id) {
        this.id = id;
    }
}