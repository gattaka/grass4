package cz.gattserver.grass.pg.model.repositories;

import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.core.model.domain.QContentNode;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass.pg.interfaces.QPhotogalleryRESTOverviewTO;
import cz.gattserver.grass.pg.model.domain.Photogallery;
import cz.gattserver.grass.pg.model.domain.QPhotogallery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class PhotogalleryRepositoryCustomImpl extends QuerydslRepositorySupport
        implements PhotogalleryRepositoryCustom {

    private final QPhotogallery p = QPhotogallery.photogallery;
    private final QContentNode c = QContentNode.contentNode;

    public PhotogalleryRepositoryCustomImpl() {
        super(Photogallery.class);
    }

    private JPQLQuery<Photogallery> createQuery(String filter, Long userId, boolean isAdmin) {
        JPQLQuery<Photogallery> query = from(p).join(c).on(p.contentNodeId.eq(c.id));
        if (!isAdmin) {
            if (userId == null) {
                query.where(c.publicated.isTrue());
            } else {
                query.where(c.publicated.isTrue().or(c.author.id.eq(userId)));
            }
        }
        if (StringUtils.isNotBlank(filter))
            query.where(c.name.toLowerCase().like(QuerydslUtil.transformSimpleLikeFilter(filter).toLowerCase()));
        return query;
    }

    @Override
    public List<PhotogalleryRESTOverviewTO> findForRestOverview(String filter, Long userId, boolean isAdmin,
                                                                Pageable pageable) {
        JPQLQuery<Photogallery> query = createQuery(filter, userId, isAdmin);
        QuerydslUtil.applyPagination(pageable, query);
        return query.select(new QPhotogalleryRESTOverviewTO(p.id, c.name)).orderBy(p.id.desc()).fetch();
    }

    @Override
    public int count(String filter, Long userId, boolean isAdmin) {
        return (int) createQuery(filter, userId, isAdmin).fetchCount();
    }

    @Override
    public String findPhotogalleryPathById(Long photogalleryId) {
        return from(p).where(p.id.eq(photogalleryId)).select(p.photogalleryPath).fetchFirst();
    }

    @Override
    public PhotogalleryRESTOverviewTO findForRestByDirectory(String directory,Long userId, boolean isAdmin) {
        return createQuery(null,userId,isAdmin).where(p.photogalleryPath.eq(directory))
                .select(new QPhotogalleryRESTOverviewTO(p.id, c.name)).fetchFirst();
    }
}