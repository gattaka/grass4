package cz.gattserver.grass.core.model.repositories.impl;

import java.util.List;

import com.querydsl.jpa.JPQLQuery;

import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.interfaces.QContentTagTO;
import cz.gattserver.grass.core.model.domain.ContentTag;
import cz.gattserver.grass.core.model.domain.QContentNode;
import cz.gattserver.grass.core.model.domain.QContentNodeContentTag;
import cz.gattserver.grass.core.model.domain.QContentTag;
import cz.gattserver.grass.core.model.repositories.ContentTagRepositoryCustom;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class ContentTagRepositoryCustomImpl extends QuerydslRepositorySupport implements ContentTagRepositoryCustom {

    private static final QContentNode c = QContentNode.contentNode;
    private static final QContentNodeContentTag ct = QContentNodeContentTag.contentNodeContentTag;
    private static final QContentTag t = QContentTag.contentTag;

    public ContentTagRepositoryCustomImpl() {
        super(ContentTag.class);
    }

    private JPQLQuery<String> createBaseQuery(boolean admin) {
        JPQLQuery<String> query = from(t).select(t.name);
        // Tag je veřejný, pokud existuje aspoň jeden jeho obsah, který je veřejný
        if (!admin) query.where(from(ct).join(c).on(ct.id.contentTagId.eq(t.id), ct.id.contentNodeId.eq(c.id))
                .where(c.hidden.isFalse(), c.hiddenByParent.isFalse()).exists());
        return query;
    }

    private JPQLQuery<String> createFilteredQuery(@Nullable String filter, boolean admin) {
        JPQLQuery<String> query = createBaseQuery(admin);
        if (filter != null) query.where(t.name.lower().like("%" + filter.toLowerCase() + "%"));
        return query;
    }

    @Override
    public int countContentTagContents(Long id, boolean admin) {
        Integer count = createBaseQuery(admin).select(t.contentNodeCount).from(t).where(t.id.eq(id)).fetchOne();
        if (count != null) return count;
        else return 0;
    }

    @Override
    public List<String> findByFilter(@Nullable String filter, boolean admin, int offset, int limit) {
        return createFilteredQuery(filter, admin).offset(offset).limit(limit).fetch();
    }

    @Override
    public Integer countByFilter(@Nullable String filter, boolean admin) {
        return Math.toIntExact(createFilteredQuery(filter, admin).fetchCount());
    }

    @Override
    public List<Integer> findContentNodesCountsGroups(boolean admin) {
        return createBaseQuery(admin).select(t.contentNodeCount).groupBy(t.contentNodeCount)
                .orderBy(t.contentNodeCount.asc()).fetch();
    }

    @Override
    public List<ContentTagTO> findAllOrderByContentCountNode(boolean admin) {
        return createBaseQuery(admin).select(new QContentTagTO(t.id, t.name, t.contentNodeCount))
                .orderBy(t.contentNodeCount.asc()).fetch();
    }

    @Override
    public List<ContentTagTO> findAllOrderByNameCaseInsensitive(boolean admin) {
        return createBaseQuery(admin).select(new QContentTagTO(t.id, t.name, t.contentNodeCount))
                .orderBy(t.name.toUpperCase().asc()).fetch();
    }

    @Override
    public ContentTagTO findAndMapById(Long id, boolean admin) {
        return createBaseQuery(admin).select(new QContentTagTO(t.id, t.name, t.contentNodeCount)).where(t.id.eq(id))
                .fetchOne();
    }

    @Override
    public ContentTagTO findAndMapByName(String name, boolean admin) {
        return createBaseQuery(admin).select(new QContentTagTO(t.id, t.name, t.contentNodeCount)).where(t.name.eq(name))
                .fetchOne();
    }
}