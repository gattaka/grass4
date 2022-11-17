package cz.gattserver.grass.print3d.util;

import cz.gattserver.grass.print3d.interfaces.Print3dTO;
import cz.gattserver.grass.print3d.model.domain.Print3d;

public interface Print3dMapper {

	/**
	 * Převede {@link Print3d} na {@link Print3dTO}
	 */
	public Print3dTO mapProjectForDetail(Print3d project);

}
