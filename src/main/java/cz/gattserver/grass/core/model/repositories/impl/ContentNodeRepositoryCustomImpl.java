package cz.gattserver.grass.core.model.repositories.impl;

import java.util.LinkedHashSet;
import java.util.List;

import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.core.interfaces.*;
import cz.gattserver.grass.core.model.domain.*;

import cz.gattserver.grass.core.model.repositories.ContentNodeRepositoryCustom;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

import cz.gattserver.grass.core.model.util.QuerydslUtil;

@Repository
public class ContentNodeRepositoryCustomImpl extends QuerydslRepositorySupport implements ContentNodeRepositoryCustom {

    private final QNode n = QNode.node;
    private final QContentNode c = QContentNode.contentNode;
    private final QContentTag t = QContentTag.contentTag;
    private final QContentNodeContentTag ct = QContentNodeContentTag.contentNodeContentTag;
    private final QUser u = QUser.user;

    public ContentNodeRepositoryCustomImpl() {
        super(ContentNode.class);
    }

    private Predicate createBasicNodePredicate(ContentNodeFilterTO filter, Long userId, boolean admin) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(ExpressionUtils.anyOf(c.draft.isFalse(), c.draft.isNull()));
        if (!admin) {
            if (userId == null) {
                builder.and(c.hidden.isFalse().and(c.hiddenByParent.isFalse()));
            } else {
                builder.andAnyOf(c.authorId.eq(userId), c.hidden.isFalse().and(c.hiddenByParent.isFalse()));
            }
        }
        if (filter.getParentNodeId() != null) builder.and(n.id.eq(filter.getParentNodeId()));
        if (StringUtils.isNotBlank(filter.getName())) {
            String filterName = QuerydslUtil.transformSimpleLikeFilter(filter.getName()).toLowerCase();
            JPQLQuery<ContentTag> query = from(t).join(ct).on(ct.id.contentTagId.eq(t.id), ct.id.contentNodeId.eq(c.id))
                    .where(t.name.toLowerCase().like(filterName));
            builder.andAnyOf(c.name.toLowerCase().like(filterName), query.exists());
        }
        if (StringUtils.isNotBlank(filter.getContentReaderID()))
            builder.and(c.contentReaderId.eq(filter.getContentReaderID()));

        return builder.getValue();
    }

    @Override
    public QueryResults<ContentNodeOverviewTO> findByTagAndUserAccess(Long tagId, Long userId, boolean admin,
                                                                      int offset, int limit) {
        JPQLQuery<ContentNode> query = from(c);
        QuerydslUtil.applyPagination(offset, limit, query);

        return query.from(c)
                // tag join
                .join(ct).on(ct.id.contentNodeId.eq(c.id), ct.id.contentTagId.eq(tagId))
                // node join
                .join(n).on(c.parentId.eq(n.id))
                // user join
                .join(u).on(c.authorId.eq(u.id))
                // select
                .select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate,
                        c.lastModificationDate, c.hidden, c.hiddenByParent, u.name, u.id, c.id))
                // where
                .where(createBasicNodePredicate(new ContentNodeFilterTO(), userId, admin))
                // order by
                .orderBy(new OrderSpecifier<>(Order.DESC, c.creationDate)).fetchResults();
    }

    @Override
    public QueryResults<ContentNodeOverviewTO> findByUserFavouritesAndUserAccess(Long favouritesUserId, Long userId,
                                                                                 boolean admin, int offset, int limit) {
        JPQLQuery<ContentNode> query = from(c);
        QUser uf = new QUser("favOwnerUser");
        QuerydslUtil.applyPagination(offset, limit, query);

        return query.from(c)
                // favourites join
                .join(uf).on(uf.favourites.contains(c))
                // node join
                .join(n).on(c.parentId.eq(n.id))
                // user join
                .join(u).on(c.authorId.eq(u.id))
                // select
                .select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate,
                        c.lastModificationDate, c.hidden, c.hiddenByParent, u.name, u.id, c.id))
                // where
                .where(createBasicNodePredicate(new ContentNodeFilterTO(), userId, admin), uf.id.eq(favouritesUserId))
                // order by
                .orderBy(new OrderSpecifier<>(Order.DESC, c.creationDate)).fetchResults();
    }

    /*
     * ByFilterAndUserAccess
     */

    private JPQLQuery<ContentNodeOverviewTO> queryByFilterAndUserAccess(ContentNodeFilterTO filter, Long userId,
                                                                        boolean admin) {
        return from(c)
                // node join
                .join(n).on(c.parentId.eq(n.id))
                // user join
                .join(u).on(c.authorId.eq(u.id))
                // select
                .select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate,
                        c.lastModificationDate, c.hidden, c.hiddenByParent, u.name, u.id, c.id))
                // where
                .where(createBasicNodePredicate(filter, userId, admin));
    }

    @Override
    public long countByFilterAndUserAccess(ContentNodeFilterTO filter, Long userId, boolean admin) {
        JPQLQuery<ContentNodeOverviewTO> query = queryByFilterAndUserAccess(filter, userId, admin);
        return query.distinct().stream().count();
    }

    @Override
    public List<ContentNodeOverviewTO> findByFilterAndUserAccess(ContentNodeFilterTO filter, Long userId, boolean admin,
                                                                 int offset, int limit, String sortProperty) {
        JPQLQuery<ContentNodeOverviewTO> query = queryByFilterAndUserAccess(filter, userId, admin);
        QuerydslUtil.applyPagination(offset, limit, query);
        if (sortProperty != null) {
            query = query.orderBy(QuerydslUtil.transformOrder(false, sortProperty));
        } else {
            query = query.orderBy(new OrderSpecifier<>(Order.DESC, c.creationDate));
        }
        return query.select(
                        new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate,
                                c.lastModificationDate, c.hidden, c.hiddenByParent, u.name, u.id, c.id))
                .groupBy(c.contentReaderId, c.contentId, c.name, n.name, n.id, c.creationDate, c.lastModificationDate,
                        c.hidden, u.name, u.id, c.id).fetch();
    }

    @Override
    public List<String> findTagsByContentId(Long contentNodeId) {
        return from(ct).join(t).on(t.id.eq(ct.id.contentTagId)).where(ct.id.contentNodeId.eq(contentNodeId))
                .select(t.name).fetch();
    }

    @Override
    public ContentNodeTO findByIdForDetail(Long contentNodeId) {
        ContentNodeTO to = from(c)
                // node join
                .join(n).on(c.parentId.eq(n.id))
                // user join
                .join(u).on(c.authorId.eq(u.id))
                // where
                .where(c.id.eq(contentNodeId))
                // select
                .select(new QContentNodeTO(c.contentReaderId, c.id, c.contentId, c.name, c.parentId, n.name,
                        c.creationDate, c.lastModificationDate, c.hidden, c.hiddenByParent, c.authorId, u.name, c.draft,
                        c.draftSourceId)).fetchFirst();
        if (to == null) return null;

        to.setContentTags(new LinkedHashSet<>(
                from(ct).join(t).on(t.id.eq(ct.id.contentTagId)).where(ct.id.contentNodeId.eq(to.getId()))
                        .select(new QContentTagTO(t.id, t.name, t.contentNodeCount)).fetch()));

        return to;
    }
}