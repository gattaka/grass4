package cz.gattserver.grass.medic.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long>, MedicalRecordRepositoryCustom {

    @Modifying
    @Query("delete MEDICAL_RECORD_MEDICAL_MEDICAMENT where id.medicalRecordId = ?1 and id.medicamentId in ?2")
    void deleteMedicalRecordMedicament(Long recordId, Set<Long> medicamentIds);
}