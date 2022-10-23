package cz.gattserver.grass.core.modules.register.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import cz.gattserver.grass.core.exception.GrassException;
import cz.gattserver.grass.core.modules.ContentModule;
import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import cz.gattserver.common.util.CZComparator;
import cz.gattserver.grass.core.modules.register.ModuleRegister;

/**
 * {@link ModuleRegisterImpl} udržuje přehled všech přihlášených modulů.
 * 
 * @author gatt
 * 
 */
@Lazy
@Component
public class ModuleRegisterImpl implements ModuleRegister {

	private static Logger logger = LoggerFactory.getLogger(ModuleRegisterImpl.class);

	/**
	 * Obsahy
	 */
	@Autowired(required = false)
	private List<ContentModule> injectedContentModules;
	private Map<String, ContentModule> contentModules;

	/**
	 * Sekce
	 */
	@Autowired(required = false)
	private List<SectionService> injectedSectionModules;

	/**
	 * Role
	 */
	private Map<String, Role> roles = new HashMap<>();

	@PostConstruct
	private final void init() {
		logger.info("ModuleRegister init");

		// Ošetření null kolekcí
		if (injectedContentModules == null)
			injectedContentModules = new ArrayList<>();
		if (injectedSectionModules == null) {
			injectedSectionModules = new ArrayList<>();
		}

		contentModules = new HashMap<>();
		for (ContentModule c : injectedContentModules)
			contentModules.put(c.getContentID(), c);

		// Role
		for (CoreRole cr : CoreRole.values())
			roles.put(cr.getAuthority(), cr);
		for (SectionService ss : injectedSectionModules)
			for (Role r : ss.getSectionRoles())
				roles.put(r.getAuthority(), r);
	}

	@Override
	public Role resolveRole(String name) {
		return roles.get(name);
	}

	@Override
	public Set<Role> getRoles() {
		return new HashSet<>(roles.values());
	}

	@Override
	public List<ContentModule> getContentModules() {
		return Collections.unmodifiableList(injectedContentModules);
	}

	@Override
	public ContentModule getContentModulesByName(String contentReaderID) {
		return contentModules.get(contentReaderID);
	}

	@Override
	public List<SectionService> getSectionServices() {
		injectedSectionModules.sort((s1, s2) -> {
			try {
				return new CZComparator().compare(s1.getSectionCaption(), s2.getSectionCaption());
			} catch (ParseException e) {
				String msg = "Nezdařilo se seřadit sekce";
				logger.error(msg, e);
				throw new GrassException(msg, e);
			}
		});
		return Collections.unmodifiableList(injectedSectionModules);
	}

}
