package cz.gattserver.grass.core.interfaces;

import com.querydsl.core.annotations.QueryProjection;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami
 * 
 * @author gatt
 * 
 */
public class ContentTagTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Název tagu
	 */
	private String name;

    @QueryProjection
    public ContentTagTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ContentTagTO() {
	}

	public ContentTagTO(String name) {
		this.name = name;
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
