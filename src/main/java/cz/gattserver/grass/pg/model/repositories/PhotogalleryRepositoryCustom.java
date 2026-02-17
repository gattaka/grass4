package cz.gattserver.grass.pg.model.repositories;

import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTOverviewTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PhotogalleryRepositoryCustom {

    List<PhotogalleryRESTOverviewTO> findForRestOverview(String filter, Long userId, boolean isAdmin,
                                                         Pageable pageable);

    int count(String filter, Long userId, boolean isAdmin);

    String findPhotogalleryPathById(Long photogalleryId);

    PhotogalleryRESTOverviewTO findForRestByDirectory(String directory,Long userId, boolean isAdmin);
}