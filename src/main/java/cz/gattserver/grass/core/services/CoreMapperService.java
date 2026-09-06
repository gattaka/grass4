package cz.gattserver.grass.core.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.interfaces.QuoteTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.model.domain.ContentTag;
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
    UserInfoTO map(User e);

	/**
	 * Převede {@link Quote} na {@link QuoteTO}
	 * 
	 * @param e
	 * @return
	 */
    QuoteTO map(Quote e);

    ContentTag map(ContentTagTO contentTagTO);
}