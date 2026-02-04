package cz.gattserver.grass.medic.domain;

import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.medic.interfaces.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class ScheduledVisitRepositoryCustomImpl implements ScheduledVisitRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ScheduledVisitOverviewTO> findByFilter(ScheduledVisitTO filterTO) {
        JPAQuery<ScheduledVisit> query = new JPAQuery<>(entityManager);
        QScheduledVisit s = QScheduledVisit.scheduledVisit;
        QMedicalRecord r = QMedicalRecord.medicalRecord;
        QMedicalInstitution i = QMedicalInstitution.medicalInstitution;
        QMedicalInstitution ri = new QMedicalInstitution("record_institution");
        QPhysician rp = QPhysician.physician;
        query.from(s)
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
        JPAQuery<ScheduledVisit> query = new JPAQuery<>(entityManager);
        QScheduledVisit s = QScheduledVisit.scheduledVisit;
        QMedicalRecord r = QMedicalRecord.medicalRecord;
        QMedicalInstitution i = QMedicalInstitution.medicalInstitution;
        QMedicalInstitution ri = new QMedicalInstitution("record_institution");
        QPhysician rp = QPhysician.physician;
        query.from(s)
                // join medical institution
                .join(i).on(s.institutionId.eq(i.id))
                // join medical record
                .leftJoin(r).on(s.recordId.eq(r.id))
                // join record medical institution
                .leftJoin(ri).on(r.institutionId.eq(ri.id))
                // join record physician
                .leftJoin(rp).on(r.physicianId.eq(rp.id));
        return query.where(s.id.eq(id))
                .select(new QScheduledVisitTO(s.id, s.purpose, s.institutionId, i.name, s.recordId, ri.id, rp.id,
                        ri.name, rp.name, s.planned, s.date, s.period)).orderBy(s.date.desc()).fetchOne();
    }
}