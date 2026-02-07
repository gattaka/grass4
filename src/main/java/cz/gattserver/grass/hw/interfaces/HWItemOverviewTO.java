package cz.gattserver.grass.hw.interfaces;

import com.querydsl.core.annotations.QueryProjection;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * HW Objekt
 */
public class HWItemOverviewTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3678406951423588173L;

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

    public String getSupervizedFor() {
        return supervizedFor;
    }

    public void setSupervizedFor(String supervizedFor) {
        this.supervizedFor = supervizedFor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public HWItemState getState() {
        return state;
    }

    public void setState(HWItemState state) {
        this.state = state;
    }

    public String getUsedInName() {
        return usedInName;
    }

    public void setUsedInName(String usedInName) {
        this.usedInName = usedInName;
    }

    public Boolean getPublicItem() {
        return publicItem;
    }

    public void setPublicItem(Boolean publicItem) {
        this.publicItem = publicItem;
    }

    public Long getUsedInId() {
        return usedInId;
    }

    public void setUsedInId(Long usedInId) {
        this.usedInId = usedInId;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof HWItemOverviewTO that)) return false;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
