package cz.gattserver.grass.pg.util;

import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass.pg.model.domain.Photogallery;

import java.util.List;

public interface PhotogalleryMapper {

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryTO}
	 */
	 PhotogalleryTO mapPhotogalleryForDetail(Photogallery photogallery, ContentNode contentNode);
}