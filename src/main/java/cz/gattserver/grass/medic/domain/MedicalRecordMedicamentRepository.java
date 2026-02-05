package cz.gattserver.grass.medic.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MedicalRecordMedicamentRepository
        extends JpaRepository<MedicalRecordMedicament, MedicalRecordMedicamentId> {

    @Modifying
    @Query("delete MEDICAL_RECORD_MEDICAL_MEDICAMENT where id.medicalRecordId = ?1")
    void deleteByRecordId(Long id);
}