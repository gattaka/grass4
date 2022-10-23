package cz.gattserver.grass.core.modules.register;

import java.util.List;
import java.util.Set;

import cz.gattserver.grass.core.modules.ContentModule;
import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.Role;

/**
 * {@link ModuleRegister} udržuje přehled všech přihlášených modulů. Zároveň
 * přijímá registrace listenerů vůči bind a unbind metodám pro jednotlivé
 * služby.
 * 
 * @author gatt
 * 
 */
public interface ModuleRegister {

	Role resolveRole(String name);

	Set<Role> getRoles();

	List<ContentModule> getContentModules();

	ContentModule getContentModulesByName(String contentReaderID);

	List<SectionService> getSectionServices();

}
