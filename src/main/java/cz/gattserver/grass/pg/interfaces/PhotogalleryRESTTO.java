package cz.gattserver.grass.pg.interfaces;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @param id                   DB identifikátor
 * @param name                 Název
 * @param creationDate         Kdy byl obsah vytvořen
 * @param lastModificationDate Kdy byl naposledy upraven
 * @param author               Jméno uživatele
 * @param photogalleryPath     Cesta ke galerii
 * @param files                Jména souborů fotek
 */
public record PhotogalleryRESTTO(Long id, String name, LocalDateTime creationDate, LocalDateTime lastModificationDate,
                                 String author, String photogalleryPath, Set<String> files) {

    @QueryProjection
    public PhotogalleryRESTTO(Long id, String name, LocalDateTime creationDate, LocalDateTime lastModificationDate,
                              String author, String photogalleryPath) {
        this(id, name, creationDate, lastModificationDate, author, photogalleryPath, new HashSet<>());
    }
}