package cz.gattserver.grass.medic.facade;

import java.util.List;
import java.util.Set;

import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

public interface MedicFacade {

	// Instituce

	void deleteMedicalInstitution(MedicalInstitutionTO dto);

	List<MedicalInstitutionTO> getAllMedicalInstitutions();

	void saveMedicalInstitution(MedicalInstitutionTO dto);

	MedicalInstitutionTO getMedicalInstitutionById(Long id);

	// Návštěvy

	void deleteScheduledVisit(ScheduledVisitTO dto);

	List<ScheduledVisitTO> getAllScheduledVisits(boolean planned);

	List<ScheduledVisitTO> getAllScheduledVisits();

	void saveScheduledVisit(ScheduledVisitTO dto);

	ScheduledVisitTO createPlannedScheduledVisitFromToBePlanned(ScheduledVisitTO dto);

	ScheduledVisitTO getScheduledVisitById(Long id);

	// Záznamy

	void deleteMedicalRecord(MedicalRecordTO dto);

	List<MedicalRecordTO> getAllMedicalRecords();

	void saveMedicalRecord(MedicalRecordTO dto);

	MedicalRecordTO getMedicalRecordById(Long id);

	// Medikamenty

	void deleteMedicament(MedicamentTO dto);

	Set<MedicamentTO> getAllMedicaments();

	void saveMedicament(MedicamentTO dto);

	MedicamentTO getMedicamentById(Long id);

	// Doktoři

	void deletePhysician(PhysicianTO dto);

	Set<PhysicianTO> getAllPhysicians();

	void savePhysician(PhysicianTO dto);

	PhysicianTO getPhysicianById(Long id);

}
