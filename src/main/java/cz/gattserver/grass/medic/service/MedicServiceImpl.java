package cz.gattserver.grass.medic.service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.common.util.ServiceUtils;
import cz.gattserver.common.vaadin.dialogs.ErrorDialog;
import cz.gattserver.grass.core.exception.GrassException;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.medic.domain.*;
import cz.gattserver.grass.medic.interfaces.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
@Slf4j
public class MedicServiceImpl implements MedicService {

	@Value("${medic.root.path}")
	private String medicRootPath;

	private final FileSystemService fileSystemService;

	private final MedicalInstitutionRepository medicalInstitutionRepository;
	private final MedicalRecordMedicamentRepository medicalRecordMedicamentRepository;
	private final ScheduledVisitRepository scheduledVisitRepository;
	private final MedicalRecordRepository medicalRecordRepository;
	private final MedicamentRepository medicamentRepository;
	private final PhysicianRepository physicianRepository;

	public MedicServiceImpl(FileSystemService fileSystemService,
			MedicalInstitutionRepository medicalInstitutionRepository,
			MedicalRecordMedicamentRepository medicalRecordMedicamentRepository,
			ScheduledVisitRepository scheduledVisitRepository, MedicalRecordRepository medicalRecordRepository,
			MedicamentRepository medicamentRepository, PhysicianRepository physicianRepository) {
		this.fileSystemService = fileSystemService;
		this.medicalInstitutionRepository = medicalInstitutionRepository;
		this.medicalRecordMedicamentRepository = medicalRecordMedicamentRepository;
		this.scheduledVisitRepository = scheduledVisitRepository;
		this.medicalRecordRepository = medicalRecordRepository;
		this.medicamentRepository = medicamentRepository;
		this.physicianRepository = physicianRepository;
	}

	// Instituce

	@Override
	public void deleteMedicalInstitution(Long id) {
		medicalInstitutionRepository.deleteById(id);
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
	public void deleteMedicalRecord(Long id) {
		medicalRecordMedicamentRepository.deleteByRecordId(id);
		medicalRecordRepository.deleteById(id);
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

		Set<ReportFileTO> filesSet = to.getFiles();
		if (to.getId() != null) {
			filesSet = ServiceUtils.processDependentSetAndDeleteMissing(to.getFiles(),
					getMedicalRecordReports(to.getId()),
					set -> set.forEach(file -> deleteReportFile(to.getId(), file.getName())));
		}

		record.setId(medicalRecordRepository.save(record).getId());

		for (ReportFileTO file : filesSet) {
			try {
				Path reportsPath = getReportsPath(record.getId());
				Path reportPath = reportsPath.resolve(file.getName());

				if (!reportPath.normalize().startsWith(reportsPath))
					throw new IllegalArgumentException("Podtečení adresáře příloh");
				Files.copy(new ByteArrayInputStream(file.getContent()), reportPath,
						StandardCopyOption.REPLACE_EXISTING);
				fileSystemService.grantPermissions(reportPath);
			} catch (IOException e) {
				String msg = "Nezdařilo se uložit soubor";
				log.error(msg, e);
				new ErrorDialog(msg).open();
			}
		}

		List<MedicalRecordMedicament> medicamentsBatch = medicamentsSet.stream()
				.map(medicamentId -> new MedicalRecordMedicament(record.getId(), medicamentId))
				.collect(Collectors.toList());
		medicalRecordMedicamentRepository.saveAll(medicamentsBatch);
	}

	public void deleteReportFile(Long id, String name) {
		Path reportsPath;
		try {
			reportsPath = getReportsPath(id);
			Path reportPath = reportsPath.resolve(name);
			if (!reportPath.normalize().startsWith(reportsPath))
				throw new IllegalArgumentException("Podtečení adresáře modulu");
			Files.deleteIfExists(reportPath);
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se smazat soubor záznamu.", e);
		}
	}

	@Override
	public MedicalRecordTO getMedicalRecordById(Long id) {
		MedicalRecordTO to = medicalRecordRepository.findAndMapById(id);
		to.setMedicaments(medicalRecordRepository.findMedicamentsByRecordId(id));
		return to;
	}

	@Override
	public Long getInstitutionLastRecordId(Long institutionId) {
		return medicalRecordRepository.findInstitutionLastRecordId(institutionId);
	}

	/**
	 * Získá {@link Path} dle jména adresáře záznamu
	 *
	 * @param recordId id záznamu
	 * @throws IllegalStateException    pokud neexistuje kořenový adresář -- chyba nastavení modulu
	 * @throws IllegalArgumentException pokud předaný adresář podtéká kořen modulu
	 */
	private Path getReportsPath(Long recordId) throws IOException {
		Validate.notNull(recordId, "ID záznamu nesmí být null");
		Path rootPath = fileSystemService.getFileSystem().getPath(medicRootPath);
		if (!Files.exists(rootPath))
			throw new IllegalStateException("Kořenový adresář modulu musí existovat");
		rootPath = rootPath.normalize();
		Path recordPath = rootPath.resolve(String.valueOf(recordId));
		if (!recordPath.normalize().startsWith(rootPath))
			throw new IllegalArgumentException("Podtečení kořenového adresáře modulu");
		if (!Files.exists(recordPath))
			fileSystemService.createDirectoriesWithPerms(recordPath);
		return recordPath;
	}

	private ReportFileTO mapPathToItem(Path path) {
		ReportFileTO to = new ReportFileTO();
		to.setName(path.getFileName().toString());
		try {
			to.setHumanSize(HumanBytesSizeFormatter.format(Files.size(path), true));
		} catch (IOException e) {
			to.setHumanSize("n/a");
		}
		try {
			to.setLastModified(
					LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault()));
		} catch (IOException e) {
			to.setLastModified(null);
		}
		return to;
	}

	@Override
	public Set<ReportFileTO> getMedicalRecordReports(Long recordId) {
		Path reportsPath;
		try {
			reportsPath = getReportsPath(recordId);
			Set<ReportFileTO> set = new HashSet<>();
			try (Stream<Path> stream = Files.list(reportsPath)) {
				stream.forEach(p -> set.add(mapPathToItem(p)));
			}
			return set;
		} catch (IOException e) {
			throw new GrassException("Nezdařilo se získat přehled příloh záznamu", e);
		}
	}

	// Medikamenty

	@Override
	public void deleteMedicament(Long id) {
		medicamentRepository.deleteById(id);
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

	@Override
	public boolean isMedicamentUsed(Long id) {
		return medicamentRepository.isUsed(id);
	}

	// Doktoři

	@Override
	public void deletePhysician(Long id) {
		physicianRepository.deleteById(id);
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