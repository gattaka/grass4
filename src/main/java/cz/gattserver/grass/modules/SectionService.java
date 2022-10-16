package cz.gattserver.grass.modules;

import java.util.Set;

import cz.gattserver.grass.security.Role;
import cz.gattserver.grass.ui.pages.factories.template.PageFactory;

/**
 * Objekt sekce. Jde o objekt, který u sebe má informace potřebné k zapojení
 * sekce do systému
 * 
 * @author gatt
 * 
 */
public interface SectionService {

	/**
	 * Vrátí factory pro vytváření stránky
	 * 
	 * @return factory stránky
	 */
	public PageFactory getSectionPageFactory();

	/**
	 * Vrátí název sekce, tento text se bude zobrazovat přímo v hlavním menu, ze
	 * kterého se také bude přecházet na okno dané sekce
	 * 
	 * @return název sekce
	 */
	public String getSectionCaption();

	/**
	 * Zjistí, zda může uživatel s danými rolemi zobrazit tuto sekci (odkaz na
	 * ní, samotná auth dle adresy URL se provádí v pageFactories jednotlivých
	 * stránek)
	 * 
	 * @param roles
	 *            Role aktuální session, v případě nepřihlášeného uživatele
	 *            {@code null}
	 * @return {@code true} pokud role vyhovují a je možné zobrazovat odkaz na
	 *         tuto sekci, jinak false
	 */
	public boolean isVisibleForRoles(Set<Role> roles);

	/**
	 * Zavádí sekce nějaké své vlastní role? Pokud ano, ať je poskytne.
	 * 
	 * @return Pole rolí, specifických pro tento modul
	 */
	public Role[] getSectionRoles();

}
