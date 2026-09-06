package cz.gattserver.grass.core.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ContentNodeTO implements ContentNodeBaseTO {

    /**
     * DB identifikátor
     */
    private Long id;

    /**
     * ID služby, která daný obsah umí číst
     */
    private String contentReaderId;

    /**
     * ID samotného obsahu v rámci dané služby (typu obsahu)
     */
    private Long contentNodeId;

    /**
     * Název obsahu
     */
    private String name;

    /**
     * nadřazený uzel (kategorie ve které obsah je)
     */
    private Long parentId;
    private String parentName;

    /**
     * Kdy byl obsah vytvořen
     */
    private LocalDateTime creationDate;

    /**
     * Kdy byl naposledy upraven
     */
    private LocalDateTime lastModificationDate;

    /**
     * Je obsah skrytý?
     */
    private boolean hidden;

    /**
     * Je obsah veřejný nebo soukromý dle jeho předka?
     */
    private boolean hiddenByParent;

    /**
     * Kdo ho vytvořil
     */
    private Long authorId;
    private String authorName;

    /**
     * Jde o plnohodnotný článek, nebo jde o rozpracovaný obsah?
     */
    private boolean draft = false;

    /**
     * Jde-li o draft upravovaného obsahu, jaké je jeho id (id v rámci konkrétní služby, není ContentNode id)
     */
    private Long draftSourceId;

    /**
     * Tagy
     */
    private Set<ContentTagTO> contentTags;

    @QueryProjection
    public ContentNodeTO(String contentReaderId, Long id, Long contentNodeId, String name, Long parentId,
                         String parentName, LocalDateTime creationDate, LocalDateTime lastModificationDate,
                         boolean hidden, boolean hiddenByParent, Long authorId, String authorName, boolean draft,
                         Long draftSourceId) {
        this.contentReaderId = contentReaderId;
        this.id = id;
        this.contentNodeId = contentNodeId;
        this.name = name;
        this.parentId = parentId;
        this.parentName = parentName;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.hidden = hidden;
        this.hiddenByParent = hiddenByParent;
        this.authorId = authorId;
        this.authorName = authorName;
        this.draft = draft;
        this.draftSourceId = draftSourceId;
    }

    public Set<String> getContentTagsAsStrings() {
        Set<String> set = new HashSet<>();
        contentTags.forEach(c -> set.add(c.getName()));
        return set;
    }

}