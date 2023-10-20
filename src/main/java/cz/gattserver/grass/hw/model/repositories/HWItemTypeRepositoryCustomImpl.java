package cz.gattserver.grass.hw.model.repositories;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import cz.gattserver.grass.core.model.util.PredicateBuilder;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass.hw.model.domain.HWItemType;
import cz.gattserver.grass.hw.model.domain.QHWItemType;

@Repository
public class HWItemTypeRepositoryCustomImpl implements HWItemTypeRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateHWItemTypes(HWItemTypeTO filter) {
		QHWItemType t = QHWItemType.hWItemType;
		PredicateBuilder builder = new PredicateBuilder();
		builder.anyILike(t.name, filter.getName());
		return builder.getBuilder();
	}

	@Override
	public long countHWItemTypes(HWItemTypeTO filter) {
		JPAQuery<HWItemType> query = new JPAQuery<>(entityManager);
		QHWItemType t = QHWItemType.hWItemType;
		return query.from(t).where(createPredicateHWItemTypes(filter)).fetchCount();
	}

	@Override
	public List<HWItemType> getHWItemTypes(HWItemTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
		JPAQuery<HWItemType> query = new JPAQuery<>(entityManager);
		QHWItemType t = QHWItemType.hWItemType;
		query.offset(offset).limit(limit);
		return query.from(t).where(createPredicateHWItemTypes(filter)).orderBy(order).fetch();
	}

}
