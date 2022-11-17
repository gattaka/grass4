package cz.gattserver.grass.pg.model.repositories;

import cz.gattserver.grass.pg.model.domain.Photogallery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhotogalleryRepository extends JpaRepository<Photogallery, Long> {

    @Query(value = "select count(p) from PHOTOGALLERY p where (p.contentNode.publicated = true or p.contentNode.author.id = ?1) and lower(p.contentNode.name) like lower(?2)")
    int countByUserAccess(Long userId, String filter);

    @Query(value = "select count(p) from PHOTOGALLERY p where p.contentNode.publicated = true or p.contentNode.author.id = ?1")
    int countByUserAccess(Long userId);

    @Query(value = "select count(p) from PHOTOGALLERY p where p.contentNode.publicated = true and lower(p.contentNode.name) like lower(?1)")
    int countByAnonAccess(String filter);

    @Query(value = "select count(p) from PHOTOGALLERY p where p.contentNode.publicated = true")
    int countByAnonAccess();

    @Query(value = "select p from PHOTOGALLERY p where (p.contentNode.publicated = true or p.contentNode.author.id = ?1) and lower(p.contentNode.name) like lower(?2) order by p.id desc")
    List<Photogallery> findByUserAccess(Long userId, String filter, Pageable pageable);

    @Query(value = "select p from PHOTOGALLERY p where p.contentNode.publicated = true or p.contentNode.author.id = ?1 order by p.id desc")
    List<Photogallery> findByUserAccess(Long userId, Pageable pageable);

    @Query(value = "select p from PHOTOGALLERY p where p.contentNode.publicated = true and lower(p.contentNode.name) like lower(?1) order by p.id desc")
    List<Photogallery> findByAnonAccess(String filter, Pageable pageable);

    @Query(value = "select p from PHOTOGALLERY p where p.contentNode.publicated = true order by p.id desc")
    List<Photogallery> findByAnonAccess(Pageable pageable);

    @Query(value = "select p.photogalleryPath from PHOTOGALLERY p where p.id = ?1")
    String findPhotogalleryPathById(Long photogalleryId);

    @Query(value = "select p from PHOTOGALLERY p where p.photogalleryPath = ?1")
    Photogallery findByDirectory(String directory);

}
