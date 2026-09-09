package cz.gattserver.grass.print3d.model;

import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.articles.model.Article;
import cz.gattserver.grass.core.model.domain.QContentNode;
import cz.gattserver.grass.core.model.domain.QNode;
import cz.gattserver.grass.core.model.domain.QUser;
import cz.gattserver.grass.print3d.interfaces.Print3dTO;
import cz.gattserver.grass.print3d.interfaces.QPrint3dTO;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class Print3dRepositoryCustomImpl extends QuerydslRepositorySupport implements Print3dRepositoryCustom {

    private static final QPrint3d p = QPrint3d.print3d;
    private static final QContentNode c = QContentNode.contentNode;
    private static final QUser u = QUser.user;
    private static final QNode n = QNode.node;

    public Print3dRepositoryCustomImpl() {
        super(Article.class);
    }

    private JPQLQuery<Print3d> createBaseQuery(Long userId, boolean isAdmin) {
        JPQLQuery<Print3d> query = from(p).join(c).on(p.contentNodeId.eq(c.id));
        if (!isAdmin) {
            if (userId == null) {
                query.where(c.hidden.isFalse(), c.hiddenByParent.isFalse());
            } else {
                query.where(c.authorId.eq(userId).or(c.hidden.isFalse().and(c.hiddenByParent.isFalse())));
            }
        }
        return query;
    }


    @Override
    public Print3dTO findByForDetailId(Long id, Long userId, boolean isAdmin) {
        return createBaseQuery(userId, isAdmin)
                // author
                .join(u).on(c.authorId.eq(u.id))
                // node
                .join(n).on(c.parentId.eq(n.id)).where(p.id.eq(id))
                .select(new QPrint3dTO(p.id, c.contentReaderId, c.id, c.name, n.id, n.name, c.creationDate,
                        c.lastModificationDate, u.id, u.name, c.hidden, c.hiddenByParent, c.draft, c.draftSourceId,
                        p.projectDir)).fetchFirst();
    }

}