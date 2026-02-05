package cz.gattserver.grass.medic.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cz.gattserver.common.util.ServiceUtils;
import cz.gattserver.grass.medic.domain.*;
import cz.gattserver.grass.medic.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class MedicServiceImpl implements MedicService {

    @Autowired
    private MedicalInstitutionRepository medicalInstitutionRepository;

    @Autowired
    private MedicalRecordMedicamentRepository medicalRecordMedicamentRepository;

    @Autowired
    private ScheduledVisitRepository scheduledVisitRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private PhysicianRepository physicianRepository;

    // Instituce

    @Override
    public void deleteMedicalInstitution(MedicalInstitutionTO to) {
        medicalInstitutionRepository.deleteById(to.getId());
    }

    @Override
    public List<MedicalInstitutionTO> getMedicalInstitutions(MedicalInstitutionTO filterTO) {
        return medicalInstitutionRepository.findByFilter(filterTO);
    }

    @Override
    public List<MedicalInstitutionTO> getMedicalInstitutions() {
        return medicalInstitutionRepository.findByFilter(new MedicalInstitutionTO());
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
        return medicalInstitutionRepository.findAndMapById(id);
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
        medicalRecordMedicamentRepository.deleteByRecordId(to.getId());
        medicalRecordRepository.deleteById(to.getId());
    }

    @Override
    public List<MedicalRecordTO> getMedicalRecords(MedicalRecordTO filterTO) {
        return medicalRecordRepository.findByFilter(filterTO);
    }

    @Override
    public List<MedicalRecordTO> getMedicalRecords() {
        return medicalRecordRepository.findByFilter(new MedicalRecordTO());
    }

    @Override
    public void saveMedicalRecord(MedicalRecordTO to) {
        MedicalRecord record = new MedicalRecord();
        record.setId(to.getId());
        record.setDate(to.getDateTime());
        record.setRecord(to.getRecord());
        record.setPhysicianId(to.getPhysicianId());
        record.setInstitutionId(to.getInstitutionId());

        Set<Long> medicamentsSet = to.getMedicaments();
        if (to.getId() != null) {
            medicamentsSet = ServiceUtils.processDependentSetAndDeleteMissing(to.getMedicaments(),
                    medicalRecordRepository.findMedicamentsByRecordId(to.getId()),
                    set -> medicalRecordRepository.deleteMedicalRecordMedicament(to.getId(), set));
        }

        record.setId(medicalRecordRepository.save(record).getId());

        List<MedicalRecordMedicament> medicamentsBatch =
                medicamentsSet.stream().map(medicamentId -> new MedicalRecordMedicament(record.getId(), medicamentId))
                        .collect(Collectors.toList());
        medicalRecordMedicamentRepository.saveAll(medicamentsBatch);
    }

    @Override
    public MedicalRecordTO getMedicalRecordById(Long id) {
        MedicalRecordTO to = medicalRecordRepository.findAndMapById(id);
        to.setMedicaments(medicalRecordRepository.findMedicamentsByRecordId(id));
        return to;
    }

    // Medikamenty

    @Override
    public void deleteMedicament(MedicamentTO to) {
        medicamentRepository.deleteById(to.getId());
    }

    @Override
    public Set<MedicamentTO> getMedicaments(MedicamentTO filterTO) {
        return medicamentRepository.findByFilter(filterTO);
    }

    @Override
    public Set<MedicamentTO> getMedicaments() {
        return medicamentRepository.findByFilter(new MedicamentTO());
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
        return medicamentRepository.findAndMapById(id);
    }

    // Doktoři

    @Override
    public void deletePhysician(PhysicianTO to) {
        physicianRepository.deleteById(to.getId());
    }

    @Override
    public List<PhysicianTO> getPhysicians(PhysicianTO filterTO) {
        return physicianRepository.findByFilter(filterTO);
    }

    @Override
    public List<PhysicianTO> getPhysicians() {
        return physicianRepository.findByFilter(new PhysicianTO());
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
        return physicianRepository.findAndMapById(id);
    }

    @Override
    public PhysicianTO getPhysicianByLastVisit(Long institutionId) {
        return physicianRepository.findPhysicianByLastVisit(institutionId);
    }

}