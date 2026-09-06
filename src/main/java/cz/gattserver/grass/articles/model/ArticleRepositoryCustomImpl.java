package cz.gattserver.grass.articles.model;

import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.QArticleDraftOverviewTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.QArticleTO;
import cz.gattserver.grass.core.model.domain.QContentNode;
import cz.gattserver.grass.core.model.domain.QNode;
import cz.gattserver.grass.core.model.domain.QUser;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    private static final QArticle a = QArticle.article;
    private static final QContentNode c = QContentNode.contentNode;
    private static final QUser u = QUser.user;
    private static final QNode n = QNode.node;

    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    private JPQLQuery<Article> createBaseQuery(Long userId, boolean isAdmin) {
        JPQLQuery<Article> query = from(a).join(c).on(a.contentNodeId.eq(c.id));
        if (!isAdmin) {
            if (userId == null) {
                query.where(c.hidden.isFalse());
            } else {
                query.where(c.hidden.isFalse(), c.hiddenByParent.isFalse());
            }
        }
        return query;
    }


    @Override
    public ArticleTO findByForDetailId(Long id, Long userId, boolean isAdmin) {
        return createBaseQuery(userId, isAdmin)
                // author
                .join(u).on(c.authorId.eq(u.id))
                // node
                .join(n).on(c.parentId.eq(n.id)).where(a.id.eq(id))
                .select(new QArticleTO(a.id, c.id, c.name, n.id, n.name, c.creationDate, c.lastModificationDate, u.id,
                        u.name, c.hidden, c.hiddenByParent, c.draft, c.draftSourceId, a.outputHTML, a.text,
                        a.searchableOutput, a.attachmentsDirId)).fetchFirst();
    }

    @Override
    public List<ArticleDraftOverviewTO> findDraftsForUser(Long userId) {
        return from(a)
                // content node
                .join(c).on(a.contentNodeId.eq(c.id))
                // author
                .join(u).on(c.authorId.eq(u.id))
                // node
                .join(n).on(c.authorId.eq(n.id)).where(c.authorId.eq(userId), c.draft.isTrue())
                .select(new QArticleDraftOverviewTO(a.id, c.name, c.creationDate, c.lastModificationDate, a.text))
                .fetch();
    }
}