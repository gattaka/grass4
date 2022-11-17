package cz.gattserver.grass.print3d.interfaces;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;

public class Print3dTO {

	/**
	 * Meta-informace o obsahu
	 */
	private ContentNodeTO contentNode;

	/**
	 * Relativní cesta (od kořene projektu) k adresáři s 3D projektem
	 */
	private String projectPath;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContentNodeTO getContentNode() {
		return contentNode;
	}

	public void setContentNode(ContentNodeTO contentNode) {
		this.contentNode = contentNode;
	}

	public String getPrint3dProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

}
