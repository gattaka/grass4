package cz.gattserver.grass.hw.model;

import java.time.LocalDate;

import jakarta.persistence.*;

import cz.gattserver.grass.hw.interfaces.HWItemState;
import lombok.Getter;
import lombok.Setter;

/**
 * Údaj o opravě, změně součástí apod.
 */
@Setter
@Getter
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

}