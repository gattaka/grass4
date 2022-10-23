package cz.gattserver.grass.core.modules.register.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.modules.register.PageFactoriesRegister;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;

@Component
public class PageFactoriesRegisterImpl implements PageFactoriesRegister {

	/**
	 * Domovská stránka
	 */
	@Resource(name = "homePageFactory")
	private PageFactory homePageFactory;

	/**
	 * Hlavní mapa stránek
	 */
	@Autowired
	private List<PageFactory> pageFactories;

	private Map<String, PageFactory> factories = new HashMap<>();

	@PostConstruct
	public void init() {
		for (PageFactory factory : pageFactories)
			factories.put(factory.getPageName(), factory);
	}

	public void setHomepageFactory(PageFactory homepageFactory) {
		this.homePageFactory = homepageFactory;
	}

	/**
	 * Dělá prakticky to samé jako původní get, až na to, že pakliže není
	 * nalezena factory pro daný klíč, je vrácena factory homepage
	 */
	public PageFactory get(String key) {
		PageFactory factory = factories.get(key);
		return factory == null ? homePageFactory : factory;
	}

	/**
	 * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
	 */
	public PageFactory putAlias(String pageName, PageFactory factory) {
		return factories.put(pageName, factory);
	}

}
