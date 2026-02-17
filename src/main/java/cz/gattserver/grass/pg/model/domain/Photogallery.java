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
	@Column(name = "CONTENTNODE_ID")
	private Long contentNodeId;

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

    public Long getContentNodeId() {
        return contentNodeId;
    }

    public void setContentNodeId(Long contentNodeId) {
        this.contentNodeId = contentNodeId;
    }

    public String getPhotogalleryPath() {
		return photogalleryPath;
	}

	public void setPhotogalleryPath(String photogalleryPath) {
		this.photogalleryPath = photogalleryPath;
	}

}
