package cz.gattserver.grass.hw.interfaces;

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

	public HWItemTypeTO(String name) {
		this.name = name;
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
