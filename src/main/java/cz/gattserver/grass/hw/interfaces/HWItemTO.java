package cz.gattserver.grass.hw.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * HW Objekt
 */
@Setter
@Getter
@NoArgsConstructor
public class HWItemTO {

    private Long id;
    private String name;
    private LocalDate purchaseDate;
    private BigDecimal price;
    private HWItemState state;
    private HWItemState stateOld; // pouze pro porovnání změny
    private Long usedInId;
    private Long usedInIdOld; // pouze pro porovnání změny
    private String usedInName;
    private String supervizedFor;
    private Boolean publicItem;
    private Integer warrantyYears;
    private String description;

    private Set<HWTypeTokenTO> types = new LinkedHashSet<>();
    private List<HWItemRecordTO> itemRecords = new ArrayList<>();

    @QueryProjection
    public HWItemTO(Long id, String name, LocalDate purchaseDate, BigDecimal price, HWItemState state, Long usedInId,
                    String usedInName, String supervizedFor, Boolean publicItem, Integer warrantyYears,
                    String description) {
        this.id = id;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.state = state;
        this.stateOld = state;
        this.usedInId = usedInId;
        this.usedInIdOld = usedInId;
        this.usedInName = usedInName;
        this.supervizedFor = supervizedFor;
        this.publicItem = publicItem;
        this.warrantyYears = warrantyYears;
        this.description = description;
    }

    public HWItemTO copy() {
        HWItemTO to =
                new HWItemTO(id, name, purchaseDate, price, state, usedInId, usedInName, supervizedFor, publicItem,
                        warrantyYears, description);
        to.setItemRecords(new ArrayList<>());
        for (HWItemRecordTO note : itemRecords)
            to.getItemRecords().add(note.copy());
        to.setTypes(new LinkedHashSet<>());
        for (HWTypeTokenTO type : types)
            to.getTypes().add(type.copy());
        return to;
    }
}