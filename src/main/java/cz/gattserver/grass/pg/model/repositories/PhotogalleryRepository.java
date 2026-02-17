package cz.gattserver.grass.pg.model.repositories;

import cz.gattserver.grass.pg.model.domain.Photogallery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhotogalleryRepository extends JpaRepository<Photogallery, Long>, PhotogalleryRepositoryCustom {
}