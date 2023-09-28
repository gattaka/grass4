package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;
import cz.gattserver.grass.medic.interfaces.MedicalRecordTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class MedicalRecordRepositoryCustomImpl implements MedicalRecordRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicate(MedicalRecordTO filterTO) {
		QMedicalRecord r = QMedicalRecord.medicalRecord;
		PredicateBuilder builder = new PredicateBuilder();
		if (filterTO != null) {
			builder.iLike(r.record, filterTO.getRecord());
			builder.iLike(r.institution.name, filterTO.getInstitutionName());
			if (filterTO.getDate() != null) {
				builder.eq(r.date.year(), filterTO.getDate().getYear());
				builder.eq(r.date.month(), filterTO.getDate().getMonthValue());
				builder.eq(r.date.dayOfMonth(), filterTO.getDate().getDayOfMonth());
			}
			builder.iLike(r.physician.name, filterTO.getPhysicianName());
		}
		return builder.getBuilder();
	}

	@Override
	public List<MedicalRecord> findList(MedicalRecordTO filterTO) {
		JPAQuery<MedicalRecord> query = new JPAQuery<>(entityManager);
		QMedicalRecord i = QMedicalRecord.medicalRecord;
		return query.select(i).from(i).where(createPredicate(filterTO))
				.orderBy(new OrderSpecifier[] { new OrderSpecifier<>(Order.DESC, i.date) }).fetch();
	}
}