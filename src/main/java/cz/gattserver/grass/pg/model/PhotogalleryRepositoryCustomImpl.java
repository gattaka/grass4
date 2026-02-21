package cz.gattserver.grass.pg.model;

import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.core.model.domain.QContentNode;
import cz.gattserver.grass.core.model.domain.QNode;
import cz.gattserver.grass.core.model.domain.QUser;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.pg.interfaces.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class PhotogalleryRepositoryCustomImpl extends QuerydslRepositorySupport
        implements PhotogalleryRepositoryCustom {

    private final QPhotogallery p = QPhotogallery.photogallery;
    private final QContentNode c = QContentNode.contentNode;
    private final QUser u = QUser.user;
    private final QNode n = QNode.node;

    public PhotogalleryRepositoryCustomImpl() {
        super(Photogallery.class);
    }

    private JPQLQuery<Photogallery> createBaseQuery(Long userId, boolean isAdmin) {
        JPQLQuery<Photogallery> query = from(p).join(c).on(p.contentNodeId.eq(c.id));
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

    @Override
    public List<PhotogalleryRESTOverviewTO> findForRestOverview(String filter, Long userId, boolean isAdmin,
                                                                Pageable pageable) {
        JPQLQuery<Photogallery> query = createOverviewQuery(filter, userId, isAdmin);
        QuerydslUtil.applyPagination(pageable, query);
        return query.select(new QPhotogalleryRESTOverviewTO(p.id, c.name)).orderBy(p.id.desc()).fetch();
    }

    @Override
    public int count(String filter, Long userId, boolean isAdmin) {
        return (int) createOverviewQuery(filter, userId, isAdmin).fetchCount();
    }

    @Override
    public String findPhotogalleryPathById(Long photogalleryId) {
        return from(p).where(p.id.eq(photogalleryId)).select(p.photogalleryDir).fetchFirst();
    }

    @Override
    public PhotogalleryRESTOverviewTO findForRestByDirectory(String directory, Long userId, boolean isAdmin) {
        return createOverviewQuery(null, userId, isAdmin).where(p.photogalleryDir.eq(directory))
                .select(new QPhotogalleryRESTOverviewTO(p.id, c.name)).fetchFirst();
    }

    @Override
    public PhotogalleryRESTTO findForRestById(Long id, Long userId, boolean isAdmin) {
        return createDetailQuery(userId, isAdmin).where(p.id.eq(id))
                .select(new QPhotogalleryRESTTO(p.id, c.name, c.creationDate, c.lastModificationDate, u.name,
                        p.photogalleryDir)).fetchFirst();
    }

    @Override
    public PhotogalleryTO findForDetailById(Long id, Long userId, boolean isAdmin) {
        return createDetailQuery(userId, isAdmin).where(p.id.eq(id))
                .select(new QPhotogalleryTO(p.id, p.contentNodeId, c.name, n.id, n.name, c.creationDate,
                        c.lastModificationDate, u.id, u.name, p.photogalleryDir, c.publicated, c.draft)).fetchFirst();
    }
}