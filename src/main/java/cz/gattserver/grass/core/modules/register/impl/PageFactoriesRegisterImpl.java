package cz.gattserver.grass.core.modules.register.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.modules.register.PageFactoriesRegister;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;

@Component
public class PageFactoriesRegisterImpl implements PageFactoriesRegister {

    /**
     * Hlavní mapa stránek
     */
    private final List<PageFactory> pageFactories;

    private final Map<String, PageFactory> factories = new HashMap<>();

    public PageFactoriesRegisterImpl(List<PageFactory> pageFactories) {
        this.pageFactories = pageFactories;
    }

    @PostConstruct
    public void init() {
        for (PageFactory factory : pageFactories)
            factories.put(factory.getPageName(), factory);
    }

    public PageFactory get(String key) {
        return factories.get(key);
    }

}