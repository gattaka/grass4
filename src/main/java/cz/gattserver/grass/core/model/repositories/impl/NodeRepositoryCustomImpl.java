package cz.gattserver.grass.core.model.repositories.impl;

import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.interfaces.QNodeTO;
import cz.gattserver.grass.core.model.domain.*;
import cz.gattserver.grass.core.model.repositories.NodeRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class NodeRepositoryCustomImpl extends QuerydslRepositorySupport implements NodeRepositoryCustom {

    private final QNode n = QNode.node;
    private final QNode nn = new QNode("parentnode");
    private final QContentNode cn = QContentNode.contentNode;

    public NodeRepositoryCustomImpl() {
        super(Node.class);
    }

    private JPQLQuery<NodeTO> createBaseMapQuery(boolean admin) {
        JPQLQuery<NodeTO> query = from(n)
                // parent Node join (nemusí mít)
                .leftJoin(nn).on(n.parentId.eq(nn.id))
                // select
                .select(new QNodeTO(n.id, n.name, nn.name, nn.id, n.hidden, n.hiddenByParent));
        if (!admin) query.where(n.hiddenByParent.isFalse(), n.hidden.isFalse());
        return query;
    }

    // All

    @Override
    public List<NodeTO> findRootNodes(boolean admin) {
        return createBaseMapQuery(admin).where(n.parentId.isNull()).fetch();
    }

    @Override
    public int countRootNodes(boolean admin) {
        return Math.toIntExact(createBaseMapQuery(admin).where(n.parentId.isNull()).stream().count());
    }

    @Override
    public List<NodeTO> findByParentId(Long id, boolean admin) {
        return createBaseMapQuery(admin).where(n.parentId.eq(id)).fetch();
    }

    @Override
    public List<NodeTO> findByFilter(String filter, boolean admin) {
        return createBaseMapQuery(admin).where(n.name.like(filter)).fetch();
    }

    @Override
    public NodeTO findAndMapById(Long nodeId, boolean admin) {
        return createBaseMapQuery(admin).where(n.id.eq(nodeId)).fetchFirst();
    }

    @Override
    public List<NodeTO> findForTree(boolean admin) {
        return createBaseMapQuery(admin).orderBy(n.id.asc()).fetch();
    }

    @Override
    public int countSubNodes(Long nodeId) {
        return Math.toIntExact(from(n).where(n.parentId.eq(nodeId)).stream().count());
    }

    @Override
    public int countContentNodes(Long nodeId) {
        return Math.toIntExact(from(cn).where(cn.parentId.eq(nodeId)).stream().count());
    }
}