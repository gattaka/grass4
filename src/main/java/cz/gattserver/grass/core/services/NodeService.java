package cz.gattserver.grass.core.services;

import java.util.List;

import cz.gattserver.grass.core.interfaces.NodeTO;

public interface NodeService {

	/**
	 * Získá kategorii dle id
	 *
	 * @param nodeId id kategorie
	 * @return kategori dle id
	 */
	NodeTO getNodeById(long nodeId);

	/**
	 * Získá všechny kořenové kategorie
	 */
	List<NodeTO> getRootNodes();

	/**
	 * Získá všechny kořenové kategorie
	 */
	int countRootNodes();

	/**
	 * Získá všechny kategorie pro zobrazení ve stromu
	 */
	List<NodeTO> getNodesForTree();

	/**
	 * Získá kategorie, které jsou jako potomci dané kategorie
	 */
	List<NodeTO> getNodesByParentNode(long nodeId);

	/**
	 * Získá počet kategorií, které jsou jako potomci dané kategorie
	 */
	int countNodesByParentNode(long parentId);

    /**
     * Založí novou kategorii
     *
     * @param parentId pakliže je kategorii vkládána do jiné kategorie, je vyplněn id
     *                 předka. Pokud je kategorie vkládána přímo do kořene sekce, je
     *                 tento argument <code>null</code>
     * @param publicated příznak, zda je katergorie zveřejněná
     * @param name     jméno nové kategorie
     * @return id kategorie pokud se přidání zdařilo, jinak <code>null</code>
     */
    long createNewNode(Long parentId, boolean publicated, String name);

	/**
	 * Přesune kategorii
	 *
	 * @param nodeId      id kategorie k přesunu
	 * @param newParentId id nového předka, do kterého má být kategorie přesunuta, nebo
	 *                    <code>null</code> pokud má být přesunuta do kořene sekce
	 * @throws IllegalStateException    pokud zjistí, že je v grafu kategorií cykl a nejedná se tedy
	 *                                  o strom
	 * @throws IllegalArgumentException pokud je vkládánaná kategorie předkem kategorie, do které je
	 *                                  vkládána -- nelze vložit předka do potomka
	 */
	void moveNode(long nodeId, Long newParentId);

	/**
	 * Smaže kategorii, pokud je prázdná
	 *
	 * @param nodeId id kategorie ke smazání
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 * <code>false</code>
	 */
	void deleteNode(long nodeId);

	/**
	 * Přejmenuje kategorii
	 *
	 * @param nodeId  id kategorie k přejmenování
	 * @param newName nový název
	 */
	void rename(long nodeId, String newName);

	/**
	 * Je kategorie prázdná?
	 *
	 * @param nodeId id kategorie
	 * @return
	 */
	boolean isNodeEmpty(long nodeId);

	/**
	 * Získá kategorie dle filtru
	 *
	 * @param filter filter
	 * @return list nalezených kategorií
	 */
	List<NodeTO> getByFilter(String filter);
}
