package cz.gattserver.grass.medic.interfaces;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import cz.gattserver.common.Identifiable;

public class ScheduledVisitTO implements Identifiable<Long> {

    private Long id;

    /**
     * Účel návštěvy
     */
    @NotNull
    @Size(min = 1)
    private String purpose;

    /**
     * Místo, kam se dostavit
     */
    @NotNull
    private Long institutionId;
    private String institutionCaption;

    /**
     * Záznam - návštěva, ze které vzešlo toto datum návštěvy
     */
    private Long recordId;
    // Pouze TO
    private Long recordInstitutionId;
    private Long recordPhysicianId;
    private String recordInstitutionCaption;
    private String recordPhysicianCaption;

    /**
     * Objednán ?
     */
    private Boolean planned;

    /**
     * Datum kontroly
     */
    @NotNull
    private LocalDateTime dateTime;

    /**
     * Perioda v měsících
     */
    private int period;

    @QueryProjection
    public ScheduledVisitTO(Long id, String purpose, Long institutionId, String institutionCaption, Long recordId,
                            Long recordInstitutionId, Long recordPhysicianId, String recordInstitutionCaption,
                            String recordPhysicianCaption, Boolean planned, LocalDateTime dateTime, int period) {
        this.id = id;
        this.purpose = purpose;
        this.institutionId = institutionId;
        this.institutionCaption = institutionCaption;
        this.recordId = recordId;
        this.recordInstitutionId = recordInstitutionId;
        this.recordPhysicianId = recordPhysicianId;
        this.recordInstitutionCaption = recordInstitutionCaption;
        this.recordPhysicianCaption = recordPhysicianCaption;
        this.planned = planned;
        this.dateTime = dateTime;
        this.period = period;
    }

    public ScheduledVisitTO() {
    }

    public ScheduledVisitTO(boolean planned) {
        this.planned = planned;
    }

    public Boolean getPlanned() {
        return planned;
    }

    public void setPlanned(Boolean planned) {
        this.planned = planned;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getInstitutionCaption() {
        return institutionCaption;
    }

    public void setInstitutionCaption(String institutionCaption) {
        this.institutionCaption = institutionCaption;
    }

    public Long getRecordInstitutionId() {
        return recordInstitutionId;
    }

    public void setRecordInstitutionId(Long recordInstitutionId) {
        this.recordInstitutionId = recordInstitutionId;
    }

    public Long getRecordPhysicianId() {
        return recordPhysicianId;
    }

    public void setRecordPhysicianId(Long recordPhysicianId) {
        this.recordPhysicianId = recordPhysicianId;
    }

    public String getRecordInstitutionCaption() {
        return recordInstitutionCaption;
    }

    public void setRecordInstitutionCaption(String recordInstitutionCaption) {
        this.recordInstitutionCaption = recordInstitutionCaption;
    }

    public String getRecordPhysicianCaption() {
        return recordPhysicianCaption;
    }

    public void setRecordPhysicianCaption(String recordPhysicianCaption) {
        this.recordPhysicianCaption = recordPhysicianCaption;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ScheduledVisitTO) {
            ScheduledVisitTO to = (ScheduledVisitTO) obj;
            if (to.getId() == null) return id == null;
            else return to.getId().equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public ScheduledVisitTO copy() {
        return new ScheduledVisitTO(id, purpose, institutionId, institutionCaption, recordId, recordInstitutionId,
                recordPhysicianId, recordInstitutionCaption, recordPhysicianCaption, planned, dateTime, period);
    }
}