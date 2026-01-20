package cz.gattserver.grass.core.services;

import java.util.List;
import java.util.Set;

import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.security.Role;

/**
 * @author Hynek
 */
public interface UserService {

	/**
	 * Zaregistruje nového uživatele
	 * 
	 * @param email
	 *            email uživatele
	 * @param username
	 *            jméno uživatele (login)
	 * @param password
	 *            heslo uživatele
	 * @return db id, které bylo nového uživateli přiděleno
	 */
	long registrateNewUser(String email, String username, String password);

	/**
	 * Aktivuje uživatele
	 * 
	 * @param userId
	 *            id uživatele
	 */
	void activateUser(long userId);

	/**
	 * Zablokuje uživatele
	 * 
	 * @param userId
	 *            id uživatele
	 */
	void banUser(long userId);

	/**
	 * Upraví role uživatele
	 * 
	 * @param userId
	 *            id uživatele
	 * @param roles
	 *            role, které mu budou nastaveny (nejedná se o přidání ale
	 *            pevnou změnu výčtu rolí)
	 */
	void changeUserRoles(long userId, Set<? extends Role> roles);

	/**
	 * Vrátí všechny uživatele
	 * 
	 * @return list uživatelů
	 */
	List<UserInfoTO> getUserInfoFromAllUsers();

	/**
	 * Vrátí uživatele dle jména
	 * 
	 * @param username
	 *            jméno uživatele
	 * @return nalezný uživatel
	 */
	UserInfoTO getUser(String username);

	/**
	 * Vrátí uživatele dle id
	 * 
	 * @param userId
	 *            id hledaného uživatele
	 * @return nalezený uživatel
	 */
	UserInfoTO getUserById(long userId);

	/**
	 * Zjistí zda daný obsah je v oblíbených daného uživatele
	 * 
	 * @param contentNodeId
	 *            id obsahu, který bude hledán v oblíbených
	 * @param userId
	 *            id uživatele, kterému bude prohledán seznam oblíbených
	 * @return <code>true</code>, pokud má daný uživatel v oblíbených daný obsah
	 */
	boolean hasInFavourites(long contentNodeId, long userId);

	/**
	 * Přidá obsah do oblíbených uživatele
	 * 
	 * @param contentNodeId
	 *            id obsahu, který bude přidán do oblíbených
	 * @param userId
	 *            id uživatele, kterému bude obsah přidán do oblíbených
	 */
	void addContentToFavourites(long contentNodeId, long userId);

	/**
	 * Odebere obsah z oblíbených uživatele
	 * 
	 * @param contentNodeId
	 *            id obsahu, který bude odebrán z oblíbených
	 * @param userId
	 *            id uživatele, kterému bude obsah odebrán z oblíbených
	 */
	void removeContentFromFavourites(long contentNodeId, long userId);

	/**
	 * Odebere obsah z oblíbených všech uživatelů
	 * 
	 * @param contentNodeId
	 *            id obsahu, který bude odebrán z oblíbených
	 */
	void removeContentFromAllUsersFavourites(long contentNodeId);

}
