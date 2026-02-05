package cz.gattserver.grass.medic.domain;

import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.medic.interfaces.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ScheduledVisitRepositoryCustomImpl extends QuerydslRepositorySupport
        implements ScheduledVisitRepositoryCustom {

    private final QScheduledVisit s = QScheduledVisit.scheduledVisit;
    private final QMedicalRecord r = QMedicalRecord.medicalRecord;
    private final QMedicalInstitution i = QMedicalInstitution.medicalInstitution;
    private final QMedicalInstitution ri = new QMedicalInstitution("record_institution");
    private final QPhysician rp = QPhysician.physician;

    public ScheduledVisitRepositoryCustomImpl() {
        super(ScheduledVisit.class);
    }

    @Override
    public List<ScheduledVisitOverviewTO> findByFilter(ScheduledVisitTO filterTO) {
        JPQLQuery<ScheduledVisit> query = from(s)
                // join medical institution
                .join(i).on(s.institutionId.eq(i.id))
                // join medical record
                .leftJoin(r).on(s.recordId.eq(r.id))
                // join record medical institution
                .leftJoin(ri).on(r.institutionId.eq(ri.id))
                // join record physician
                .leftJoin(rp).on(r.physicianId.eq(rp.id));
        if (filterTO != null) if (filterTO.getPlanned() != null) query.where(s.planned.eq(filterTO.getPlanned()));
        return query.orderBy(s.date.desc())
                .select(new QScheduledVisitOverviewTO(s.id, s.purpose, s.institutionId, i.name, s.planned, s.date,
                        s.period)).fetch();
    }

    @Override
    public ScheduledVisitTO findForDetailById(Long id) {
        return from(s)
                // join medical institution
                .join(i).on(s.institutionId.eq(i.id))
                // join medical record
                .leftJoin(r).on(s.recordId.eq(r.id))
                // join record medical institution
                .leftJoin(ri).on(r.institutionId.eq(ri.id))
                // join record physician
                .leftJoin(rp).on(r.physicianId.eq(rp.id))
                // where
                .where(s.id.eq(id))
                // select
                .select(new QScheduledVisitTO(s.id, s.purpose, s.institutionId, i.name, s.recordId, ri.id, rp.id,
                        ri.name, rp.name, s.planned, s.date, s.period)).orderBy(s.date.desc()).fetchOne();
    }
}