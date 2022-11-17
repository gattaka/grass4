package cz.gattserver.grass.pg.model.domain;

import cz.gattserver.grass.core.model.domain.ContentNode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity(name = "PHOTOGALLERY")
public class Photogallery {

	/**
	 * Meta-informace o obsahu
	 */
	@OneToOne
	private ContentNode contentNode;

	/**
	 * Relativní cesta (od kořene fotogalerie) k adresáři s fotografiemi
	 */
	private String photogalleryPath;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

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
