package cz.gattserver.grass.core.interfaces;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami, overview
 *
 * @author gatt
 *
 */
public record ContentNodeOverviewTO(Long id, String contentReaderID, Long contentID, String name, String parentNodeName,
                                    Long parentNodeId, LocalDateTime creationDate, LocalDateTime lastModificationDate,
                                    Boolean hidden, Boolean hiddenByParent, String authorName, Long authorId) {

    @QueryProjection
    public ContentNodeOverviewTO(String contentReaderID, Long contentID, String name, String parentNodeName,
                                 Long parentNodeId, LocalDateTime creationDate, LocalDateTime lastModificationDate,
                                 Boolean hidden, Boolean hiddenByParent, String authorName, Long authorId, Long id) {
        this(id, contentReaderID, contentID, name, parentNodeName, parentNodeId, creationDate, lastModificationDate,
                hidden, hiddenByParent, authorName, authorId);
    }
}