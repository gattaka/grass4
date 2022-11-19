package cz.gattserver.grass.recipes.model.dto;

public class RecipeOverviewTO {

	/**
	 * Název receptu
	 */
	private String name;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public RecipeOverviewTO() {
	}

	public RecipeOverviewTO(Long id, String name) {
		super();
		this.id = id;
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
