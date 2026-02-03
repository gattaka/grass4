package cz.gattserver.grass.medic.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.common.Identifiable;

import java.time.LocalDateTime;
import java.util.Objects;

public class ScheduledVisitOverviewTO implements Identifiable<Long> {

    private Long id;
    private String purpose;
    private Long institutionId;
    private String institutionCaption;
    private Boolean planned;
    private LocalDateTime dateTime;
    private int period;

    @QueryProjection
    public ScheduledVisitOverviewTO(Long id, String purpose, Long institutionId, String institutionCaption,
                                     Boolean planned, LocalDateTime dateTime,
                                    int period) {
        this.id = id;
        this.purpose = purpose;
        this.institutionId = institutionId;
        this.institutionCaption = institutionCaption;
        this.planned = planned;
        this.dateTime = dateTime;
        this.period = period;
    }

    public ScheduledVisitOverviewTO() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionCaption() {
        return institutionCaption;
    }

    public void setInstitutionCaption(String institutionCaption) {
        this.institutionCaption = institutionCaption;
    }

    public Boolean getPlanned() {
        return planned;
    }

    public void setPlanned(Boolean planned) {
        this.planned = planned;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ScheduledVisitOverviewTO that)) return false;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}