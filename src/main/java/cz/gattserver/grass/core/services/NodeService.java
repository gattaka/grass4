package cz.gattserver.grass.core.services;

import java.util.List;

import cz.gattserver.grass.core.interfaces.NodeTO;
import jakarta.validation.constraints.NotNull;

public interface NodeService {

    /**
     * Získá kategorii dle id
     *
     * @param nodeId id kategorie
     * @return kategori dle id
     */
    NodeTO getNodeById(Long nodeId);

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
    List<NodeTO> getNodesByParentNode(Long nodeId);

    /**
     * Založí novou kategorii
     *
     * @param parentId   pakliže je kategorii vkládána do jiné kategorie, je vyplněn id
     *                   předka. Pokud je kategorie vkládána přímo do kořene sekce, je
     *                   tento argument <code>null</code>
     * @param hidden příznak, zda je katergorie zveřejněná
     * @param name       jméno nové kategorie
     * @return id kategorie pokud se přidání zdařilo, jinak <code>null</code>
     */
    Long createNewNode(Long parentId, boolean hidden, String name);

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
    void moveNode(@NotNull Long nodeId, @NotNull Long newParentId);

    /**
     * Smaže kategorii, pokud je prázdná
     *
     * @param nodeId id kategorie ke smazání
     * @return <code>true</code> pokud se přidání zdařilo, jinak
     * <code>false</code>
     */
    void deleteNode(Long nodeId);

    /**
     * Přejmenuje kategorii
     *
     * @param nodeId  id kategorie k přejmenování
     * @param newName nový název
     */
    void rename(Long nodeId, String newName);

    /**
     * Je kategorie prázdná?
     *
     * @param nodeId id kategorie
     * @return
     */
    boolean isNodeEmpty(Long nodeId);

    /**
     * Získá kategorie dle filtru
     *
     * @param filter filter
     * @return list nalezených kategorií
     */
    List<NodeTO> getByFilter(String filter);

    /**
     * Skryje kategorii
     *
     * @param id id kategorie
     */
    void hide(Long id);

    /**
     * Zveřejní kategorii
     *
     * @param id id kategorie
     */
    void show(Long id);
}
