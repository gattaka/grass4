package cz.gattserver.grass.medic.domain;

import cz.gattserver.grass.medic.interfaces.PhysicianTO;

import java.util.List;

public interface PhysicianRepositoryCustom {

	List<PhysicianTO> findByFilter(PhysicianTO filterTO);

    PhysicianTO findPhysicianByLastVisit(Long institutionId);

    PhysicianTO findAndMapById(Long id);
}