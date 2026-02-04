package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.OrderSpecifier;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;

import java.util.List;

public interface MedicalRecordRepositoryCustom {

    List<MedicalRecordTO> findByFilter(MedicalRecordTO filterTO);

    MedicalRecordTO findAndMapById(Long id);
}