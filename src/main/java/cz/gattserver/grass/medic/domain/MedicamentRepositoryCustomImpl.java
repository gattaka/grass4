package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicamentTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class MedicamentRepositoryCustomImpl implements MedicamentRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicate(MedicamentTO filterTO) {
		QMedicament m = QMedicament.medicament;
		PredicateBuilder builder = new PredicateBuilder();
		if (filterTO != null) {
			builder.iLike(m.name, filterTO.getName());
			builder.iLike(m.tolerance, filterTO.getTolerance());
		}
		return builder.getBuilder();
	}

	@Override
	public List<Medicament> findList(MedicamentTO filterTO) {
		JPAQuery<Medicament> query = new JPAQuery<>(entityManager);
		QMedicament i = QMedicament.medicament;
		return query.select(i).from(i).where(createPredicate(filterTO))
				.orderBy(new OrderSpecifier[] { new OrderSpecifier<>(Order.DESC, i.name) }).fetch();
	}
}