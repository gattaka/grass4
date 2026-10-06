package cz.gattserver.grass.medic.interfaces;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import cz.gattserver.common.Identifiable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalRecordTO implements Identifiable<Long> {

    private Long id;

    /**
     * Místo ošetření
     */
    @NotNull
    private Long institutionId;
    private String institutionName;

    /**
     * Lékař - ošetřující
     */
    @NotNull
    private Long physicianId;
    private String physicianName;

    /**
     * Kdy se to stalo
     */
    @NotNull
    private LocalDateTime dateTime;

    /**
     * Záznam o vyšetření
     */
    @NotNull
    @Size(min = 1)
    private String record = "";

    /**
     * Napsané léky
     */
    private Set<Long> medicaments = new HashSet<>();

    /**
     * Zprávy
     */
    private Set<ReportFileTO> files = new HashSet<>();

    public MedicalRecordTO() {
    }

    @QueryProjection
    public MedicalRecordTO(Long id, Long institutionId, String institutionName, Long physicianId, String physicianName,
                           LocalDateTime dateTime, String record) {
        this.id = id;
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.physicianId = physicianId;
        this.physicianName = physicianName;
        this.dateTime = dateTime;
        this.record = record;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return dateTime.format(DateTimeFormatter.ofPattern("d. M. yyyy HH:mm")) + " " + getPhysicianName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MedicalRecordTO to) {
			if (to.getId() == null) return id == null;
            else return to.getId().equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public MedicalRecordTO copy() {
        MedicalRecordTO to =
                new MedicalRecordTO(id, institutionId, institutionName, physicianId, physicianName, dateTime, record);
        to.medicaments = new HashSet<>();
        if (medicaments != null) to.medicaments.addAll(medicaments);
        if (files != null) to.files.addAll(files);
        return to;
    }
}