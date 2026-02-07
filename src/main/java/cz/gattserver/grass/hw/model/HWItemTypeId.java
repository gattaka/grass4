package cz.gattserver.grass.hw.model;

import jakarta.persistence.*;

@Embeddable
public class HWItemTypeId {

    @Column(name = "HW_ITEM_ID")
    private Long hwItemId;

    @Column(name = "HW_TYPE_ID")
    private Long hwTypeId;

    public Long getHwItemId() {
        return hwItemId;
    }

    public void setHwItemId(Long hwItemId) {
        this.hwItemId = hwItemId;
    }

    public Long getHwTypeId() {
        return hwTypeId;
    }

    public void setHwTypeId(Long hwTypeId) {
        this.hwTypeId = hwTypeId;
    }
}