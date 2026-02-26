package cz.gattserver.grass.songs.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class ChordTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5257368159145971566L;

    /**
     * DB id
     */
    @EqualsAndHashCode.Include
    private Long id;

    /**
	 * Název
	 */
	private String name;

	/**
	 * Konfigurace
	 */
	private Long configuration;

    @QueryProjection
    public ChordTO(Long id, String name, Long configuration) {
        this.id = id;
        this.name = name;
        this.configuration = configuration;
    }
}