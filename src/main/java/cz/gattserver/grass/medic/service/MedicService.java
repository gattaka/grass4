package cz.gattserver.grass.medic.service;

import java.util.List;
import java.util.Set;

import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

public interface MedicService {

	// Instituce

	void deleteMedicalInstitution(MedicalInstitutionTO dto);

	List<MedicalInstitutionTO> getMedicalInstitutions(MedicalInstitutionTO filterTO);

	List<MedicalInstitutionTO> getMedicalInstitutions();

	void saveMedicalInstitution(MedicalInstitutionTO dto);

	MedicalInstitutionTO getMedicalInstitutionById(Long id);

	// Návštěvy

	void deleteScheduledVisit(ScheduledVisitTO to);

	List<ScheduledVisitTO> getAllScheduledVisits(boolean planned);

	List<ScheduledVisitTO> getAllScheduledVisits();

	void saveScheduledVisit(ScheduledVisitTO dto);

	ScheduledVisitTO createPlannedScheduledVisitFromToBePlanned(ScheduledVisitTO dto);

	ScheduledVisitTO getScheduledVisitById(Long id);

	// Záznamy

	void deleteMedicalRecord(MedicalRecordTO to);

	List<MedicalRecordTO> getMedicalRecords(MedicalRecordTO filterTO);

	List<MedicalRecordTO> getMedicalRecords();

	void saveMedicalRecord(MedicalRecordTO to);

	MedicalRecordTO getMedicalRecordById(Long id);

	// Medikamenty

	void deleteMedicament(MedicamentTO to);

	Set<MedicamentTO> getMedicaments(MedicamentTO filterTO);

	Set<MedicamentTO> getMedicaments();

	void saveMedicament(MedicamentTO to);

	MedicamentTO getMedicamentById(Long id);

	// Doktoři

	void deletePhysician(PhysicianTO to);

	Set<PhysicianTO> getPhysicians(PhysicianTO filterTO);

	Set<PhysicianTO> getPhysicians();

	void savePhysician(PhysicianTO to);

	PhysicianTO getPhysicianById(Long id);

	PhysicianTO getPhysicianByLastVisit(Long institutionId);
}