package cz.gattserver.grass.pg.util;

import com.vaadin.copilot.shaded.checkerframework.checker.units.qual.A;
import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.services.CoreMapperService;
import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass.pg.model.domain.Photogallery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PhotogalleryMapperImpl implements PhotogalleryMapper {

	/**
	 * Core mapper
	 */
	@Autowired
	private CoreMapperService mapper;

	/**
	 * Převede {@link Photogallery} na {@link PhotogalleryTO}
	 */
	public PhotogalleryTO mapPhotogalleryForDetail(Photogallery photogallery, ContentNode contentNode) {
		if (photogallery == null)
			return null;

		PhotogalleryTO photogalleryTO = new PhotogalleryTO();
		photogalleryTO.setId(photogallery.getId());
		photogalleryTO.setPhotogalleryPath(photogallery.getPhotogalleryPath());
		photogalleryTO.setContentNode(mapper.mapContentNodeForDetail(contentNode));
		return photogalleryTO;
	}

}