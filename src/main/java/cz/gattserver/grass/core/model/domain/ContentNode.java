package cz.gattserver.grass.core.model.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "CONTENT_NODE")
public class ContentNode {

    /**
     * DB identifikátor
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID služby, která daný obsah umí číst
     */
    @Column(name = "CONTENT_READER_ID")
    private String contentReaderId;

    /**
     * ID samotného obsahu v rámci dané služby (typu obsahu)
     */
    @Column(name = "CONTENT_ID")
    private Long contentId;

    /**
     * Název obsahu
     */
    private String name;

    /**
     * Nadřazený uzel (kategorie ve které obsah je)
     */
    @Column(name = "PARENT_ID")
    private Long parentId;

    /**
     * Kdy byl obsah vytvořen
     */
    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    /**
     * Kdy byl naposledy upraven
     */
    @Column(name = "LAST_MODIFICATION_DATE")
    private LocalDateTime lastModificationDate;

    /**
     * Je obsah skrytý?
     */
    @Column(nullable = false)
    private Boolean hidden = true;

    /**
     * Je obsah skrytý dle jeho předka?
     */
    @Column(nullable = false, name = "HIDDEN_BY_PARENT")
    private Boolean hiddenByParent = true;

    /**
     * Jde o plnohodnotný obsah, nebo jde o rozpracovaný obsah?
     */
    @Column(nullable = false)
    private Boolean draft = false;

    /**
     * Jde-li o draft upravovaného obsahu, jaké je jeho id (obsah modulu obsahů)
     */
    @Column(name = "DRAFT_SOURCE_ID")
    private Long draftSourceId;

    /**
     * Kdo ho vytvořil
     */
    @Column(name = "AUTHOR_ID")
    private Long authorId;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ContentNode)) return false;
        return ((ContentNode) obj).getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}