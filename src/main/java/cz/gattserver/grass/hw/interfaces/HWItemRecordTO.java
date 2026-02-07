package cz.gattserver.grass.hw.interfaces;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Údaj o opravě, změně součástí apod.
 */
public class HWItemRecordTO {

    private Long id;

    private Long hwItemId;

    private String usedInName;
    // pouze TO
    private Long usedInId;

    @NotNull
    private LocalDate date;

    @NotNull
    @Size(min = 1)
    private String description;

    /**
     * Stav do kterého byl HW převeden v souvislosti s popisovanou událostí
     */
    private HWItemState state;

    public HWItemRecordTO() {
    }

    @QueryProjection
    public HWItemRecordTO(Long id, Long hwItemId, String usedInName, LocalDate date, String description, HWItemState state) {
        this.id = id;
        this.hwItemId = hwItemId;
        this.usedInName = usedInName;
        this.date = date;
        this.description = description;
        this.state = state;
    }

    // copy constructor
    public HWItemRecordTO(Long id, Long hwItemId, String usedInName, Long usedInId, LocalDate date, String description,
                          HWItemState state) {
        this.id = id;
        this.hwItemId = hwItemId;
        this.usedInName = usedInName;
        this.usedInId = usedInId;
        this.date = date;
        this.description = description;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public HWItemState getState() {
        return state;
    }

    public void setState(HWItemState state) {
        this.state = state;
    }

    public Long getHwItemId() {
        return hwItemId;
    }

    public void setHwItemId(Long hwItemId) {
        this.hwItemId = hwItemId;
    }

    public String getUsedInName() {
        return usedInName;
    }

    public void setUsedInName(String usedInName) {
        this.usedInName = usedInName;
    }

    public Long getUsedInId() {
        return usedInId;
    }

    public void setUsedInId(Long usedInId) {
        this.usedInId = usedInId;
    }

    public HWItemRecordTO copy() {
        return new HWItemRecordTO(id, hwItemId, usedInName,usedInId, date, description, state);
    }
}