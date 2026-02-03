package cz.gattserver.grass.medic.domain;

import cz.gattserver.grass.medic.interfaces.PhysicianTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitOverviewTO;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

import java.util.List;

public interface ScheduledVisitRepositoryCustom {

	List<ScheduledVisitOverviewTO> findByFilter(ScheduledVisitTO filterTO);

    ScheduledVisitTO findForDetailById(Long id);
}
