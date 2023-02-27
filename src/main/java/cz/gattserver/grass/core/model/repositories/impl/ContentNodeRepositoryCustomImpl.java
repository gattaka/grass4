package cz.gattserver.grass.core.model.repositories.impl;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.interfaces.QContentNodeOverviewTO;
import cz.gattserver.grass.core.model.repositories.ContentNodeRepositoryCustom;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.model.domain.QContentNode;
import cz.gattserver.grass.core.model.domain.QContentTag;
import cz.gattserver.grass.core.model.domain.QNode;
import cz.gattserver.grass.core.model.domain.QUser;
import cz.gattserver.grass.core.model.util.QuerydslUtil;

@Repository
public class ContentNodeRepositoryCustomImpl implements ContentNodeRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createBasicNodePredicate(ContentNodeFilterTO filter, Long userId, boolean admin) {
		QContentNode c = QContentNode.contentNode;
		QNode n = QNode.node;
		QUser u = QUser.user;
		QContentTag t = QContentTag.contentTag;

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(ExpressionUtils.anyOf(c.draft.isFalse(), c.draft.isNull()));
		if (!admin) {
			if (userId != null)
				builder.and(ExpressionUtils.anyOf(c.publicated.isTrue(), u.id.eq(userId)));
			else
				builder.and(c.publicated.isTrue());
		}
		if (filter.getParentNodeId() != null)
			builder.and(n.id.eq(filter.getParentNodeId()));
		if (StringUtils.isNotBlank(filter.getName())) {
			String filterName = QuerydslUtil.transformSimpleLikeFilter(filter.getName()).toLowerCase();
			builder.andAnyOf(c.name.toLowerCase().like(filterName), t.name.toLowerCase().like(filterName));
		}
		if (StringUtils.isNotBlank(filter.getContentReaderID()))
			builder.and(c.contentReaderId.eq(filter.getContentReaderID()));

		return builder.getValue();
	}

	@Override
	public QueryResults<ContentNodeOverviewTO> findByTagAndUserAccess(Long tagId, Long userId, boolean admin,
																	  int offset, int limit) {
		JPAQuery<ContentNode> query = new JPAQuery<>(entityManager);
		QContentNode c = QContentNode.contentNode;
		QNode n = QNode.node;
		QUser u = QUser.user;
		QContentTag t = QContentTag.contentTag;
		query.offset(offset).limit(limit);
		return query.from(t).innerJoin(t.contentNodes, c).innerJoin(c.parent, n).innerJoin(c.author, u)
				.where(createBasicNodePredicate(new ContentNodeFilterTO(), userId, admin), t.id.eq(tagId))
				.select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id,
						c.creationDate,
						c.lastModificationDate, c.publicated, u.name, u.id, c.id))
				.orderBy(new OrderSpecifier<LocalDateTime>(Order.DESC, c.creationDate)).fetchResults();
	}

	@Override
	public QueryResults<ContentNodeOverviewTO> findByUserFavouritesAndUserAccess(Long favouritesUserId, Long userId,
																				 boolean admin, int offset,
																				 int limit) {
		JPAQuery<ContentNode> query = new JPAQuery<>(entityManager);
		QContentNode c = QContentNode.contentNode;
		QNode n = QNode.node;
		QUser u = QUser.user;
		QUser uf = new QUser("favOwnerUser");
		QuerydslUtil.applyPagination(offset, limit, query);
		return query.from(uf).innerJoin(uf.favourites, c).innerJoin(c.parent, n).innerJoin(c.author, u)
				.where(createBasicNodePredicate(new ContentNodeFilterTO(), userId, admin), uf.id.eq(favouritesUserId))
				.select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id,
						c.creationDate,
						c.lastModificationDate, c.publicated, u.name, u.id, c.id))
				.orderBy(new OrderSpecifier<LocalDateTime>(Order.DESC, c.creationDate)).fetchResults();
	}

	@Override
	public QueryResults<ContentNodeOverviewTO> findByFilterAndUserAccess(ContentNodeFilterTO filter, Long userId,
																		 boolean admin, int offset, int limit,
																		 String sortProperty) {
		JPAQuery<ContentNode> query = new JPAQuery<>(entityManager);
		QContentNode c = QContentNode.contentNode;
		QNode n = QNode.node;
		QUser u = QUser.user;
		QContentTag t = QContentTag.contentTag;
		QuerydslUtil.applyPagination(offset, limit, query);
		query = query.from(c).innerJoin(c.parent, n).innerJoin(c.author, u).innerJoin(c.contentTags, t)
				.where(createBasicNodePredicate(filter, userId, admin));
		if (sortProperty != null) {
			query = query.orderBy(QuerydslUtil.transformOrder(false, sortProperty));
		} else {
			query = query.orderBy(new OrderSpecifier<LocalDateTime>(Order.DESC, c.creationDate));
		}
		return query.select(new QContentNodeOverviewTO(c.contentReaderId, c.contentId, c.name, n.name, n.id,
				c.creationDate, c.lastModificationDate, c.publicated, u.name, u.id, c.id)).fetchResults();
	}

}
