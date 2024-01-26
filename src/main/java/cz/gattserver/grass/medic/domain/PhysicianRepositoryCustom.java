package cz.gattserver.grass.medic.domain;

import cz.gattserver.grass.medic.interfaces.PhysicianTO;

import java.util.List;

public interface PhysicianRepositoryCustom {

	List<Physician> findList(PhysicianTO filterTO);

	Physician findPhysicianByLastVisit(Long institutionId);
}
