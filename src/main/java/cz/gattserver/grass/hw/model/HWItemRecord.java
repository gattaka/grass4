package cz.gattserver.grass.hw.model;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.*;

import cz.gattserver.grass.hw.interfaces.HWItemState;

/**
 * Údaj o opravě, změně součástí apod.
 */
@Entity(name = "HW_ITEM_RECORD")
public class HWItemRecord {

    /**
     * Identifikátor změny
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifikátor HW položky
     */
    @Column(name = "HW_ITEM_ID")
    private Long hwItemId;

    /**
     * Datum události
     */
    private LocalDate date;

    /**
     * Popis změny
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Stav do kterého byl HW převeden v souvislosti s popisovanou událostí
     */
    private HWItemState state;

    /**
     * Součásti
     */
    private String usage;

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

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public Long getHwItemId() {
        return hwItemId;
    }

    public void setHwItemId(Long hwItemId) {
        this.hwItemId = hwItemId;
    }
}