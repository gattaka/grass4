package cz.gattserver.grass.hw.interfaces;

import com.querydsl.core.annotations.QueryProjection;

/**
 * Typ hw
 */
public class HWItemTypeTO {

	/**
	 * Identifikátor hw
	 */
	private Long id;

	/**
	 * Název
	 */
	private String name;

	/**
	 * Počet instancí daného typu
	 */
	private Integer count;

	public HWItemTypeTO(String name) {
		this.name = name;
	}

	@QueryProjection
	public HWItemTypeTO(Long id, String name, Integer count) {
		this.id = id;
		this.name = name;
		this.count = count;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public HWItemTypeTO() {
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

}
