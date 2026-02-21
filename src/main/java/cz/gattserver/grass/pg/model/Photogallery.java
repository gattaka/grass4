package cz.gattserver.grass.pg.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
    @Column(name = "PHOTOGALLERYPATH")
	private String photogalleryDir;

}
