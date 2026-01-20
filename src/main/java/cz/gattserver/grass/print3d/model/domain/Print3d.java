package cz.gattserver.grass.print3d.model.domain;

import cz.gattserver.grass.core.model.domain.ContentNode;
import jakarta.persistence.*;

@Entity(name = "PRINT3D")
public class Print3d {

    /**
     * DB identifikátor
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
	 * Meta-informace o obsahu
	 */
	@OneToOne
	private ContentNode contentNode;

    /**
	 * Relativní cesta (od kořene 3d projektů) k adresáři s 3d projektem
	 */
	private String projectPath;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContentNode getContentNode() {
		return contentNode;
	}

	public void setContentNode(ContentNode contentNode) {
		this.contentNode = contentNode;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

}
