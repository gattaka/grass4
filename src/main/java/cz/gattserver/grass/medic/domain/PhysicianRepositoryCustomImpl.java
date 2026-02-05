package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;

import cz.gattserver.grass.medic.interfaces.QPhysicianTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class PhysicianRepositoryCustomImpl extends QuerydslRepositorySupport implements PhysicianRepositoryCustom {

    private final QPhysician p = QPhysician.physician;
    private final QMedicalRecord r = QMedicalRecord.medicalRecord;

    public PhysicianRepositoryCustomImpl() {
        super(Physician.class);
    }

    private Predicate createPredicate(PhysicianTO filterTO) {
        PredicateBuilder builder = new PredicateBuilder();
        if (filterTO != null) builder.iLike(p.name, filterTO.getName());
        return builder.getBuilder();
    }

    @Override
    public List<PhysicianTO> findByFilter(PhysicianTO filterTO) {
        return from(p).where(createPredicate(filterTO)).select(new QPhysicianTO(p.id, p.name, p.email, p.phone))
                .orderBy(p.name.desc()).fetch();
    }

    @Override
    public PhysicianTO findPhysicianByLastVisit(Long institutionId) {
        return from(p)
                // join record
                .join(r).on(r.physicianId.eq(p.id))
                // where
                .where(r.institutionId.eq(institutionId)).select(new QPhysicianTO(p.id, p.name, p.email, p.phone))
                .orderBy(r.date.desc()).fetchFirst();
    }

    @Override
    public PhysicianTO findAndMapById(Long id) {
        return from(p)
                // where
                .where(p.id.eq(id)).select(new QPhysicianTO(p.id, p.name, p.email, p.phone)).fetchOne();
    }
}