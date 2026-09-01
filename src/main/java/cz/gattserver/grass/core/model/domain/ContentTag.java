package cz.gattserver.grass.core.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "CONTENT_TAG")
public class ContentTag {

    /**
     * DB identifikátor
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Aktuální počet obsahů, které jsou označeny tímto tagem
     */
    @Column(name = "CONTENT_NODES_COUNT")
    private Integer contentNodeCount = 0;

    /**
     * Název tagu
     */
    private String name;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ContentTag)) return false;
        return ((ContentTag) obj).getName().equals(getName());
    }

    @Override
    public int hashCode() {
        return getName() == null ? 0 : getName().hashCode();
    }

}