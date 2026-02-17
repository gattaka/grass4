package cz.gattserver.grass.pg.interfaces;

import com.querydsl.core.annotations.QueryProjection;

public class PhotogalleryRESTOverviewTO {

    /**
     * DB identifikátor
     */
    private Long id;

    private String name;

    public PhotogalleryRESTOverviewTO() {
    }

    @QueryProjection
    public PhotogalleryRESTOverviewTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}