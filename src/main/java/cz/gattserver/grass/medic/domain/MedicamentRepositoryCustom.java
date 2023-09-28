package cz.gattserver.grass.medic.domain;

import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;

import java.util.List;

public interface MedicamentRepositoryCustom {

	List<Medicament> findList(MedicamentTO filterTO);
}
