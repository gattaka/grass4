package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public class PhysicianRepositoryCustomImpl implements PhysicianRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicate(PhysicianTO filterTO) {
		QPhysician p = QPhysician.physician;
		PredicateBuilder builder = new PredicateBuilder();
		if (filterTO != null) {
			builder.iLike(p.name, filterTO.getName());
		}
		return builder.getBuilder();
	}

	@Override
	public List<Physician> findList(PhysicianTO filterTO) {
		JPAQuery<Physician> query = new JPAQuery<>(entityManager);
		QPhysician p = QPhysician.physician;
		return query.select(p).from(p).where(createPredicate(filterTO))
				.orderBy(new OrderSpecifier[] { new OrderSpecifier<>(Order.DESC, p.name) }).fetch();
	}

	@Override
	public Physician findPhysicianByLastVisit(Long institutionId) {
		JPAQuery<Physician> query = new JPAQuery<>(entityManager);
		QPhysician p = QPhysician.physician;
		QMedicalRecord r = QMedicalRecord.medicalRecord;
		return query.select(p).from(r).join(p).on(r.physician.id.eq(p.id)).where(r.institution.id.eq(institutionId))
				.orderBy(new OrderSpecifier[] { new OrderSpecifier<>(Order.DESC, r.date) }).fetchOne();
	}
}