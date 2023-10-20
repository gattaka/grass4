package cz.gattserver.grass.core.model.domain;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "CONTENTNODE")
public class ContentNode {

    /**
     * ID služby, která daný obsah umí číst
     */
    private String contentReaderId;

    /**
     * ID samotného obsahu v rámci dané služby (typu obsahu)
     */
    private Long contentId;

    /**
     * Název obsahu
     */
    private String name;

    /**
     * Nadřazený uzel (kategorie ve které obsah je)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Node parent;

    /**
     * Kdy byl obsah vytvořen
     */
    private LocalDateTime creationDate;

    /**
     * Kdy byl naposledy upraven
     */
    private LocalDateTime lastModificationDate;

    /**
     * Je obsah určen k publikování nebo je soukromý?
     */
    @Column(nullable = false)
    private Boolean publicated = true;

    /**
     * Jde o plnohodnotný obsah, nebo jde o rozpracovaný obsah?
     */
    @Column(nullable = false)
    private Boolean draft = false;

    /**
     * Jde-li o draft upravovaného obsahu, jaké je jeho id (obsah modulu obsahů)
     */
    private Long draftSourceId;

    /**
     * Tagy
     */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<ContentTag> contentTags;

    /**
     * Kdo ho vytvořil
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private User author;

    /**
     * DB identifikátor
     */
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;

    public Boolean getDraft() {
        // Zpětná kompatibilita s null záznamy
        return Boolean.TRUE.equals(draft);
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public Long getDraftSourceId() {
        return draftSourceId;
    }

    public void setDraftSourceId(Long draftSourceId) {
        this.draftSourceId = draftSourceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ContentNode))
            return false;
        return ((ContentNode) obj).getId() == getId();
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentReaderId() {
        return contentReaderId;
    }

    public void setContentReaderId(String contentReaderId) {
        this.contentReaderId = contentReaderId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(LocalDateTime lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Boolean getPublicated() {
        return publicated;
    }

    public void setPublicated(Boolean publicated) {
        this.publicated = publicated;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ContentTag> getContentTags() {
        return contentTags;
    }

    public void setContentTags(Set<ContentTag> contentTags) {
        this.contentTags = contentTags;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

}
