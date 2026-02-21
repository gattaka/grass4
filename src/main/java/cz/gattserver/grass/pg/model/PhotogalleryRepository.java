package cz.gattserver.grass.pg.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotogalleryRepository extends JpaRepository<Photogallery, Long>, PhotogalleryRepositoryCustom {
}