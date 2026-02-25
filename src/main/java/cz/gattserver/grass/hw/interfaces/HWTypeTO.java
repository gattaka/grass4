package cz.gattserver.grass.hw.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Typ hw
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HWTypeTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 53799968348098827L;

    /**
     * Identifikátor hw
     */
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Název
     */
    private String name;

    /**
     * Počet instancí daného typu
     */
    private Integer count;

    public HWTypeTO(Long id) {
        this.id = id;
    }

    @QueryProjection
    public HWTypeTO(Long id, String name, Integer count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public HWTypeTO copy() {
        return new HWTypeTO(id, name, count);
    }
}