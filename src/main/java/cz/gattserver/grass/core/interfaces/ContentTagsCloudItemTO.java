package cz.gattserver.grass.core.interfaces;

public class ContentTagsCloudItemTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Počet obsahů
	 */
	private Integer contentsCount;

	/**
	 * Název tagu
	 */
	private String name;

	/**
	 * Font velikost
	 */
	private int fontSize;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getContentsCount() {
		return contentsCount;
	}

	public void setContentsCount(Integer contentsCount) {
		this.contentsCount = contentsCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

}
