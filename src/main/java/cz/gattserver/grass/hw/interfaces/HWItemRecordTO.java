package cz.gattserver.grass.hw.interfaces;

import java.time.LocalDate;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Údaj o opravě, změně součástí apod.
 */
@Setter
@Getter
public class HWItemRecordTO {

    private Long id;

    private Long hwItemId;

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
    public HWItemRecordTO(Long id, Long hwItemId, LocalDate date, String description, HWItemState state) {
        this.id = id;
        this.hwItemId = hwItemId;
        this.date = date;
        this.description = description;
        this.state = state;
    }

    public HWItemRecordTO copy() {
        return new HWItemRecordTO(id, hwItemId, date, description, state);
    }
}