package cz.gattserver.grass.recipes.model.dto;

public class RecipeDTO {

	/**
	 * Název receptu
	 */
	private String name;

	/**
	 * Popis receptu
	 */
	private String description;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public RecipeDTO() {
	}

	public RecipeDTO(Long id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
