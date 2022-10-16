package cz.gattserver.grass.interfaces;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

/**
 * Objekt sloužící pro přepravu dat mezi fasádou a view třídami, overview
 * 
 * @author gatt
 * 
 */
public class ContentNodeOverviewTO {

	/**
	 * ID služby, která daný obsah umí číst
	 */
	private String contentReaderID;

	/**
	 * ID samotného obsahu v rámci dané služby (typu obsahu)
	 */
	private Long contentID;

	/**
	 * Název obsahu
	 */
	private String name;

	/**
	 * Nadřazený uzel (kategorie ve které obsah je)
	 */
	private String parentNodeName;
	private Long parentNodeId;

	/**
	 * Kdy byl obsah vytvořen
	 */
	private LocalDateTime creationDate;

	/**
	 * Kdy byl naposledy upraven
	 */
	private LocalDateTime lastModificationDate;

	/**
	 * Je obsah ve fázi příprav, nebo už má být publikován ?
	 */
	private boolean publicated = true;

	/**
	 * Kdo ho vytvořil
	 */
	private String authorName;
	private Long authorId;

	/**
	 * DB identifikátor
	 */
	private Long id;

	@QueryProjection
	public ContentNodeOverviewTO(String contentReaderID, Long contentID, String name, String parentNodeName,
			Long parentNodeId, LocalDateTime creationDate, LocalDateTime lastModificationDate, Boolean publicated,
			String authorName, Long authorId, Long id) {
		super();
		this.contentReaderID = contentReaderID;
		this.contentID = contentID;
		this.name = name;
		this.parentNodeName = parentNodeName;
		this.parentNodeId = parentNodeId;
		this.creationDate = creationDate;
		this.lastModificationDate = lastModificationDate;
		this.publicated = publicated;
		this.authorName = authorName;
		this.authorId = authorId;
		this.id = id;
	}

	public String getParentNodeName() {
		return parentNodeName;
	}

	public void setParentNodeName(String parentNodeName) {
		this.parentNodeName = parentNodeName;
	}

	public Long getParentNodeId() {
		return parentNodeId;
	}

	public void setParentNodeId(Long parentNodeId) {
		this.parentNodeId = parentNodeId;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public boolean isPublicated() {
		return publicated;
	}

	public void setPublicated(boolean publicated) {
		this.publicated = publicated;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContentReaderID() {
		return contentReaderID;
	}

	public void setContentReaderID(String contentReaderID) {
		this.contentReaderID = contentReaderID;
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

	public Long getContentID() {
		return contentID;
	}

	public void setContentID(Long contentID) {
		this.contentID = contentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
