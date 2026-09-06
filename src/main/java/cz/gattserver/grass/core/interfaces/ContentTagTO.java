package cz.gattserver.grass.core.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami
 *
 * @author gatt
 *
 */
@Getter
@Setter
public class ContentTagTO {

    /**
     * DB identifikátor
     */
    private Long id;

    /**
     * Název tagu
     */
    private String name;

    /**
     * Aktuální počet obsahů, které jsou označeny tímto tagem
     */
    private Integer contentNodeCount = 0;

    @QueryProjection
    public ContentTagTO(Long id, String name, Integer contentNodeCount) {
        this.id = id;
        this.name = name;
        this.contentNodeCount = contentNodeCount;
    }

    public ContentTagTO() {
    }

    public ContentTagTO(String name) {
        this.name = name;
    }

}