package cz.gattserver.grass.pg.model;

import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PhotogalleryRepositoryCustom {

    List<PhotogalleryRESTOverviewTO> findForRestOverview(String filter, Long userId, boolean isAdmin,
                                                         Pageable pageable);

    int count(String filter, Long userId, boolean isAdmin);

    String findPhotogalleryPathById(Long photogalleryId);

    PhotogalleryRESTOverviewTO findForRestByDirectory(String directory,Long userId, boolean isAdmin);

    PhotogalleryRESTTO findForRestById(Long id, Long userId, boolean isAdmin);

    PhotogalleryTO findForDetailById(Long id,Long userId, boolean isAdmin);
}