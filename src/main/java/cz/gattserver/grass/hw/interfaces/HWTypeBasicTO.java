package cz.gattserver.grass.hw.interfaces;

import com.querydsl.core.annotations.QueryProjection;

/**
 * Typ hw
 */
public class HWTypeBasicTO {

	/**
	 * Identifikátor hw
	 */
	private Long id;

	/**
	 * Název
	 */
	private String name;

	public HWTypeBasicTO(String name) {
		this.name = name;
	}

	@QueryProjection
	public HWTypeBasicTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

    public HWTypeBasicTO() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public HWTypeBasicTO copy() {
        return new HWTypeBasicTO(id, name);
    }
}