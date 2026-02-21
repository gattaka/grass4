package cz.gattserver.grass.hw.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Typ hw
 */
@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HWTypeTokenTO {

	/**
	 * Identifikátor hw
	 */
    @EqualsAndHashCode.Include
	private Long id;

	/**
	 * Název
	 */
	private String name;

	public HWTypeTokenTO(String name) {
		this.name = name;
	}

	@QueryProjection
	public HWTypeTokenTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

    public HWTypeTokenTO copy() {
        return new HWTypeTokenTO(id, name);
    }

}