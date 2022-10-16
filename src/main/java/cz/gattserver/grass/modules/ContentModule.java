package cz.gattserver.grass.modules;

import com.vaadin.flow.server.StreamResource;

import cz.gattserver.grass.ui.pages.factories.template.PageFactory;

public interface ContentModule {

	/**
	 * Vrátí factory stránky, která slouží jako editor pro vytváření nebo
	 * modifikaci daného obsahu
	 * 
	 * @return factory
	 */
	PageFactory getContentEditorPageFactory();

	/**
	 * Vrátí factory stránky, která slouží jako prohlížeč obsahu
	 * 
	 * @return factory
	 */
	PageFactory getContentViewerPageFactory();

	/**
	 * Vrátí popisek k tlačítku "vytvořit nový obsah"
	 * 
	 * @return popisek ve stylu "článek", aby to pasovalo k popisku "Vytvořit
	 *         nový"
	 */
	String getCreateNewContentLabel();

	/**
	 * Vrátí cestu k ikoně, kterou bude obsah reprezentován
	 * 
	 * @return cesta k ikoně obsahu
	 */
	StreamResource getContentIcon();

	/**
	 * Vrátí identifikátor služby obsahu
	 * 
	 * @return identifikátor služby
	 */
	String getContentID();

}
