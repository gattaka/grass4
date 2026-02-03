package cz.gattserver.grass.medic.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity(name = "MEDICAL_VISIT")
public class ScheduledVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Účel návštěvy
     */
    @NotNull
    private String purpose;

    /**
     * Místo, kam se dostavit
     */
    @Column(name = "INSTITUTION_ID")
    private Long institutionId;

    /**
     * Záznam - návštěva, ze které vzešlo toto datum návštěvy
     */
    @Column(name = "RECORD_ID")
    private Long recordId;

    /**
     * Objednán ? Nebo je ještě potřeba se objednat ?
     */
    private boolean planned;

    /**
     * Datum kontroly
     */
    private LocalDateTime date;

    /**
     * Perioda v měsících
     */
    private int period;

    public boolean isPlanned() {
        return planned;
    }

    public void setPlanned(boolean planned) {
        this.planned = planned;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institution) {
        this.institutionId = institution;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long record) {
        this.recordId = record;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}
