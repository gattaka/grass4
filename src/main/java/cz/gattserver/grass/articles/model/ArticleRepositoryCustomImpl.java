package cz.gattserver.grass.articles.model;

import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.QArticleDraftOverviewTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.QArticleTO;
import cz.gattserver.grass.core.model.domain.QContentNode;
import cz.gattserver.grass.core.model.domain.QNode;
import cz.gattserver.grass.core.model.domain.QUser;
import cz.gattserver.grass.pg.interfaces.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    private final QArticle a = QArticle.article;
    private final QContentNode c = QContentNode.contentNode;
    private final QUser u = QUser.user;
    private final QNode n = QNode.node;

    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    private JPQLQuery<Article> createBaseQuery(Long userId, boolean isAdmin) {
        JPQLQuery<Article> query = from(a).join(c).on(a.contentNodeId.eq(c.id));
        if (!isAdmin) {
            if (userId == null) {
                query.where(c.publicated.isTrue());
            } else {
                query.where(c.publicated.isTrue().or(c.author.id.eq(userId)));
            }
        }
        return query;
    }


    @Override
    public ArticleTO findByForDetailId(Long id, Long userId, boolean isAdmin) {
        return createBaseQuery(userId, isAdmin)
                // author
                .join(u).on(c.author.id.eq(u.id))
                // node
                .join(n).on(c.parent.id.eq(n.id)).where(c.author.id.eq(userId), c.draft.isTrue())
                .select(new QArticleTO(a.id, c.id, c.name, n.id, n.name, c.creationDate, c.lastModificationDate, u.id,
                        u.name, c.publicated, c.draft, c.draftSourceId, a.outputHTML, a.text, a.searchableOutput,
                        a.attachmentsDirId)).fetchFirst();
    }

    @Override
    public List<ArticleDraftOverviewTO> findDraftsForUser(Long userId) {
        return from(a)
                // content node
                .join(c).on(a.contentNodeId.eq(c.id))
                // author
                .join(u).on(c.author.id.eq(u.id))
                // node
                .join(n).on(c.parent.id.eq(n.id)).where(c.author.id.eq(userId), c.draft.isTrue())
                .select(new QArticleDraftOverviewTO(a.id, c.name, c.creationDate, c.lastModificationDate, a.text))
                .fetch();
    }
}