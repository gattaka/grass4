package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;
import cz.gattserver.grass.medic.interfaces.PhysicianTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
}