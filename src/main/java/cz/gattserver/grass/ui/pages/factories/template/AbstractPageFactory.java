package cz.gattserver.grass.ui.pages.factories.template;

public abstract class AbstractPageFactory implements PageFactory {

	private String pageName;

	/**
	 * Konstruktor
	 * 
	 * @param pageName
	 *            jméno stránky (URL, dle kterého se k ní bude přistupovat)
	 */
	public AbstractPageFactory(String pageName) {
		this.pageName = pageName;
	}

	public String getPageName() {
		return pageName;
	}

}
