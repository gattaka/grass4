package cz.gattserver.grass.core.services.impl;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.ContentNodeTO2;
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
	public boolean canShowSection(SectionService sectionService, UserInfoTO userInfoTO) {
		return sectionService.isVisibleForRoles(userInfoTO.getRoles());
	}

	/**
	 * Může uživatel upravovat "hlášky"
	 */
	public boolean canModifyQuotes(UserInfoTO userInfoTO) {
		return isLoggedIn(userInfoTO) && userInfoTO.isAdmin();
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
	public boolean canCreateContent(UserInfoTO userInfoTO) {
		return isLoggedIn(userInfoTO) && userInfoTO.hasRole(CoreRole.AUTHOR);
	}

	/**
	 * Může uživatel upravit daný obsah ?
	 */
	public boolean canModifyContent(ContentNodeTO2 contentNodeTO, UserInfoTO userInfoTO) {
		if (isLoggedIn(userInfoTO)) {
			// pokud je admin, může upravit kterýkoliv obsah
			if (userInfoTO.isAdmin())
				return true;

			// pokud jsi autor, můžeš upravit svůj obsah
			if (contentNodeTO.getAuthorId().equals(userInfoTO.getId()))
				return true;
		}
		return false;
	}

	/**
	 * Může uživatel smazat daný obsah ?
	 */
	public boolean canDeleteContent(ContentNodeTO2 contentNodeTO, UserInfoTO userInfoTO) {
		return canModifyContent(contentNodeTO, userInfoTO);
	}

	/**
	 * =======================================================================
	 * Kategorie
	 * =======================================================================
	 */

	/**
	 * Může uživatel založit kategorii ?
	 */
	public boolean canCreateNode(UserInfoTO userInfoTO) {
		return isLoggedIn(userInfoTO) && userInfoTO.isAdmin();
	}

	/**
	 * Může uživatel upravit kategorii ?
	 */
	public boolean canModifyNode(UserInfoTO userInfoTO) {
		return canCreateNode(userInfoTO);
	}

	/**
	 * Může uživatel přesunout kategorii ?
	 */
	public boolean canMoveNode(UserInfoTO userInfoTO) {
		return canModifyNode(userInfoTO);
	}

	/**
	 * Může uživatel smazat kategorii ?
	 */
	public boolean canDeleteNode(UserInfoTO userInfoTO) {
		return canModifyNode(userInfoTO);
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
	public boolean isLoggedIn(UserInfoTO userInfoTO) {
		return userInfoTO.getId() != null;
	}

	/**
	 * Může daný uživatel zobrazit detaily o uživateli X ?
	 */
	public boolean canShowUserDetails(UserInfoTO targetUserInfoTO, UserInfoTO userInfoTO) {
		// nelze zobrazit detail od žádného uživatele
		if (userInfoTO.getId() == null || targetUserInfoTO == null)
			return false;

		// uživatel může vidět detaily o sobě
		if (userInfoTO.getId().equals(targetUserInfoTO.getId()))
			return true;

		// administrator může vidět detaily od všech uživatelů
		return userInfoTO.isAdmin();
	}

	/**
	 * Může se uživatel zaregistrovat ?
	 */
	public boolean canRegistrate(UserInfoTO userInfoTO) {
		if (!isLoggedIn(userInfoTO)) {
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
	public boolean canShowSettings(UserInfoTO userInfoTO) {
		return isLoggedIn(userInfoTO);
	}

	/**
	 * Může zobrazit stránku s nastavením aplikace ?
	 */
	public boolean canShowApplicationSettings(UserInfoTO userInfoTO) {
		return userInfoTO.isAdmin();
	}

	/**
	 * Může zobrazit stránku s nastavením kategorií ?
	 */
	public boolean canShowCategoriesSettings(UserInfoTO userInfoTO) {
		return userInfoTO.isAdmin();
	}

	/**
	 * Může zobrazit stránku s nastavením uživatelů ?
	 */
	public boolean canShowUserSettings(UserInfoTO userInfoTO) {
		return userInfoTO.isAdmin();
	}

	/**
	 * Může přidat obsah do svých oblíbených ?
	 */
	public boolean canAddContentToFavourites(ContentNodeTO2 contentNodeTO, UserInfoTO userInfoTO) {
		return isLoggedIn(userInfoTO) && !userFacade.hasInFavourites(contentNodeTO.getContentNodeId(), userInfoTO.getId());
	}

	/**
	 * Může odebrat obsah ze svých oblíbených ?
	 */
	public boolean canRemoveContentFromFavourites(ContentNodeTO2 contentNodeTO, UserInfoTO userInfoTO) {
		return isLoggedIn(userInfoTO) && userFacade.hasInFavourites(contentNodeTO.getContentNodeId(), userInfoTO.getId());
	}
}