package cz.gattserver.grass.hw.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * HW Objekt
 */
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HWItemOverviewTO {

    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private HWItemState state;
    private Long usedInId;
    private String usedInName;
    private String supervizedFor;
    private BigDecimal price;
    private LocalDate purchaseDate;
    private Boolean publicItem;

    public HWItemOverviewTO() {
    }

    public HWItemOverviewTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @QueryProjection
    public HWItemOverviewTO(Long id, String name, HWItemState state, String usedInName, String supervizedFor,
                            BigDecimal price, LocalDate purchaseDate, Boolean publicItem) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.usedInName = usedInName;
        this.supervizedFor = supervizedFor;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.publicItem = publicItem;
    }
}