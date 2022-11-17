package cz.gattserver.grass.pg.util;

import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass.pg.model.domain.Photogallery;

import java.util.List;

public interface PhotogalleryMapper {

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryTO}
	 */
	public PhotogalleryTO mapPhotogalleryForDetail(Photogallery photogallery);

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryRESTOverviewTO}
	 */
	public PhotogalleryRESTOverviewTO mapPhotogalleryForRESTOverview(Photogallery photogallery);

	/**
	 * Převede list {@link Photogallery} na list
	 * {@link PhotogalleryRESTOverviewTO}
	 */
	public List<PhotogalleryRESTOverviewTO> mapPhotogalleryForRESTOverviewCollection(
			List<Photogallery> photogalleryCollection);

}
