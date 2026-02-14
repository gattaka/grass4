package cz.gattserver.grass.hw.interfaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

public class HWFilterTO implements Serializable {

    private static final long serialVersionUID = 7979321455877648798L;

    private Long id;
    private String name;
    private HWItemState state;
    private Long usedInId;
    private String usedInName;
    private String supervizedFor;
    private BigDecimal price;
    private LocalDate purchaseDateFrom;
    private LocalDate purchaseDateTo;
    private Collection<String> types;
    private Boolean publicItem;
    private Long ignoreId;

    public String getSupervizedFor() {
        return supervizedFor;
    }

    public HWFilterTO setSupervizedFor(String supervizedFor) {
        this.supervizedFor = supervizedFor;
        return this;
    }

    public String getName() {
        return name;
    }

    public HWFilterTO setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDate getPurchaseDateFrom() {
        return purchaseDateFrom;
    }

    public HWFilterTO setPurchaseDateFrom(LocalDate purchaseDateFrom) {
        this.purchaseDateFrom = purchaseDateFrom;
        return this;
    }

    public LocalDate getPurchaseDateTo() {
        return purchaseDateTo;
    }

    public HWFilterTO setPurchaseDateTo(LocalDate purchaseDateTo) {
        this.purchaseDateTo = purchaseDateTo;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public HWFilterTO setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public HWItemState getState() {
        return state;
    }

    public HWFilterTO setState(HWItemState state) {
        this.state = state;
        return this;
    }

    public Long getUsedInId() {
        return usedInId;
    }

    public void setUsedInId(Long usedInId) {
        this.usedInId = usedInId;
    }

    public String getUsedInName() {
        return usedInName;
    }

    public HWFilterTO setUsedInName(String usedInName) {
        this.usedInName = usedInName;
        return this;
    }

    public Collection<String> getTypes() {
        return types;
    }

    public HWFilterTO setTypes(Collection<String> types) {
        this.types = types;
        return this;
    }

    public Boolean getPublicItem() {
        return publicItem;
    }

    public void setPublicItem(Boolean publicItem) {
        this.publicItem = publicItem;
    }

    public Long getIgnoreId() {
        return ignoreId;
    }

    public void setIgnoreId(Long ignoreId) {
        this.ignoreId = ignoreId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}