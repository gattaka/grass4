package cz.gattserver.grass.pg.interfaces;

public class PhotogalleryRESTOverviewTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	private String name;

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
