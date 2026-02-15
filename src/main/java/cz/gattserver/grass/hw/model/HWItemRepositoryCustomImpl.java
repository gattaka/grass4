package cz.gattserver.grass.hw.model;

import java.util.Collection;
import java.util.List;

import com.querydsl.core.types.Order;
import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.hw.interfaces.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

public class HWItemRepositoryCustomImpl extends QuerydslRepositorySupport implements HWItemRepositoryCustom {

    private final QHWItem h = QHWItem.hWItem;
    private final QHWItem u = new QHWItem("used_in");
    private final QHWType t = QHWType.hWType;
    private final QHWItemType it = QHWItemType.hWItemType;

    public HWItemRepositoryCustomImpl() {
        super(HWItem.class);
    }

    private Predicate createPredicate(HWFilterTO filter, JPQLQuery<?> query) {
        PredicateBuilder builder = new PredicateBuilder();
        if (filter != null) {
            builder.anyILike(h.name, filter.getName());
            builder.eq(h.state, filter.getState());
            builder.eq(h.usedInId, filter.getUsedInId());
            if (StringUtils.isNotBlank(filter.getUsedInName())) {
                query.join(u).on(u.id.eq(h.usedInId));
                builder.iLike(u.name, filter.getUsedInName());
            }
            builder.ne(h.id, filter.getIgnoreId());
            builder.iLike(h.supervizedFor, filter.getSupervizedFor());
            builder.eq(h.price, filter.getPrice());
            builder.between(h.purchaseDate, filter.getPurchaseDateFrom(), filter.getPurchaseDateTo());
            builder.ne(h.id, filter.getIgnoreId());
            if (Boolean.TRUE == filter.getPublicItem()) builder.eq(h.publicItem, true);
            if (filter.getTypes() != null && !filter.getTypes().isEmpty()) {
                for (String type : filter.getTypes()) {
                    query.where(new JPAQuery<>().from(it).join(t).on(it.id.hwTypeId.eq(t.id))
                            .where(it.id.hwItemId.eq(h.id), t.name.eq(type)).exists());
                }
            }
        }
        return builder.getBuilder();
    }

    @Override
    public long countHWItems(HWFilterTO filter) {
        JPQLQuery<HWItem> query = from(h);
        return query.where(createPredicate(filter, query)).fetchCount();
    }

    @Override
    public HWItemTO findByIdAndMapForDetail(Long id) {
        return from(h).leftJoin(u).on(u.id.eq(h.usedInId)).where(h.id.eq(id))
                .select(new QHWItemTO(h.id, h.name, h.purchaseDate, h.price, h.state, h.usedInId, u.name,
                        h.supervizedFor, h.publicItem, h.warrantyYears, h.description)).fetchOne();
    }

    @Override
    public HWItemOverviewTO findByIdAndMap(Long id) {
        return from(h).leftJoin(u).on(u.id.eq(h.usedInId)).where(h.id.eq(id))
                .select(new QHWItemOverviewTO(h.id, h.name, h.state, u.name, h.supervizedFor, h.price, h.purchaseDate,
                        h.publicItem)).fetchOne();
    }

    @Override
    public List<HWItemOverviewTO> findAndMap(HWFilterTO filter, Integer offset, Integer limit,
                                             OrderSpecifier<?>[] order) {
        JPQLQuery<HWItemOverviewTO> query = from(h).leftJoin(u).on(h.usedInId.eq(u.id))
                .select(new QHWItemOverviewTO(h.id, h.name, h.state, u.name, h.supervizedFor, h.price, h.purchaseDate,
                        h.publicItem));
        if (offset != null && limit != null) query.offset(offset).limit(limit);
        query.where(createPredicate(filter, query));
        if (order != null) {
            for (OrderSpecifier<?> os : order) {
                if ("name".equals(os.getTarget().toString()))
                    query.orderBy(Order.ASC == os.getOrder() ? h.name.asc() : h.name.desc());
            }
        }
        return query.fetch();
    }

    @Override
    public List<Long> getHWItemIds(HWFilterTO filter, OrderSpecifier<?>[] order) {
        JPQLQuery<HWItem> query = from(h);
        if (order != null) {
            for (OrderSpecifier<?> os : order) {
                if ("name".equals(os.getTarget().toString()))
                    query.orderBy(Order.ASC == os.getOrder() ? h.name.asc() : h.name.desc());
            }
        }
        return query.where(createPredicate(filter, query)).select(h.id).fetch();
    }

}