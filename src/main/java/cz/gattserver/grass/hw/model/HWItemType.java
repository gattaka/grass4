package cz.gattserver.grass.hw.model;

import jakarta.persistence.*;

@Entity(name = "HW_ITEM_TYPE")
public class HWItemType {

    /**
     * Identifikátor hw
     */
    @EmbeddedId
    private HWItemTypeId id;

    public HWItemType() {
    }

    public HWItemType(Long hwItemId, Long hwTypeId) {
        this.id = new HWItemTypeId(hwItemId, hwTypeId);
    }

    public HWItemTypeId getId() {
        return id;
    }

    public void setId(HWItemTypeId id) {
        this.id = id;
    }
}