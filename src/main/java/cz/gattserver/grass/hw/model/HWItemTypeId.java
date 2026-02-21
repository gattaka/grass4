package cz.gattserver.grass.hw.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@Embeddable
@EqualsAndHashCode
public class HWItemTypeId implements Serializable {

    @Serial
    private static final long serialVersionUID = -898418862465443635L;

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

}