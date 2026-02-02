package cz.gattserver.grass.medic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass.medic.domain.MedicalInstitutionRepository;
import cz.gattserver.grass.medic.domain.MedicalRecordRepository;
import cz.gattserver.grass.medic.domain.MedicamentRepository;
import cz.gattserver.grass.medic.domain.PhysicianRepository;
import cz.gattserver.grass.medic.domain.ScheduledVisitRepository;
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
public class MedicServiceImpl implements MedicService {

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
	public List<MedicalInstitutionTO> getMedicalInstitutions(MedicalInstitutionTO filterTO) {
		return medicMapper.mapMedicalInstitutions(medicalInstitutionRepository.findList(filterTO));
	}

	@Override
	public List<MedicalInstitutionTO> getMedicalInstitutions() {
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
	public void deleteScheduledVisit(ScheduledVisitTO to) {
		scheduledVisitRepository.deleteById(to.getId());
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
	public void saveScheduledVisit(ScheduledVisitTO to) {
		ScheduledVisit visit = new ScheduledVisit();
		visit.setId(to.getId());
		if (to.getTime() == null)
			visit.setDate(to.getDate().atStartOfDay());
		else
			visit.setDate(to.getDate().atTime(to.getTime()));
		visit.setPeriod(to.getPeriod());
		visit.setPurpose(to.getPurpose());
		visit.setPlanned(to.isPlanned());

		// pouze pokud jde o save nikoliv o update
		if (visit.getId() == null) {
			// save nemůže mít stav MISSED
			visit.setPlanned(ScheduledVisitState.PLANNED.equals(to.getState()));
		}

		if (to.getRecord() != null)
			visit.setRecord(medicalRecordRepository.findById(to.getRecord().getId()).orElse(null));

		if (to.getInstitution() != null)
			visit.setInstitution(medicalInstitutionRepository.findById(to.getInstitution().getId()).orElse(null));

		scheduledVisitRepository.save(visit);
	}

	@Override
	public ScheduledVisitTO getScheduledVisitById(Long id) {
		return medicMapper.mapScheduledVisit(scheduledVisitRepository.findById(id).orElse(null));
	}

	// Záznamy

	@Override
	public void deleteMedicalRecord(MedicalRecordTO to) {
		scheduledVisitRepository.deleteById(to.getId());
	}

	@Override
	public List<MedicalRecordTO> getMedicalRecords(MedicalRecordTO filterTO) {
		return medicMapper.mapMedicalRecords(medicalRecordRepository.findList(filterTO));
	}

	@Override
	public List<MedicalRecordTO> getMedicalRecords() {
		return medicMapper.mapMedicalRecords(medicalRecordRepository.findAll(Sort.by(Sort.Order.desc("date"))));
	}

	@Override
	public void saveMedicalRecord(MedicalRecordTO to) {
		MedicalRecord record = new MedicalRecord();
		record.setId(to.getId());
		record.setDate(to.getDate().atTime(to.getTime()));
		record.setRecord(to.getRecord());

		if (to.getPhysician() != null)
			record.setPhysician(physicianRepository.findById(to.getPhysician().getId()).orElse(null));

		if (to.getInstitution() != null)
			record.setInstitution(medicalInstitutionRepository.findById(to.getInstitution().getId()).orElse(null));

		List<Medicament> medicaments = new ArrayList<>();
		for (MedicamentTO m : to.getMedicaments()) {
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
	public void deleteMedicament(MedicamentTO to) {
		medicamentRepository.deleteById(to.getId());
	}

	@Override
	public Set<MedicamentTO> getMedicaments(MedicamentTO filterTO) {
		return medicMapper.mapMedicaments(medicamentRepository.findList(filterTO));
	}

	@Override
	public Set<MedicamentTO> getMedicaments() {
		return medicMapper.mapMedicaments(medicamentRepository.findAll());
	}

	@Override
	public void saveMedicament(MedicamentTO to) {
		Medicament medicament = new Medicament();
		medicament.setId(to.getId());
		medicament.setName(to.getName());
		medicament.setTolerance(to.getTolerance());
		medicamentRepository.save(medicament);
	}

	@Override
	public MedicamentTO getMedicamentById(Long id) {
		return medicMapper.mapMedicament(medicamentRepository.findById(id).orElse(null));
	}

	// Doktoři

	@Override
	public void deletePhysician(PhysicianTO to) {
		physicianRepository.deleteById(to.getId());
	}

	@Override
	public Set<PhysicianTO> getPhysicians(PhysicianTO filterTO) {
		return medicMapper.mapPhysicians(physicianRepository.findList(filterTO));
	}

	@Override
	public Set<PhysicianTO> getPhysicians() {
		return medicMapper.mapPhysicians(physicianRepository.findAll());
	}

	@Override
	public void savePhysician(PhysicianTO to) {
		Physician physician = new Physician();
		physician.setId(to.getId());
		physician.setName(to.getName());
        physician.setEmail(to.getEmail());
        physician.setPhone(to.getPhone());
		physicianRepository.save(physician);
	}

	@Override
	public PhysicianTO getPhysicianById(Long id) {
		return medicMapper.mapPhysician(physicianRepository.findById(id).orElse(null));
	}

	@Override
	public PhysicianTO getPhysicianByLastVisit(Long institutionId) {
		return medicMapper.mapPhysician(physicianRepository.findPhysicianByLastVisit(institutionId));
	}

}
