package cz.gattserver.grass.hw.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.hw.interfaces.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

public class HWItemRepositoryCustomImpl extends QuerydslRepositorySupport implements HWItemRepositoryCustom {

    private final QHWItem h = QHWItem.hWItem;
    private final QHWItem u = new QHWItem("used_id");
    private final QHWType t = QHWType.hWType;

    public HWItemRepositoryCustomImpl() {
        super(HWItem.class);
    }

    private Predicate createPredicate(HWFilterTO filter) {
        PredicateBuilder builder = new PredicateBuilder();
        builder.anyILike(h.name, filter.getName());
        builder.eq(h.state, filter.getState());
        builder.eq(h.usedInId, filter.getUsedInId());
        builder.iLike(u.name, filter.getUsedInName());
        builder.iLike(h.supervizedFor, filter.getSupervizedFor());
        builder.eq(h.price, filter.getPrice());
        builder.between(h.purchaseDate, filter.getPurchaseDateFrom(), filter.getPurchaseDateTo());
        builder.ne(h.id, filter.getIgnoreId());
        if (Boolean.TRUE == filter.getPublicItem()) builder.eq(h.publicItem, true);
        if (filter.getTypes() != null) for (String type : filter.getTypes()) {
            JPAQuery<HWType> subQuery = new JPAQuery<>();
            // TODO
//            subQuery.from(t).where(t.name.eq(type), h.types.contains(t));
            builder.exists(subQuery);
        }
        return builder.getBuilder();
    }

    @Override
    public long countHWItems(HWFilterTO filter) {
        return from(h).where(createPredicate(filter)).fetchCount();
    }

    @Override
    public HWItemTO findByIdAndMapForDetail(Long id) {
        return from(h).leftJoin(u).on(u.id.eq(h.usedInId))
                .select(new QHWItemTO(h.id, h.name, h.purchaseDate, h.price, h.state, h.usedInId, u.name,
                        h.supervizedFor, h.publicItem, h.warrantyYears, h.description)).fetchOne();
    }

    @Override
    public HWItemOverviewTO findByIdAndMap(Long id) {
        return from(h).leftJoin(u).on(u.id.eq(h.usedInId))
                .select(new QHWItemOverviewTO(h.id, h.name, h.state, u.name, h.supervizedFor, h.price, h.purchaseDate,
                        h.publicItem)).fetchOne();
    }

    @Override
    public List<HWItemOverviewTO> findAndMap(HWFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
        return from(h).leftJoin(u).on(h.usedInId.eq(u.id)).where(createPredicate(filter))
                .select(new QHWItemOverviewTO(h.id, h.name, h.state, u.name, h.supervizedFor, h.price, h.purchaseDate,
                        h.publicItem)).offset(offset).limit(limit).orderBy(order).fetch();
    }

    @Override
    public List<Long> getHWItemIds(HWFilterTO filter, OrderSpecifier<?>[] order) {
        return from(h).where(createPredicate(filter)).orderBy(order).select(h.id).fetch();
    }

    @Override
    public List<HWItemOverviewTO> findByTypesId(Long id) {
        return List.of();
    }

    @Override
    public List<HWItemOverviewTO> findByUsedInId(Long id) {
        return List.of();
    }

    @Override
    public List<HWItemOverviewTO> getHWItemsByTypes(Collection<String> types) {
        return List.of();
    }

    @Override
    public List<HWItemOverviewTO> findAllExcept(Long itemId) {
        return List.of();
    }

    @Override
    public List<HWItemOverviewTO> findAndMap() {
        return from(h).leftJoin(u).on(h.usedInId.eq(u.id))
                .select(new QHWItemOverviewTO(h.id, h.name, h.state, u.name, h.supervizedFor, h.price, h.purchaseDate,
                        h.publicItem)).orderBy(h.name.asc()).fetch();
    }
}