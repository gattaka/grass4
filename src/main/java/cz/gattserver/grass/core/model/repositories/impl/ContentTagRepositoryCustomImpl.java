package cz.gattserver.grass.core.model.repositories.impl;

import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.JPQLQuery;

import cz.gattserver.grass.core.model.domain.ContentTag;
import cz.gattserver.grass.core.model.domain.QContentTag;
import cz.gattserver.grass.core.model.repositories.ContentTagRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class ContentTagRepositoryCustomImpl extends QuerydslRepositorySupport implements ContentTagRepositoryCustom {

    private static final QContentTag c = QContentTag.contentTag;

    public ContentTagRepositoryCustomImpl() {
        super(ContentTag.class);
    }

    @Override
    public int countContentTagContents(Long id) {
        return from(c).select(c.contentNodeCount).from(c).where(c.id.eq(id)).fetchOne();
    }

    private JPQLQuery<String> createQuery(Optional<String> filterOptional) {
        JPQLQuery<String> query = from(c).select(c.name);
        filterOptional.ifPresent(filter -> query.where(c.name.lower().like("%" + filter.toLowerCase() + "%")));
        return query;
    }

    @Override
    public List<String> findByFilter(Optional<String> filterOptional, int offset, int limit) {
        return createQuery(filterOptional).offset(offset).limit(limit).fetch();
    }

    @Override
    public Integer countByFilter(Optional<String> filterOptional) {
        return Math.toIntExact(createQuery(filterOptional).fetchCount());
    }

    @Override
    public List<Integer> findContentNodesCountsGroups() {
        return from(c).select(c.contentNodeCount).groupBy(c.contentNodeCount).orderBy(c.contentNodeCount.asc()).fetch();
    }

    @Override
    public List<ContentTag> findAllOrderByContentCountNode() {
        return from(c).select(c).orderBy(c.contentNodeCount.asc()).fetch();
    }
}