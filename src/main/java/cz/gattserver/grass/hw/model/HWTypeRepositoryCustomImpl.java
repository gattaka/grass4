package cz.gattserver.grass.hw.model;

import java.util.List;

import com.querydsl.core.types.Order;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.interfaces.QHWTypeTO;
import cz.gattserver.grass.hw.model.domain.QHWItem;
import cz.gattserver.grass.hw.model.domain.QHWType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import cz.gattserver.grass.core.model.util.PredicateBuilder;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

@Repository
public class HWTypeRepositoryCustomImpl implements HWTypeRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateHWItemTypes(HWTypeTO filter) {
		QHWType t = QHWType.hWType;
		PredicateBuilder builder = new PredicateBuilder();
		builder.anyILike(t.name, filter.getName());
		return builder.getBuilder();
	}

	@Override
	public long countHWItemTypes(HWTypeTO filter) {
		JPAQuery<HWType> query = new JPAQuery<>(entityManager);
		QHWType t = QHWType.hWType;
		return query.from(t).where(createPredicateHWItemTypes(filter)).fetchCount();
	}

	@Override
	public List<HWTypeTO> getHWItemTypes(HWTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
		JPAQuery<HWTypeTO> query = new JPAQuery<>(entityManager);
		QHWType t = QHWType.hWType;
		QHWItem h = QHWItem.hWItem;
		query.offset(offset).limit(limit);
		query.from(h).join(h.types, t).where(createPredicateHWItemTypes(filter))
				.groupBy(t.id, t.name).select(new QHWTypeTO(t.id, t.name, h.id.count().intValue()));

		for (OrderSpecifier<?> os : order) {
			if ("name".equals(os.getTarget().toString()))
				query.orderBy(Order.ASC == os.getOrder() ? t.name.asc() : t.name.desc());
			if ("count".equals(os.getTarget().toString()))
				query.orderBy(Order.ASC == os.getOrder() ? h.id.count().asc() : h.id.count().desc());
		}
		return query.fetch();
	}

}
