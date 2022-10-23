package cz.gattserver.grass.core.services.impl;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.config.CoreConfiguration;
import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.CoreRole;

/**
 * Access control list, bere uživatele a operaci a vyhodnocuje, zda povolit nebo
 * zablokovat
 * 
 * @author gatt
 * 
 */
@Component
public final class CoreACLServiceImpl implements CoreACLService {

	@Autowired
	private UserService userFacade;

	@Autowired
	private ConfigurationService configurationService;

	/**
	 * =======================================================================
	 * Sekce
	 * =======================================================================
	 */

	/**
	 * Může uživatel zobrazit danou sekci ?
	 */
	public boolean canShowSection(SectionService section, UserInfoTO user) {
		return section.isVisibleForRoles(user.getRoles());
	}

	/**
	 * Může uživatel upravovat "hlášky"
	 */
	public boolean canModifyQuotes(UserInfoTO user) {
		return isLoggedIn(user) && user.isAdmin();
	}

	/**
	 * =======================================================================
	 * Obsahy
	 * =======================================================================
	 */

	/**
	 * Může uživatel zobrazit daný obsah ?
	 */
	// řešeno jako DB dotaz

	/**
	 * Může uživatel vytvářet obsah ?
	 */
	public boolean canCreateContent(UserInfoTO user) {
		return isLoggedIn(user) && user.hasRole(CoreRole.AUTHOR);
	}

	/**
	 * Může uživatel upravit daný obsah ?
	 */
	public boolean canModifyContent(ContentNodeTO content, UserInfoTO user) {
		if (isLoggedIn(user)) {
			// pokud je admin, může upravit kterýkoliv obsah
			if (user.isAdmin())
				return true;

			// pokud jsi autor, můžeš upravit svůj obsah
			if (content.getAuthor().getId().equals(user.getId()))
				return true;
		}
		return false;
	}

	/**
	 * Může uživatel smazat daný obsah ?
	 */
	public boolean canDeleteContent(ContentNodeTO content, UserInfoTO user) {
		return canModifyContent(content, user);
	}

	/**
	 * =======================================================================
	 * Kategorie
	 * =======================================================================
	 */

	/**
	 * Může uživatel založit kategorii ?
	 */
	public boolean canCreateNode(UserInfoTO user) {
		return isLoggedIn(user) && user.isAdmin();
	}

	/**
	 * Může uživatel upravit kategorii ?
	 */
	public boolean canModifyNode(UserInfoTO user) {
		return canCreateNode(user);
	}

	/**
	 * Může uživatel přesunout kategorii ?
	 */
	public boolean canMoveNode(UserInfoTO user) {
		return canModifyNode(user);
	}

	/**
	 * Může uživatel smazat kategorii ?
	 */
	public boolean canDeleteNode(UserInfoTO user) {
		return canModifyNode(user);
	}

	/**
	 * =======================================================================
	 * Různé
	 * =======================================================================
	 */

	/**
	 * Je uživatel přihlášen?
	 */
	@Override
	public boolean isLoggedIn(UserInfoTO user) {
		return user.getId() != null;
	}

	/**
	 * Může daný uživatel zobrazit detaily o uživateli X ?
	 */
	public boolean canShowUserDetails(UserInfoTO anotherUser, UserInfoTO user) {
		// nelze zobrazit detail od žádného uživatele
		if (user.getId() == null || anotherUser == null)
			return false;

		// uživatel může vidět detaily o sobě
		if (user.getId().equals(anotherUser.getId()))
			return true;

		// administrator může vidět detaily od všech uživatelů
		return user.isAdmin();
	}

	/**
	 * Může se uživatel zaregistrovat ?
	 */
	public boolean canRegistrate(UserInfoTO user) {
		if (!isLoggedIn(user)) {
			// jenom host se může registrovat
			CoreConfiguration configuration = new CoreConfiguration();
			configurationService.loadConfiguration(configuration);
			return configuration.isRegistrations();
		}
		// jinak false
		return false;
	}

	/**
	 * Může zobrazit stránku s nastavením ?
	 */
	public boolean canShowSettings(UserInfoTO user) {
		return isLoggedIn(user);
	}

	/**
	 * Může zobrazit stránku s nastavením aplikace ?
	 */
	public boolean canShowApplicationSettings(UserInfoTO user) {
		return user.isAdmin();
	}

	/**
	 * Může zobrazit stránku s nastavením kategorií ?
	 */
	public boolean canShowCategoriesSettings(UserInfoTO user) {
		return user.isAdmin();
	}

	/**
	 * Může zobrazit stránku s nastavením uživatelů ?
	 */
	public boolean canShowUserSettings(UserInfoTO user) {
		return user.isAdmin();
	}

	/**
	 * Může přidat obsah do svých oblíbených ?
	 */
	public boolean canAddContentToFavourites(ContentNodeTO contentNodeDTO, UserInfoTO user) {
		return isLoggedIn(user) && !userFacade.hasInFavourites(contentNodeDTO.getId(), user.getId());
	}

	/**
	 * Může odebrat obsah ze svých oblíbených ?
	 */
	public boolean canRemoveContentFromFavourites(ContentNodeTO contentNodeDTO, UserInfoTO user) {
		return isLoggedIn(user) && userFacade.hasInFavourites(contentNodeDTO.getId(), user.getId());
	}

}
