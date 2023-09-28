package cz.gattserver.grass.medic.domain;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.books.model.domain.Book;
import cz.gattserver.grass.books.model.domain.QBook;
import cz.gattserver.grass.books.model.interfaces.BookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.BookTO;
import cz.gattserver.grass.books.model.interfaces.QBookOverviewTO;
import cz.gattserver.grass.books.model.interfaces.QBookTO;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.medic.interfaces.MedicalInstitutionTO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class MedicalInstitutionRepositoryCustomImpl implements MedicalInstitutionRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicate(MedicalInstitutionTO filterTO) {
		QMedicalInstitution i = QMedicalInstitution.medicalInstitution;
		PredicateBuilder builder = new PredicateBuilder();
		if (filterTO != null) {
			builder.iLike(i.address, filterTO.getAddress());
			builder.iLike(i.name, filterTO.getName());
			builder.iLike(i.web, filterTO.getWeb());
			builder.iLike(i.hours, filterTO.getHours());
		}
		return builder.getBuilder();
	}

	@Override
	public List<MedicalInstitution> findList(MedicalInstitutionTO filterTO) {
		JPAQuery<MedicalInstitution> query = new JPAQuery<>(entityManager);
		QMedicalInstitution i = QMedicalInstitution.medicalInstitution;
		return query.select(i).from(i).where(createPredicate(filterTO))
				.orderBy(new OrderSpecifier[] { new OrderSpecifier<>(Order.DESC, i.name) }).fetch();
	}
}