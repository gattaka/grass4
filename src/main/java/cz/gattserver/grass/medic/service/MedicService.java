package cz.gattserver.grass.medic.service;

import java.util.List;
import java.util.Set;

import cz.gattserver.grass.medic.interfaces.*;

public interface MedicService {

	// Instituce

	void deleteMedicalInstitution(Long to);

	List<MedicalInstitutionTO> getMedicalInstitutions(MedicalInstitutionTO filterTO);

	List<MedicalInstitutionTO> getMedicalInstitutions();

	void saveMedicalInstitution(MedicalInstitutionTO to);

	MedicalInstitutionTO getMedicalInstitutionById(Long id);

	// Návštěvy

	void deleteScheduledVisit(Long to);

	List<ScheduledVisitOverviewTO> getAllScheduledVisits(boolean planned);

	List<ScheduledVisitOverviewTO> getAllScheduledVisits();

	void saveScheduledVisit(ScheduledVisitTO dto);

	ScheduledVisitTO getScheduledVisitById(Long id);

	// Záznamy

	void deleteMedicalRecord(Long id);

	List<MedicalRecordTO> getMedicalRecords(MedicalRecordTO filterTO);

	List<MedicalRecordTO> getMedicalRecords();

	void saveMedicalRecord(MedicalRecordTO to);

	MedicalRecordTO getMedicalRecordById(Long id);

	// Medikamenty

	void deleteMedicament(Long id);

	Set<MedicamentTO> getMedicaments(MedicamentTO filterTO);

	Set<MedicamentTO> getMedicaments();

	void saveMedicament(MedicamentTO to);

	MedicamentTO getMedicamentById(Long id);

    boolean isMedicamentUsed(Long id);

	// Doktoři

	void deletePhysician(Long id);

	List<PhysicianTO> getPhysicians(PhysicianTO filterTO);

    List<PhysicianTO> getPhysicians();

	void savePhysician(PhysicianTO to);

	PhysicianTO getPhysicianById(Long id);

	PhysicianTO getPhysicianByLastVisit(Long institutionId);
}