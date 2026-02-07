package cz.gattserver.grass.hw.model;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class HWItemRecordRepositoryCustomImpl extends QuerydslRepositorySupport
        implements HWItemRecordRepositoryCustom {

    public HWItemRecordRepositoryCustomImpl() {
        super(HWItemRecord.class);
    }
}