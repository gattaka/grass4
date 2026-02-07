package cz.gattserver.grass.hw.interfaces;

import com.querydsl.core.annotations.QueryProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * HW Objekt
 */
public class HWItemTO {

    private static final long serialVersionUID = 4661359528372859703L;

    private Long id;
    private String name;
    private LocalDate purchaseDate;
    private BigDecimal price;
    private HWItemState state;
    private Long usedInId;
    private String usedInName;
    private String supervizedFor;
    private Boolean publicItem;
    private Integer warrantyYears;
    private String description;

    private Set<HWTypeTO> types;
    private List<HWItemRecordTO> serviceNotes;

    public HWItemTO() {
    }

    @QueryProjection
    public HWItemTO(Long id, String name, LocalDate purchaseDate, BigDecimal price, HWItemState state, Long usedInId,
                    String usedInName, String supervizedFor, Boolean publicItem, Integer warrantyYears,
                    String description) {
        this.id = id;
        this.name = name;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.state = state;
        this.usedInId = usedInId;
        this.usedInName = usedInName;
        this.supervizedFor = supervizedFor;
        this.publicItem = publicItem;
        this.warrantyYears = warrantyYears;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getUsedInId() {
        return usedInId;
    }

    public void setUsedInId(Long usedInId) {
        this.usedInId = usedInId;
    }

    public String getUsedInName() {
        return usedInName;
    }

    public void setUsedInName(String usedInName) {
        this.usedInName = usedInName;
    }

    public String getSupervizedFor() {
        return supervizedFor;
    }

    public void setSupervizedFor(String supervizedFor) {
        this.supervizedFor = supervizedFor;
    }

    public Boolean getPublicItem() {
        return publicItem;
    }

    public void setPublicItem(Boolean publicItem) {
        this.publicItem = publicItem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getWarrantyYears() {
        return warrantyYears;
    }

    public void setWarrantyYears(Integer warrantyYears) {
        this.warrantyYears = warrantyYears;
    }

    public Set<HWTypeTO> getTypes() {
        return types;
    }

    public void setTypes(Set<HWTypeTO> types) {
        this.types = types;
    }

    public List<HWItemRecordTO> getServiceNotes() {
        return serviceNotes;
    }

    public void setServiceNotes(List<HWItemRecordTO> serviceNotes) {
        this.serviceNotes = serviceNotes;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public HWItemTO copy() {
        HWItemTO to =
                new HWItemTO(id, name, purchaseDate, price, state, usedInId, usedInName, supervizedFor, publicItem,
                        warrantyYears, description);
        to.setServiceNotes(new ArrayList<>());
        for (HWItemRecordTO note : serviceNotes)
            to.getServiceNotes().add(note.copy());
        to.setTypes(new LinkedHashSet<>());
        for (HWTypeTO type : types)
            to.getTypes().add(type.copy());
        return to;
    }
}