package cz.gattserver.grass.hw.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.querydsl.core.types.Order;
import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.hw.interfaces.HWTypeTokenTO;
import cz.gattserver.grass.hw.interfaces.HWTypeTO;
import cz.gattserver.grass.hw.interfaces.QHWTypeTO;
import cz.gattserver.grass.hw.interfaces.QHWTypeBasicTO;

import cz.gattserver.grass.core.model.util.PredicateBuilder;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

public class HWTypeRepositoryCustomImpl extends QuerydslRepositorySupport implements HWTypeRepositoryCustom {

    private final QHWType t = QHWType.hWType;
    private final QHWItem i = QHWItem.hWItem;
    private final QHWItemType it = QHWItemType.hWItemType;

    public HWTypeRepositoryCustomImpl() {
        super(HWType.class);
    }

    private Predicate createPredicate(HWTypeTO filter) {
        PredicateBuilder builder = new PredicateBuilder();
        builder.anyILike(t.name, filter.getName());
        return builder.getBuilder();
    }

    private JPQLQuery<HWType> createGroupQuery() {
        return from(t).leftJoin(it).on(it.id.hwTypeId.eq(t.id)).groupBy(t.id, t.name);
    }

    private JPQLQuery<HWTypeTO> createGroupSelectQuery() {
        return createGroupQuery().select(new QHWTypeTO(t.id, t.name, it.id.hwItemId.count().intValue()));
    }

    @Override
    public long countHWTypes(HWTypeTO filter) {
        return from(t).where(createPredicate(filter)).fetchCount();
    }

    @Override
    public Set<HWTypeTokenTO> findOrderByName() {
        return new LinkedHashSet<>(
                createGroupQuery().select(new QHWTypeBasicTO(t.id, t.name)).orderBy(t.name.asc()).fetch());
    }

    @Override
    public HWTypeTO findByIdAndMap(Long id) {
        return createGroupSelectQuery().where(t.id.eq(id)).fetchOne();
    }

    @Override
    public Set<HWTypeTokenTO> findByItemId(Long itemId) {
        return new LinkedHashSet<>(
                createGroupQuery().where(it.id.hwItemId.eq(itemId)).select(new QHWTypeBasicTO(t.id, t.name)).fetch());
    }

    @Override
    public Set<Long> findTypeIdsByItemId(Long itemId) {
        return new LinkedHashSet<>(
                createGroupQuery().where(it.id.hwItemId.eq(itemId)).select(t.id).fetch());
    }

    @Override
    public List<Long> findHWTypeIds(HWTypeTO filterTO, OrderSpecifier<?>[] order) {
        return from(t).where(createPredicate(filterTO)).orderBy(order).select(t.id).fetch();
    }

    @Override
    public List<HWTypeTO> getHWTypes(HWTypeTO filterTO, int offset, int limit, OrderSpecifier<?>[] order) {
        JPQLQuery<HWTypeTO> query = createGroupSelectQuery();

        for (OrderSpecifier<?> os : order) {
            if ("name".equals(os.getTarget().toString()))
                query.orderBy(Order.ASC == os.getOrder() ? t.name.asc() : t.name.desc());
            if ("count".equals(os.getTarget().toString()))
                query.orderBy(Order.ASC == os.getOrder() ? i.id.count().asc() : i.id.count().desc());
        }
        return query.where(createPredicate(filterTO)).offset(offset).limit(limit).fetch();
    }
}