package cz.gattserver.grass.hw.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class HWItemTypeId implements Serializable {

    @Column(name = "HW_ITEM_ID")
    private Long hwItemId;

    @Column(name = "HW_TYPE_ID")
    private Long hwTypeId;

    public HWItemTypeId() {
    }

    public HWItemTypeId(Long hwItemId, Long hwTypeId) {
        this.hwItemId = hwItemId;
        this.hwTypeId = hwTypeId;
    }

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

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof HWItemTypeId that)) return false;

        return hwItemId.equals(that.hwItemId) && hwTypeId.equals(that.hwTypeId);
    }

    @Override
    public int hashCode() {
        int result = hwItemId.hashCode();
        result = 31 * result + hwTypeId.hashCode();
        return result;
    }
}