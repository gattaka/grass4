package cz.gattserver.grass.core.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.interfaces.QuoteTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.model.domain.ContentTag;
import cz.gattserver.grass.core.model.domain.Node;
import cz.gattserver.grass.core.model.domain.Quote;
import cz.gattserver.grass.core.model.domain.User;

/**
 * <b>Mapper pro různé typy.</b>
 * 
 * <p>
 * Je potřeba aby byl volán na objektech s aktivními proxy objekty. To znamená,
 * že před tímto mapperem nedošlo k uzavření session, ve které byl původní
 * objekt pořízen.
 * </p>
 * 
 * <p>
 * Mapper využívá proxy objekty umístěné v atributech předávaných entit. Během
 * mapování tak může docházet k dotazům na DB, které produkují tyto proxy
 * objekty a které se bez původní session mapovaného objektu neobejdou.
 * </p>
 * 
 * @author gatt
 * 
 */
public interface CoreMapperService {

	/**
	 * Převede {@link User} na {@link UserInfoTO}
	 * 
	 * @param e
	 * @return
	 */
	public UserInfoTO map(User e);

	/**
	 * Převede {@link Quote} na {@link QuoteTO}
	 * 
	 * @param e
	 * @return
	 */
	public QuoteTO map(Quote e);

	/**
	 * Převede {@link ContentNode} na {@link ContentNodeTO}, používá se pro
	 * detail obsahu, kde je potřeba rekurzivní mapování parentů do breadcrumb
	 * 
	 * @param e
	 * @return
	 */
	public ContentNodeTO mapContentNodeForDetail(ContentNode e);

	/**
	 * Převede {@link ContentTag} na {@link ContentTagOverviewTO}
	 * 
	 * @param e
	 * @return
	 */
	public ContentTagOverviewTO mapContentTagForOverview(ContentTag e);

	/**
	 * Převede list {@link ContentTag} na list {@link ContentTagOverviewTO}
	 * 
	 * @param contentTags
	 * @return
	 */
	public List<ContentTagOverviewTO> mapContentTagCollection(Collection<ContentTag> contentTags);

	/**
	 * Převede list {@link ContentTag} na list {@link ContentTagOverviewTO}
	 * 
	 * @param contentTags
	 * @return
	 */
	public Set<ContentTagOverviewTO> mapContentTagCollectionForOverview(Collection<ContentTag> contentTags);

	/**
	 * Převede {@link Node} na {@link NodeTO}
	 * 
	 * @param e
	 * @return
	 */
	public NodeTO mapNodeForDetail(Node e);

	/**
	 * Pro overview je potřeba akorát id + název
	 */
	public NodeOverviewTO mapNodeForOverview(Node e);

	/**
	 * Převede list {@link Node} na list {@link NodeTO}
	 * 
	 * @param nodes
	 * @return
	 */
	public List<NodeOverviewTO> mapNodesForOverview(Collection<Node> nodes);

}