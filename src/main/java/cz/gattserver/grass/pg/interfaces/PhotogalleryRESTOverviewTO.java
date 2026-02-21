package cz.gattserver.grass.pg.interfaces;

import com.querydsl.core.annotations.QueryProjection;

/**
 * @param id DB identifikátor
 */
public record PhotogalleryRESTOverviewTO(Long id, String name) {

    @QueryProjection
    public PhotogalleryRESTOverviewTO {
    }
}