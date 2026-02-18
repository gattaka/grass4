package cz.gattserver.grass.core.services;

import cz.gattserver.grass.core.interfaces.ContentNodeTO2;
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
     * @param sectionService sekce
     * @param userInfoTO     uživatel
     * @return <code>true</code> pokud může
     */
    boolean canShowSection(SectionService sectionService, UserInfoTO userInfoTO);

    /**
     * Může uživatel upravovat "hlášky"
     *
     * @param userInfoTO uživatel
     * @return <code>true</code> pokud může
     */
    boolean canModifyQuotes(UserInfoTO userInfoTO);

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
     * @param userInfoTO uživatel
     * @return <code>true</code> pokud může
     */
    boolean canCreateContent(UserInfoTO userInfoTO);

    /**
     * Může uživatel upravit daný obsah ?
     *
     * @param contentNodeTO obsah
     * @param userInfoTO    uživatel
     * @return <code>true</code> pokud může
     */
    boolean canModifyContent(ContentNodeTO2 contentNodeTO, UserInfoTO userInfoTO);

    /**
     * Může uživatel smazat daný obsah ?
     *
     * @param contentNodeTO obsah
     * @param userInfoTO    uživatel
     * @return <code>true</code> pokud může
     */
    boolean canDeleteContent(ContentNodeTO2 contentNodeTO, UserInfoTO userInfoTO);

    /**
     * =======================================================================
     * Kategorie
     * =======================================================================
     */

    /**
     * Může uživatel založit kategorii ?
     *
     * @param userInfoTO uživatel
     * @return <code>true</code> pokud může
     */
    boolean canCreateNode(UserInfoTO userInfoTO);

    /**
     * Může uživatel upravit kategorii ?
     *
     * @param userInfoTO uživatel
     * @return <code>true</code> pokud může
     */
    boolean canModifyNode(UserInfoTO userInfoTO);

    /**
     * Může uživatel přesunout kategorii ?
     *
     * @param userInfoTO uživatel
     * @return <code>true</code> pokud může
     */
    boolean canMoveNode(UserInfoTO userInfoTO);

    /**
     * Může uživatel smazat kategorii ?
     *
     * @param userInfoTO uživatel
     * @return <code>true</code> pokud může
     */
    boolean canDeleteNode(UserInfoTO userInfoTO);

    /**
     * =======================================================================
     * Různé
     * =======================================================================
     */

    /**
     * Je uživatel přihlášen?
     *
     * @param userInfoTO uživatel
     * @return <code>true</code> pokud je
     */
    boolean isLoggedIn(UserInfoTO userInfoTO);

    /**
     * Může daný uživatel zobrazit detaily o uživateli X ?
     *
     * @param targetUserInfoTO zobrazovaný uživatel
     * @param userInfoTO       přihlášený uživatel
     * @return <code>true</code> pokud může
     */
    boolean canShowUserDetails(UserInfoTO targetUserInfoTO, UserInfoTO userInfoTO);

    /**
     * Může se uživatel zaregistrovat ?
     *
     * @param userInfoTO přihlášený uživatel
     * @return <code>true</code> pokud může
     */
    boolean canRegistrate(UserInfoTO userInfoTO);

    /**
     * Může zobrazit stránku s nastavením ?
     *
     * @param userInfoTO přihlášený uživatel
     * @return <code>true</code> pokud může
     */
    boolean canShowSettings(UserInfoTO userInfoTO);

    /**
     * Může zobrazit stránku s nastavením aplikace ?
     *
     * @param userInfoTO přihlášený uživatel
     * @return <code>true</code> pokud může
     */
    boolean canShowApplicationSettings(UserInfoTO userInfoTO);

    /**
     * Může zobrazit stránku s nastavením kategorií ?
     *
     * @param userInfoTO přihlášený uživatel
     * @return <code>true</code> pokud může
     */
    boolean canShowCategoriesSettings(UserInfoTO userInfoTO);

    /**
     * Může zobrazit stránku s nastavením uživatelů ?
     *
     * @param userInfoTO přihlášený uživatel
     * @return <code>true</code> pokud může
     */
    boolean canShowUserSettings(UserInfoTO userInfoTO);

    /**
     * Může přidat obsah do svých oblíbených ?
     *
     * @param contentNodeTO obsah
     * @param userInfoTO    přihlášený uživatel
     * @return <code>true</code> pokud může
     */
    boolean canAddContentToFavourites(ContentNodeTO2 contentNodeTO, UserInfoTO userInfoTO);

    /**
     * Může odebrat obsah ze svých oblíbených ?
     *
     * @param contentNodeTO obsah
     * @param userInfoTO    přihlášený uživatel
     * @return <code>true</code> pokud může
     */
    boolean canRemoveContentFromFavourites(ContentNodeTO2 contentNodeTO, UserInfoTO userInfoTO);

}
