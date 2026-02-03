package cz.gattserver.grass.medic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass.medic.interfaces.*;
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
    public void deleteMedicalInstitution(MedicalInstitutionTO to) {
        medicalInstitutionRepository.deleteById(to.getId());
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
    public void saveMedicalInstitution(MedicalInstitutionTO to) {
        MedicalInstitution institution = new MedicalInstitution();
        institution.setId(to.getId());
        institution.setAddress(to.getAddress());
        institution.setHours(to.getHours());
        institution.setName(to.getName());
        institution.setWeb(to.getWeb());
        medicalInstitutionRepository.save(institution);
    }

    @Override
    public MedicalInstitutionTO getMedicalInstitutionById(Long id) {
        return medicMapper.mapMedicalInstitution(medicalInstitutionRepository.findById(id).orElse(null));
    }

    // Návštěvy

    @Override
    public void deleteScheduledVisit(Long id) {
        scheduledVisitRepository.deleteById(id);
    }

    @Override
    public List<ScheduledVisitOverviewTO> getAllScheduledVisits(boolean planned) {
        ScheduledVisitTO filter = new ScheduledVisitTO();
        filter.setPlanned(planned);
        return scheduledVisitRepository.findByFilter(filter);
    }

    @Override
    public List<ScheduledVisitOverviewTO> getAllScheduledVisits() {
        return scheduledVisitRepository.findByFilter(new ScheduledVisitTO());
    }

    @Override
    public void saveScheduledVisit(ScheduledVisitTO to) {
        ScheduledVisit visit = new ScheduledVisit();
        visit.setId(to.getId());
        visit.setDate(to.getDateTime());
        visit.setPeriod(to.getPeriod());
        visit.setPurpose(to.getPurpose());
        visit.setPlanned(to.getPlanned());
        visit.setRecordId(to.getRecordId());
        visit.setInstitutionId(to.getInstitutionId());

        scheduledVisitRepository.save(visit);
    }

    @Override
    public ScheduledVisitTO getScheduledVisitById(Long id) {
        return scheduledVisitRepository.findForDetailById(id);
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
        record.setDate(to.getDateTime());
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
