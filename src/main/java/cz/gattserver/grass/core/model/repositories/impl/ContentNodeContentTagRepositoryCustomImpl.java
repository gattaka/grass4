package cz.gattserver.grass.core.model.repositories.impl;

import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.interfaces.QContentTagTO;
import cz.gattserver.grass.core.model.domain.*;
import cz.gattserver.grass.core.model.repositories.ContentNodeContentTagRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.LinkedHashSet;
import java.util.Set;

public class ContentNodeContentTagRepositoryCustomImpl extends QuerydslRepositorySupport
        implements ContentNodeContentTagRepositoryCustom {

    private final QContentTag t = QContentTag.contentTag;
    private final QContentNodeContentTag ct = QContentNodeContentTag.contentNodeContentTag;

    public ContentNodeContentTagRepositoryCustomImpl() {
        super(ContentNodeContentTag.class);
    }

    @Override
    public Set<ContentTagTO> findByContendNodeId(Long contentNodeId) {
        return new LinkedHashSet<>(
                from(ct).join(t).on(ct.id.contentTagId.eq(t.id)).where(ct.id.contentNodeId.eq(contentNodeId))
                        .select(new QContentTagTO(t.id, t.name)).fetch());
    }
}