package cz.gattserver.grass.medic.domain;

import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

import java.util.List;

public interface MedicalInstitutionRepositoryCustom {

    List<MedicalInstitutionTO> findByFilter(MedicalInstitutionTO filterTO);

    MedicalInstitutionTO findAndMapById(Long id);
}