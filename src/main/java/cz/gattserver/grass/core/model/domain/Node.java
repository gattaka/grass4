package cz.gattserver.grass.core.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "NODE")
public class Node {

    /**
     * DB identifikátor
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Název uzlu
     */
    private String name;

    /**
     * Předek uzlu
     */
    @Column(name = "PARENT_ID")
    private Long parentId;

    /**
     * Je uzel veřejný nebo soukromý?
     */
    @Column(nullable = false)
    private Boolean publicated = true;

    /**
     * Je uzel veřejný nebo soukromý dle jeho předka?
     */
    @Column(nullable = false, name = "PUBLICATED_BY_PARENT")
    private Boolean publicatedByParent = true;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) return false;
        return ((Node) obj).getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}