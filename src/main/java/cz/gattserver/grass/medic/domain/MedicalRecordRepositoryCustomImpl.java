package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;

import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.QMedicalRecordTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

public class MedicalRecordRepositoryCustomImpl implements MedicalRecordRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private final QMedicalRecord r = QMedicalRecord.medicalRecord;
    private final QMedicalInstitution i = QMedicalInstitution.medicalInstitution;
    private final QPhysician p = QPhysician.physician;

    private Predicate createPredicate(MedicalRecordTO filterTO) {
        PredicateBuilder builder = new PredicateBuilder();
        if (filterTO != null) {
            builder.iLike(r.record, filterTO.getRecord());
            builder.iLike(i.name, filterTO.getInstitutionName());
            if (filterTO.getDateTime() != null) {
                builder.eq(r.date.year(), filterTO.getDateTime().getYear());
                builder.eq(r.date.month(), filterTO.getDateTime().getMonthValue());
                builder.eq(r.date.dayOfMonth(), filterTO.getDateTime().getDayOfMonth());
            }
            builder.iLike(p.name, filterTO.getPhysicianName());
        }
        return builder.getBuilder();
    }

    private JPAQuery<MedicalRecordTO> createQuery() {
        JPAQuery<MedicalRecordTO> query = new JPAQuery<>(entityManager);
        return query.from(r)
                // join institution
                .join(i).on(r.institutionId.eq(i.id))
                // join physician
                .join(p).on(r.physicianId.eq(p.id))
                // select
                .select(new QMedicalRecordTO(r.id, r.institutionId, i.name, r.physicianId, p.name, r.date, r.record));
    }

    @Override
    public List<MedicalRecordTO> findByFilter(MedicalRecordTO filterTO) {
        return createQuery().where(createPredicate(filterTO)).orderBy(r.date.desc()).fetch();
    }

    @Override
    public MedicalRecordTO findAndMapById(Long id) {
        return createQuery().where(r.id.eq(id)).fetchOne();
    }
}