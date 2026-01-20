package cz.gattserver.grass.pg.model.domain;

import cz.gattserver.grass.core.model.domain.ContentNode;
import jakarta.persistence.*;

@Entity(name = "PHOTOGALLERY")
public class Photogallery {

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
	 * Relativní cesta (od kořene fotogalerie) k adresáři s fotografiemi
	 */
	private String photogalleryPath;


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

	public String getPhotogalleryPath() {
		return photogalleryPath;
	}

	public void setPhotogalleryPath(String photogalleryPath) {
		this.photogalleryPath = photogalleryPath;
	}

}
