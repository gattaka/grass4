package cz.gattserver.grass.medic.facade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

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

@Component
public class MedicMapperImpl implements MedicMapper {

	public MedicalInstitutionTO mapMedicalInstitution(MedicalInstitution e) {
		if (e == null)
			return null;

		MedicalInstitutionTO dto = new MedicalInstitutionTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setAddress(e.getAddress());
		dto.setWeb(e.getWeb());
		dto.setHours(e.getHours());
		return dto;
	}

	public List<MedicalInstitutionTO> mapMedicalInstitutions(List<MedicalInstitution> e) {
		List<MedicalInstitutionTO> list = new ArrayList<>();
		for (MedicalInstitution i : e) {
			list.add(mapMedicalInstitution(i));
		}
		return list;
	}

	public ScheduledVisitTO mapScheduledVisit(ScheduledVisit e) {
		if (e == null)
			return null;

		ScheduledVisitTO dto = new ScheduledVisitTO();
		dto.setId(e.getId());
		dto.setDate(e.getDate().toLocalDate());
		dto.setTime(e.getDate().toLocalTime());
		dto.setInstitution(mapMedicalInstitution(e.getInstitution()));
		dto.setPeriod(e.getPeriod());
		dto.setPurpose(e.getPurpose());
		dto.setRecord(mapMedicalRecord(e.getRecord()));
		dto.setPlanned(e.isPlanned());

		if (LocalDateTime.now().compareTo(e.getDate()) > 0) {
			dto.setState(ScheduledVisitState.MISSED);
		} else {
			dto.setState(e.isPlanned() ? ScheduledVisitState.PLANNED : ScheduledVisitState.TO_BE_PLANNED);
		}

		return dto;
	}

	public List<ScheduledVisitTO> mapScheduledVisits(List<ScheduledVisit> e) {
		List<ScheduledVisitTO> list = new ArrayList<>();
		for (ScheduledVisit i : e) {
			list.add(mapScheduledVisit(i));
		}
		return list;
	}

	public MedicalRecordTO mapMedicalRecord(MedicalRecord e) {
		if (e == null)
			return null;

		MedicalRecordTO dto = new MedicalRecordTO();
		dto.setId(e.getId());
		dto.setDate(e.getDate().toLocalDate());
		dto.setTime(e.getDate().toLocalTime());
		dto.setInstitution(mapMedicalInstitution(e.getInstitution()));
		dto.setRecord(e.getRecord());
		dto.setPhysician(mapPhysician(e.getPhysician()));
		dto.setMedicaments(mapMedicaments(e.getMedicaments()));
		return dto;
	}

	public List<MedicalRecordTO> mapMedicalRecords(List<MedicalRecord> e) {
		List<MedicalRecordTO> list = new ArrayList<>();
		for (MedicalRecord i : e) {
			list.add(mapMedicalRecord(i));
		}
		return list;
	}

	public MedicamentTO mapMedicament(Medicament e) {
		if (e == null)
			return null;

		MedicamentTO dto = new MedicamentTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		dto.setTolerance(e.getTolerance());
		return dto;
	}

	public Set<MedicamentTO> mapMedicaments(List<Medicament> e) {
		Set<MedicamentTO> set = new HashSet<>();
		for (Medicament i : e) {
			set.add(mapMedicament(i));
		}
		return set;
	}

	public PhysicianTO mapPhysician(Physician e) {
		if (e == null)
			return null;

		PhysicianTO dto = new PhysicianTO();
		dto.setId(e.getId());
		dto.setName(e.getName());
		return dto;
	}

	public Set<PhysicianTO> mapPhysicians(List<Physician> e) {
		Set<PhysicianTO> set = new HashSet<>();
		for (Physician i : e) {
			set.add(mapPhysician(i));
		}
		return set;
	}

}
