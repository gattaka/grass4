package cz.gattserver.grass.pg.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.core.interfaces.ContentNodeTO2;
import cz.gattserver.grass.core.interfaces.ContentTagTO;

import java.time.LocalDateTime;
import java.util.Set;

public class PhotogalleryTO implements ContentNodeTO2 {

    private  Long id;
    private  Long contentId;
    private  String name;
    private  Long parentId;
    private  String parentName;
    private  LocalDateTime creationDate;
    private  LocalDateTime lastModificationDate;
    private  Long authorId;
    private  String authorName;
    private  String photogalleryPath;
    private  boolean publicated;
    private  boolean draft;

    private Set<ContentTagTO> contentTags;

    @QueryProjection
    public PhotogalleryTO(Long id, Long contentId, String name, Long parentId, String parentName,
                          LocalDateTime creationDate, LocalDateTime lastModificationDate, Long authorId,
                          String authorName, String photogalleryPath, boolean publicated, boolean draft) {
        this.id = id;
        this.contentId = contentId;
        this.name = name;
        this.parentId = parentId;
        this.parentName = parentName;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.authorId = authorId;
        this.authorName = authorName;
        this.photogalleryPath = photogalleryPath;
        this.publicated = publicated;
        this.draft = draft;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Long getContentNodeId() {
        return contentId;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public LocalDateTime getLastModificationDate() {
        return lastModificationDate;
    }

    @Override
    public boolean isDraft() {
        return draft;
    }

    @Override
    public Long getAuthorId() {
        return authorId;
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    public String getPhotogalleryPath() {
        return photogalleryPath;
    }

    @Override
    public boolean isPublicated() {
        return publicated;
    }

    public Set<ContentTagTO> getContentTags() {
        return contentTags;
    }

    @Override
    public String getParentName() {
        return parentName;
    }

    public void setContentTags(Set<ContentTagTO> contentTags) {
        this.contentTags = contentTags;
    }
}