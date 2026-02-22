package cz.gattserver.grass.articles.model;

import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.articles.model.domain.QArticle;
import cz.gattserver.grass.core.model.domain.QContentNode;
import cz.gattserver.grass.core.model.domain.QNode;
import cz.gattserver.grass.core.model.domain.QUser;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.pg.interfaces.*;
import cz.gattserver.grass.pg.model.Photogallery;
import cz.gattserver.grass.pg.model.QPhotogallery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport
        implements ArticleRepositoryCustom {

    private final QArticle a = QArticle.article;
    private final QContentNode c = QContentNode.contentNode;
    private final QUser u = QUser.user;
    private final QNode n = QNode.node;

    public ArticleRepositoryCustomImpl() {
        super(Photogallery.class);
    }

    private JPQLQuery<Photogallery> createBaseQuery(Long userId, boolean isAdmin) {
        JPQLQuery<Photogallery> query = from(a).join(c).on(a.contentNodeId.eq(c.id));
        if (!isAdmin) {
            if (userId == null) {
                query.where(c.publicated.isTrue());
            } else {
                query.where(c.publicated.isTrue().or(c.author.id.eq(userId)));
            }
        }
        return query;
    }

    private JPQLQuery<Photogallery> createOverviewQuery(String filter, Long userId, boolean isAdmin) {
        JPQLQuery<Photogallery> query = createBaseQuery(userId, isAdmin);
        if (StringUtils.isNotBlank(filter))
            query.where(c.name.toLowerCase().like(QuerydslUtil.transformSimpleLikeFilter(filter).toLowerCase()));
        return query;
    }

    private JPQLQuery<Photogallery> createDetailQuery(Long userId, boolean isAdmin) {
        return createBaseQuery(userId, isAdmin).join(u).on(c.author.id.eq(u.id)).join(n).on(c.parent.id.eq(n.id));
    }

//    @Override
//    public List<PhotogalleryRESTOverviewTO> findForRestOverview(String filter, Long userId, boolean isAdmin,
//                                                                Pageable pageable) {
//        JPQLQuery<Photogallery> query = createOverviewQuery(filter, userId, isAdmin);
//        QuerydslUtil.applyPagination(pageable, query);
//        return query.select(new QPhotogalleryRESTOverviewTO(a.id, c.name)).orderBy(a.id.desc()).fetch();
//    }
//
//    @Override
//    public int count(String filter, Long userId, boolean isAdmin) {
//        return (int) createOverviewQuery(filter, userId, isAdmin).fetchCount();
//    }
//
//    @Override
//    public String findPhotogalleryPathById(Long photogalleryId) {
//        return from(a).where(a.id.eq(photogalleryId)).select(a.photogalleryDir).fetchFirst();
//    }
//
//    @Override
//    public PhotogalleryRESTOverviewTO findForRestByDirectory(String directory, Long userId, boolean isAdmin) {
//        return createOverviewQuery(null, userId, isAdmin).where(a.photogalleryDir.eq(directory))
//                .select(new QPhotogalleryRESTOverviewTO(a.id, c.name)).fetchFirst();
//    }
//
//    @Override
//    public PhotogalleryRESTTO findForRestById(Long id, Long userId, boolean isAdmin) {
//        return createDetailQuery(userId, isAdmin).where(a.id.eq(id))
//                .select(new QPhotogalleryRESTTO(a.id, c.name, c.creationDate, c.lastModificationDate, u.name,
//                        a.photogalleryDir)).fetchFirst();
//    }
//
//    @Override
//    public PhotogalleryTO findForDetailById(Long id, Long userId, boolean isAdmin) {
//        return createDetailQuery(userId, isAdmin).where(a.id.eq(id))
//                .select(new QPhotogalleryTO(a.id, a.contentNodeId, c.name, n.id, n.name, c.creationDate,
//                        c.lastModificationDate, u.id, u.name, a.photogalleryDir, c.publicated, c.draft)).fetchFirst();
//    }
}