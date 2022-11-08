package cz.gattserver.grass.articles.editor.parser.interfaces;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;

import java.util.Set;

public class ArticleRESTTO {

	/**
	 * DB identifikátor
	 */
	private Long id;

	/**
	 * Přeložený obsah článku
	 */
	private String outputHTML;

	/**
	 * Meta-informace o obsahu
	 */
	private ContentNodeTO contentNode;

	/**
	 * Dodatečné CSS resources, které je potřeba nahrát (od pluginů)
	 */
	private Set<String> pluginCSSResources;

	/**
	 * Dodatečné JS resources, které je potřeba nahrát (od pluginů)
	 */
	private Set<String> pluginJSResources;

	public Set<String> getPluginCSSResources() {
		return pluginCSSResources;
	}

	public void setPluginCSSResources(Set<String> pluginCSSResources) {
		this.pluginCSSResources = pluginCSSResources;
	}

	public Set<String> getPluginJSResources() {
		return pluginJSResources;
	}

	public void setPluginJSResources(Set<String> pluginJSResources) {
		this.pluginJSResources = pluginJSResources;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContentNodeTO getContentNode() {
		return contentNode;
	}

	public void setContentNode(ContentNodeTO contentNode) {
		this.contentNode = contentNode;
	}

	public String getOutputHTML() {
		return outputHTML;
	}

	public void setOutputHTML(String outputHTML) {
		this.outputHTML = outputHTML;
	}

}
