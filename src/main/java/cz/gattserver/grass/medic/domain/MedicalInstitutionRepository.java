package cz.gattserver.grass.medic.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass.medic.domain.MedicalInstitution;

public interface MedicalInstitutionRepository
		extends JpaRepository<MedicalInstitution, Long>, MedicalInstitutionRepositoryCustom {

}