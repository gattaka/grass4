package cz.gattserver.grass.medic.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass.medic.domain.ScheduledVisit;

public interface ScheduledVisitRepository extends JpaRepository<ScheduledVisit, Long>, ScheduledVisitRepositoryCustom {

}