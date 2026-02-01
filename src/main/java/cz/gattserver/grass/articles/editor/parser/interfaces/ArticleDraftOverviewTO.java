package cz.gattserver.grass.articles.editor.parser.interfaces;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;

/**
 * DTO pro výběr rozpracovaného článku v menu
 * 
 * @author Hynek
 *
 */
public class ArticleDraftOverviewTO {

    /**
     * DB identifikátor
     */
    private Long id;

	/**
	 * Náhled článku
	 */
	private String text;

	/**
	 * Meta-informace o obsahu
	 */
	private ContentNodeTO contentNode;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ContentNodeTO getContentNode() {
		return contentNode;
	}

	public void setContentNode(ContentNodeTO contentNode) {
		this.contentNode = contentNode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
