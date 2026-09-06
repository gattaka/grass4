package cz.gattserver.grass.core.interfaces;

/**
 * Objekt sloužící pro filtraci obsahů
 * 
 * @author gatt
 * 
 */
public class ContentNodeFilterTO {

	/**
	 * ID služby, která daný obsah umí číst
	 */
	private String contentReaderID;

	/**
	 * Název obsahu
	 */
	private String name;

	/**
	 * Nadřazený uzel (kategorie ve které obsah je)
	 */
	private Long parentNodeId;

	/**
	 * Je obsah skrytý?
	 */
	private boolean hidden = false;

	/**
	 * Kdo ho vytvořil
	 */
	private Long authorId;

	/**
	 * DB identifikátor
	 */
	private Long id;

	public String getContentReaderID() {
		return contentReaderID;
	}

	public ContentNodeFilterTO setContentReaderID(String contentReaderID) {
		this.contentReaderID = contentReaderID;
		return this;
	}

	public String getName() {
		return name;
	}

	public ContentNodeFilterTO setName(String name) {
		this.name = name;
		return this;
	}

	public Long getParentNodeId() {
		return parentNodeId;
	}

	public ContentNodeFilterTO setParentNodeId(Long parentNodeId) {
		this.parentNodeId = parentNodeId;
		return this;
	}

	public boolean isHidden() {
		return hidden;
	}

	public ContentNodeFilterTO setHidden(boolean hidden) {
		this.hidden = hidden;
		return this;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public ContentNodeFilterTO setAuthorId(Long authorId) {
		this.authorId = authorId;
		return this;
	}

	public Long getId() {
		return id;
	}

	public ContentNodeFilterTO setId(Long id) {
		this.id = id;
		return this;
	}

}
