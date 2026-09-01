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

    private JPQLQuery<NodeTO> createBaseQuery() {
        return from(n)
                // parent Node join
                .join(nn).on(n.parentId.eq(nn.id))
                // select
                .select(new QNodeTO(n.id, n.name, nn.name, nn.id, n.publicated, n.publicatedByParent));
    }

    @Override
    public List<NodeTO> findAllRootNodes() {
        return createBaseQuery().where(n.parentId.isNull()).fetch();
    }

    @Override
    public int countAllRootNodes() {
        return Math.toIntExact(from(n).where(n.parentId.isNull()).stream().count());
    }

    @Override
    public List<NodeTO> findAllByParentId(Long id) {
        return createBaseQuery().where(n.parentId.eq(id)).fetch();
    }

    @Override
    public int countAllByParentId(Long id) {
        return Math.toIntExact(from(n).where(n.parentId.eq(id)).stream().count());
    }

    @Override
    public List<Node> findPublicRootNodes() {
        return from(n).where(n.parentId.isNull(), n.publicatedByParent.isTrue()).fetch();
    }

    @Override
    public int countPublicRootNodes() {
        return Math.toIntExact(from(n).where(n.parentId.isNull(), n.publicatedByParent.isTrue()).stream().count());
    }

    @Override
    public List<Node> findPublicByParentId(Long id) {
        return from(n).where(n.parentId.eq(id), n.publicatedByParent.isTrue()).fetch();
    }

    @Override
    public int countPublicByParentId(Long id) {
        return Math.toIntExact(from(n).where(n.parentId.eq(id), n.publicatedByParent.isTrue()).stream().count());
    }

    @Override
    public List<NodeTO> findAllByFilter(String filter) {
        return createBaseQuery().where(n.name.like(filter)).fetch();
    }

    @Override
    public List<NodeTO> findPublicByFilter(String filter) {
        return createBaseQuery().where(n.name.like(filter), n.publicatedByParent.isTrue()).fetch();
    }

    @Override
    public NodeTO findAndMapById(Long nodeId) {
        return createBaseQuery().where(n.id.eq(nodeId)).fetchFirst();
    }

    @Override
    public List<NodeTO> findForTree() {
        return createBaseQuery().orderBy(n.id.asc()).fetch();
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