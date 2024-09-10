package cz.gattserver.grass.hw.model.repositories;

import java.util.List;

import com.querydsl.core.types.Order;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.hw.interfaces.QHWItemTypeTO;
import cz.gattserver.grass.hw.model.domain.QHWItem;
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
	public List<HWItemTypeTO> getHWItemTypes(HWItemTypeTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
		JPAQuery<HWItemTypeTO> query = new JPAQuery<>(entityManager);
		QHWItemType t = QHWItemType.hWItemType;
		QHWItem h = QHWItem.hWItem;
		query.offset(offset).limit(limit);
		query.from(h).join(h.types, t).where(createPredicateHWItemTypes(filter))
				.groupBy(t.id, t.name).select(new QHWItemTypeTO(t.id, t.name, h.id.count().intValue()));

		for (OrderSpecifier<?> os : order) {
			if ("name".equals(os.getTarget().toString()))
				query.orderBy(Order.ASC == os.getOrder() ? t.name.asc() : t.name.desc());
			if ("count".equals(os.getTarget().toString()))
				query.orderBy(Order.ASC == os.getOrder() ? h.id.count().asc() : h.id.count().desc());
		}
		return query.fetch();
	}

}
