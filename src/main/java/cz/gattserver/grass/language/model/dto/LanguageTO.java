package cz.gattserver.grass.language.model.dto;

public class LanguageTO {

	/**
	 * Název
	 */
	private String name;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
