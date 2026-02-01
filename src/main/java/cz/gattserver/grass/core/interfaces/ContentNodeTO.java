package cz.gattserver.grass.core.interfaces;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ContentNodeTO {

    /**
     * DB identifikátor
     */
    private Long id;

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
	 * nadřazený uzel (kategorie ve které obsah je)
	 */
	private NodeOverviewTO parent;

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
	private UserInfoTO author;

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
	private Set<ContentTagOverviewTO> contentTags;

	public String getContentReaderID() {
		return contentReaderID;
	}

	public ContentNodeTO setContentReaderID(String contentReaderID) {
		this.contentReaderID = contentReaderID;
		return this;
	}

	public Long getContentID() {
		return contentID;
	}

	public ContentNodeTO setContentID(Long contentID) {
		this.contentID = contentID;
		return this;
	}

	public String getName() {
		return name;
	}

	public ContentNodeTO setName(String name) {
		this.name = name;
		return this;
	}

	public NodeOverviewTO getParent() {
		return parent;
	}

	public ContentNodeTO setParent(NodeOverviewTO parent) {
		this.parent = parent;
		return this;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public ContentNodeTO setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public LocalDateTime getLastModificationDate() {
		return lastModificationDate;
	}

	public ContentNodeTO setLastModificationDate(LocalDateTime lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
		return this;
	}

	public boolean isPublicated() {
		return publicated;
	}

	public ContentNodeTO setPublicated(boolean publicated) {
		this.publicated = publicated;
		return this;
	}

	public UserInfoTO getAuthor() {
		return author;
	}

	public ContentNodeTO setAuthor(UserInfoTO author) {
		this.author = author;
		return this;
	}

	public Long getId() {
		return id;
	}

	public ContentNodeTO setId(Long id) {
		this.id = id;
		return this;
	}

	public ContentNodeTO setDraft(boolean draft) {
		this.draft = draft;
		return this;
	}

	public boolean isDraft() {
		return draft;
	}

	public Long getDraftSourceId() {
		return draftSourceId;
	}

	public ContentNodeTO setDraftSourceId(Long draftSourceId) {
		this.draftSourceId = draftSourceId;
		return this;
	}

	public Set<ContentTagOverviewTO> getContentTags() {
		return contentTags;
	}

	public Set<String> getContentTagsAsStrings() {
		Set<String> set = new HashSet<>();
		contentTags.forEach(c -> set.add(c.getName()));
		return set;
	}

	public ContentNodeTO setContentTags(Set<ContentTagOverviewTO> contentTags) {
		this.contentTags = contentTags;
		return this;
	}

}
