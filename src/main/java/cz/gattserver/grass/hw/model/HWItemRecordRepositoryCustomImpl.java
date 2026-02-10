package cz.gattserver.grass.hw.model;

import cz.gattserver.grass.hw.interfaces.HWItemRecordTO;
import cz.gattserver.grass.hw.interfaces.QHWItemRecordTO;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class HWItemRecordRepositoryCustomImpl extends QuerydslRepositorySupport
        implements HWItemRecordRepositoryCustom {

    private final QHWItemRecord r = QHWItemRecord.hWItemRecord;

    public HWItemRecordRepositoryCustomImpl() {
        super(HWItemRecord.class);
    }

    @Override
    public List<HWItemRecordTO> findByItemId(Long itemId) {
        return from(r).where(r.hwItemId.eq(itemId))
                .select(new QHWItemRecordTO(r.id, r.hwItemId, r.date, r.description, r.state))
                .orderBy(r.date.desc(), r.id.desc()).fetch();
    }
}