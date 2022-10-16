package cz.gattserver.grass.services;

import java.util.List;

import cz.gattserver.grass.interfaces.NodeOverviewTO;
import cz.gattserver.grass.interfaces.NodeTO;

public interface NodeService {

	/**
	 * Získá kategorii dle id
	 * 
	 * @param nodeId
	 *            id kategorie
	 * @return kategori dle id, namapovaná pro přehled
	 */
	public NodeOverviewTO getNodeByIdForOverview(long nodeId);

	/**
	 * Získá kategorii dle id a namapuje jí, aby se dala použít v detailu
	 * kategorie
	 * 
	 * @param nodeId
	 *            id kategorie
	 * @return kategori dle id, namapovaná pro detail
	 */
	public NodeTO getNodeByIdForDetail(long nodeId);

	/**
	 * Získá všechny kořenové kategorie
	 */
	public List<NodeOverviewTO> getRootNodes();
	
	/**
	 * Získá všechny kořenové kategorie
	 */
	public int countRootNodes();

	/**
	 * Získá všechny kategorie pro zobrazení ve stromu
	 */
	public List<NodeOverviewTO> getNodesForTree();

	/**
	 * Získá kategorie, které jsou jako potomci dané kategorie
	 */
	public List<NodeOverviewTO> getNodesByParentNode(long nodeId);
	
	/**
	 * Získá počet kategorií, které jsou jako potomci dané kategorie
	 */
	public int countNodesByParentNode(long parentId);

	/**
	 * Založí novou kategorii
	 * 
	 * @param parentId
	 *            pakliže je kategorii vkládána do jiné kategorie, je vyplněn id
	 *            předka. Pokud je kategorie vkládána přímo do kořene sekce, je
	 *            tento argument <code>null</code>
	 * @param name
	 *            jméno nové kategorie
	 * @return id kategorie pokud se přidání zdařilo, jinak <code>null</code>
	 */
	public long createNewNode(Long parentId, String name);

	/**
	 * Přesune kategorii
	 * 
	 * @throws IllegalStateException
	 *             pokud zjistí, že je v grafu kategorií cykl a nejedná se tedy
	 *             o strom
	 * @throws IllegalArgumentException
	 *             pokud je vkládánaná kategorie předkem kategorie, do které je
	 *             vkládána -- nelze vložit předka do potomka
	 * @param nodeId
	 *            id kategorie k přesunu
	 * @param newParentId
	 *            id nového předka, do kterého má být kategorie přesunuta, nebo
	 *            <code>null</code> pokud má být přesunuta do kořene sekce
	 */
	public void moveNode(long nodeId, Long newParentId);

	/**
	 * Smaže kategorii, pokud je prázdná
	 * 
	 * @param nodeId
	 *            id kategorie ke smazání
	 * @return <code>true</code> pokud se přidání zdařilo, jinak
	 *         <code>false</code>
	 */
	public void deleteNode(long nodeId);

	/**
	 * Přejmenuje kategorii
	 * 
	 * @param nodeId
	 *            id kategorie k přejmenování
	 * @param newName
	 *            nový název
	 */
	public void rename(long nodeId, String newName);

	/**
	 * Je kategorie prázdná?
	 * 
	 * @param nodeId
	 *            id kategorie
	 * @return
	 */
	public boolean isNodeEmpty(long nodeId);
}
