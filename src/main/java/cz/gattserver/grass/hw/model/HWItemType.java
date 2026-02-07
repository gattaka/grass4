package cz.gattserver.grass.hw.model;

import jakarta.persistence.*;

@Entity(name = "HW_ITEM_TYPE")
public class HWItemType {

	/**
	 * Identifikátor hw
	 */
    @EmbeddedId
	private HWItemTypeId id;

    public HWItemTypeId getId() {
        return id;
    }

    public void setId(HWItemTypeId id) {
        this.id = id;
    }
}