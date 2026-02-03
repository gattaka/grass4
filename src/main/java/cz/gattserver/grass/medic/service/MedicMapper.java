package cz.gattserver.grass.medic.service;

import java.util.List;
import java.util.Set;

import cz.gattserver.grass.medic.domain.MedicalInstitution;
import cz.gattserver.grass.medic.domain.MedicalRecord;
import cz.gattserver.grass.medic.domain.Medicament;
import cz.gattserver.grass.medic.domain.Physician;
import cz.gattserver.grass.medic.domain.ScheduledVisit;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

public interface MedicMapper {

    MedicalInstitutionTO mapMedicalInstitution(MedicalInstitution e);

    List<MedicalInstitutionTO> mapMedicalInstitutions(List<MedicalInstitution> e);

    MedicalRecordTO mapMedicalRecord(MedicalRecord e);

    List<MedicalRecordTO> mapMedicalRecords(List<MedicalRecord> e);

    MedicamentTO mapMedicament(Medicament e);

    Set<MedicamentTO> mapMedicaments(List<Medicament> e);

    PhysicianTO mapPhysician(Physician e);

    Set<PhysicianTO> mapPhysicians(List<Physician> e);

}
