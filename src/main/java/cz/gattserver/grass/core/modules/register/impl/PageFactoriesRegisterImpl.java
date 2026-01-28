package cz.gattserver.grass.core.modules.register.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.modules.register.PageFactoriesRegister;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;

@Component
public class PageFactoriesRegisterImpl implements PageFactoriesRegister {

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

    public PageFactory get(String key) {
        return factories.get(key);
    }

    /**
     * Původní put metoda - má prakticky jediné použití a tím je tvorba aliasů
     */
    public PageFactory putAlias(String pageName, PageFactory factory) {
        return factories.put(pageName, factory);
    }

}
