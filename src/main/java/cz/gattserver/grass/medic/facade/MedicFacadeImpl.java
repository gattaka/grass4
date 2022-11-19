package cz.gattserver.grass.medic.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass.medic.dao.MedicalInstitutionRepository;
import cz.gattserver.grass.medic.dao.MedicalRecordRepository;
import cz.gattserver.grass.medic.dao.MedicamentRepository;
import cz.gattserver.grass.medic.dao.PhysicianRepository;
import cz.gattserver.grass.medic.dao.ScheduledVisitRepository;
import cz.gattserver.grass.medic.domain.MedicalInstitution;
import cz.gattserver.grass.medic.domain.MedicalRecord;
import cz.gattserver.grass.medic.domain.Medicament;
import cz.gattserver.grass.medic.domain.Physician;
import cz.gattserver.grass.medic.domain.ScheduledVisit;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitState;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

@Transactional
@Component
public class MedicFacadeImpl implements MedicFacade {

	@Autowired
	private MedicalInstitutionRepository medicalInstitutionRepository;

	@Autowired
	private ScheduledVisitRepository scheduledVisitRepository;

	@Autowired
	private MedicalRecordRepository medicalRecordRepository;

	@Autowired
	private MedicamentRepository medicamentRepository;

	@Autowired
	private PhysicianRepository physicianRepository;

	@Autowired
	private MedicMapper medicMapper;

	// Instituce

	@Override
	public void deleteMedicalInstitution(MedicalInstitutionTO institution) {
		medicalInstitutionRepository.deleteById(institution.getId());
	}

	@Override
	public List<MedicalInstitutionTO> getAllMedicalInstitutions() {
		return medicMapper.mapMedicalInstitutions(medicalInstitutionRepository.findAll());
	}

	@Override
	public void saveMedicalInstitution(MedicalInstitutionTO dto) {
		MedicalInstitution institution = new MedicalInstitution();
		institution.setId(dto.getId());
		institution.setAddress(dto.getAddress());
		institution.setHours(dto.getHours());
		institution.setName(dto.getName());
		institution.setWeb(dto.getWeb());
		medicalInstitutionRepository.save(institution);
	}

	@Override
	public MedicalInstitutionTO getMedicalInstitutionById(Long id) {
		return medicMapper.mapMedicalInstitution(medicalInstitutionRepository.findById(id).orElse(null));
	}

	// Návštěvy

	@Override
	public void deleteScheduledVisit(ScheduledVisitTO dto) {
		scheduledVisitRepository.deleteById(dto.getId());
	}

	@Override
	public List<ScheduledVisitTO> getAllScheduledVisits(boolean planned) {
		return medicMapper.mapScheduledVisits(scheduledVisitRepository.findByPlanned(planned));
	}

	@Override
	public List<ScheduledVisitTO> getAllScheduledVisits() {
		return medicMapper.mapScheduledVisits(scheduledVisitRepository.findAll());
	}

	@Override
	public ScheduledVisitTO createPlannedScheduledVisitFromToBePlanned(ScheduledVisitTO dto) {
		ScheduledVisitTO newDTO = new ScheduledVisitTO();
		newDTO.setInstitution(dto.getInstitution());
		newDTO.setState(ScheduledVisitState.PLANNED);
		newDTO.setPurpose(dto.getPurpose());
		newDTO.setRecord(dto.getRecord());
		return newDTO;
	}

	@Override
	public void saveScheduledVisit(ScheduledVisitTO dto) {
		ScheduledVisit visit = new ScheduledVisit();
		visit.setId(dto.getId());
		if (dto.getTime() == null)
			visit.setDate(dto.getDate().atStartOfDay());
		else
			visit.setDate(dto.getDate().atTime(dto.getTime()));
		visit.setPeriod(dto.getPeriod());
		visit.setPurpose(dto.getPurpose());
		visit.setPlanned(dto.isPlanned());

		// pouze pokud jde o save nikoliv o update
		if (visit.getId() == null) {
			// save nemůže mít stav MISSED
			visit.setPlanned(ScheduledVisitState.PLANNED.equals(dto.getState()));
		}

		if (dto.getRecord() != null) {
			visit.setRecord(medicalRecordRepository.findById(dto.getRecord().getId()).orElse(null));
		}

		if (dto.getInstitution() != null) {
			visit.setInstitution(medicalInstitutionRepository.findById(dto.getInstitution().getId()).orElse(null));
		}

		scheduledVisitRepository.save(visit);
	}

	@Override
	public ScheduledVisitTO getScheduledVisitById(Long id) {
		return medicMapper.mapScheduledVisit(scheduledVisitRepository.findById(id).orElse(null));
	}

	// Záznamy

	@Override
	public void deleteMedicalRecord(MedicalRecordTO dto) {
		scheduledVisitRepository.deleteById(dto.getId());
	}

	@Override
	public List<MedicalRecordTO> getAllMedicalRecords() {
		return medicMapper.mapMedicalRecords(medicalRecordRepository.findOrderByDateDesc());
	}

	@Override
	public void saveMedicalRecord(MedicalRecordTO dto) {
		MedicalRecord record = new MedicalRecord();
		record.setId(dto.getId());
		record.setDate(dto.getDate().atTime(dto.getTime()));
		record.setRecord(dto.getRecord());

		if (dto.getPhysician() != null)
			record.setPhysician(physicianRepository.findById(dto.getPhysician().getId()).orElse(null));

		if (dto.getInstitution() != null)
			record.setInstitution(medicalInstitutionRepository.findById(dto.getInstitution().getId()).orElse(null));

		List<Medicament> medicaments = new ArrayList<>();
		for (MedicamentTO m : dto.getMedicaments()) {
			Medicament medicament = medicamentRepository.findById(m.getId()).orElse(null);
			medicaments.add(medicament);
		}
		record.setMedicaments(medicaments);

		medicalRecordRepository.save(record);
	}

	@Override
	public MedicalRecordTO getMedicalRecordById(Long id) {
		return medicMapper.mapMedicalRecord(medicalRecordRepository.findById(id).orElse(null));
	}

	// Medikamenty

	@Override
	public void deleteMedicament(MedicamentTO dto) {
		medicamentRepository.deleteById(dto.getId());
	}

	@Override
	public Set<MedicamentTO> getAllMedicaments() {
		return medicMapper.mapMedicaments(medicamentRepository.findAll());
	}

	@Override
	public void saveMedicament(MedicamentTO dto) {
		Medicament medicament = new Medicament();
		medicament.setId(dto.getId());
		medicament.setName(dto.getName());
		medicament.setTolerance(dto.getTolerance());
		medicamentRepository.save(medicament);
	}

	@Override
	public MedicamentTO getMedicamentById(Long id) {
		return medicMapper.mapMedicament(medicamentRepository.findById(id).orElse(null));
	}

	// Doktoři

	@Override
	public void deletePhysician(PhysicianTO dto) {
		physicianRepository.deleteById(dto.getId());
	}

	@Override
	public Set<PhysicianTO> getAllPhysicians() {
		return medicMapper.mapPhysicians(physicianRepository.findAll());
	}

	@Override
	public void savePhysician(PhysicianTO dto) {
		Physician physician = new Physician();
		physician.setId(dto.getId());
		physician.setName(dto.getName());
		physicianRepository.save(physician);
	}

	@Override
	public PhysicianTO getPhysicianById(Long id) {
		return medicMapper.mapPhysician(physicianRepository.findById(id).orElse(null));
	}

}
