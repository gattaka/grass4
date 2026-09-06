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
     * Je uzel skrytý?
     */
    @Column(nullable = false)
    private Boolean hidden = false;

    /**
     * Je uzel skrytý dle jeho předka?
     */
    @Column(nullable = false, name = "HIDDEN_BY_PARENT")
    private Boolean hiddenByParent = false;

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