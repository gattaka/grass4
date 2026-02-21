package cz.gattserver.grass.print3d.model;

import cz.gattserver.grass.core.model.domain.ContentNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
    @Column(name = "PROJECTPATH")
	private String projectDir;

}