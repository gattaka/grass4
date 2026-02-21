package cz.gattserver.grass.print3d.interfaces;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Print3dTO {

	/**
	 * Meta-informace o obsahu
	 */
	private ContentNodeTO contentNode;

	/**
	 * Relativní cesta (od kořene projektu) k adresáři s 3D projektem
	 */
	private String projectDir;

	/**
	 * DB identifikátor
	 */
	private Long id;

}