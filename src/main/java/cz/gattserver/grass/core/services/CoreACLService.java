package cz.gattserver.grass.core.services;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.modules.SectionService;

public interface CoreACLService {

	/**
	 * =======================================================================
	 * Sekce
	 * =======================================================================
	 */

	/**
	 * Může uživatel zobrazit danou sekci ?
	 * 
	 * @param section
	 *            sekce
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canShowSection(SectionService section, UserInfoTO user);

	/**
	 * Může uživatel upravovat "hlášky"
	 * 
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canModifyQuotes(UserInfoTO user);

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
	 * 
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canCreateContent(UserInfoTO user);

	/**
	 * Může uživatel upravit daný obsah ?
	 * 
	 * @param content
	 *            obsah
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canModifyContent(ContentNodeTO content, UserInfoTO user);

	/**
	 * Může uživatel smazat daný obsah ?
	 * 
	 * @param content
	 *            obsah
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canDeleteContent(ContentNodeTO content, UserInfoTO user);

	/**
	 * =======================================================================
	 * Kategorie
	 * =======================================================================
	 */

	/**
	 * Může uživatel založit kategorii ?
	 * 
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canCreateNode(UserInfoTO user);

	/**
	 * Může uživatel upravit kategorii ?
	 * 
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canModifyNode(UserInfoTO user);

	/**
	 * Může uživatel přesunout kategorii ?
	 * 
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canMoveNode(UserInfoTO user);

	/**
	 * Může uživatel smazat kategorii ?
	 * 
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canDeleteNode(UserInfoTO user);

	/**
	 * =======================================================================
	 * Různé
	 * =======================================================================
	 */

	/**
	 * Je uživatel přihlášen?
	 * 
	 * @param user
	 *            uživatel
	 * @return <code>true</code> pokud je
	 */
	public boolean isLoggedIn(UserInfoTO user);

	/**
	 * Může daný uživatel zobrazit detaily o uživateli X ?
	 * 
	 * @param anotherUser
	 *            zobrazovaný uživatel
	 * @param user
	 *            přihlášený uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canShowUserDetails(UserInfoTO anotherUser, UserInfoTO user);

	/**
	 * Může se uživatel zaregistrovat ?
	 * 
	 * @param user
	 *            přihlášený uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canRegistrate(UserInfoTO user);

	/**
	 * Může zobrazit stránku s nastavením ?
	 * 
	 * @param user
	 *            přihlášený uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canShowSettings(UserInfoTO user);

	/**
	 * Může zobrazit stránku s nastavením aplikace ?
	 * 
	 * @param user
	 *            přihlášený uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canShowApplicationSettings(UserInfoTO user);

	/**
	 * Může zobrazit stránku s nastavením kategorií ?
	 * 
	 * @param user
	 *            přihlášený uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canShowCategoriesSettings(UserInfoTO user);

	/**
	 * Může zobrazit stránku s nastavením uživatelů ?
	 * 
	 * @param user
	 *            přihlášený uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canShowUserSettings(UserInfoTO user);

	/**
	 * Může přidat obsah do svých oblíbených ?
	 * 
	 * @param contentNodeDTO
	 *            obsah
	 * @param user
	 *            přihlášený uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canAddContentToFavourites(ContentNodeTO contentNodeDTO, UserInfoTO user);

	/**
	 * Může odebrat obsah ze svých oblíbených ?
	 * 
	 * @param contentNodeDTO
	 *            obsah
	 * @param user
	 *            přihlášený uživatel
	 * @return <code>true</code> pokud může
	 */
	public boolean canRemoveContentFromFavourites(ContentNodeTO contentNode, UserInfoTO user);

}
