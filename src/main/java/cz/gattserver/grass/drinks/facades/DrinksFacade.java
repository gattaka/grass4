package cz.gattserver.grass.drinks.facades;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.grass.drinks.model.interfaces.*;

import java.util.List;

public interface DrinksFacade {

	/**
	 * Smaže nápoj
	 * 
	 * @param id
	 */
	void deleteDrink(Long id);

	/*
	 * Piva
	 */

	/**
	 * Získá počet piv v DB
	 * 
	 * @param filterTO
	 */
	int countBeers(BeerOverviewTO filterTO);

	/**
	 * Získá počet piv v DB
	 * 
	 * @param filter
	 *            filter přes název pivovaru a piva
	 */
	int countBeers(String filter);

	/**
	 * Získá všechny piva
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<BeerOverviewTO> getBeers(BeerOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder);

	/**
	 * Získá všechny piva
	 * 
	 * @param filter
	 *            filter přes název pivovaru a piva
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<BeerOverviewTO> getBeers(String filter, int offset, int limit);

	/**
	 * Získá pivo dle id
	 * 
	 * @param id
	 */
	BeerTO getBeerById(Long id);

	/**
	 * Založ/uprav nové pivo
	 * 
	 * @param to
	 */
	BeerTO saveBeer(BeerTO to);

	/*
	 * Rumy
	 */

	/**
	 * Získá počet rumů v DB
	 * 
	 * @param filter
	 *            filter přes název palírny a rumu
	 */
	int countRums(String filter);

	/**
	 * Získá všechny rumy
	 * 
	 * @param offset
	 * @param limit
	 * @param filter
	 *            filter přes název palírny a rumu
	 * @return
	 */
	List<RumOverviewTO> getRums(String filter, int offset, int limit);

	/**
	 * Získá počet rumů v DB
	 * 
	 * @param filterTO
	 */
	int countRums(RumOverviewTO filterTO);

	/**
	 * Získá všechny rumy
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<RumOverviewTO> getRums(RumOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder);

	/**
	 * Získá rum dle id
	 * 
	 * @param id
	 */
	RumTO getRumById(Long id);

	/**
	 * Založ/uprav nový rum
	 * 
	 * @param to
	 */
	RumTO saveRum(RumTO to);

	/*
	 * Whiskey
	 */

	/**
	 * Získá počet whiskey v DB
	 * 
	 * @param filter
	 *            filter přes název palírny a whiskey
	 */
	int countWhiskeys(String filter);

	/**
	 * Získá všechny whiskey
	 * 
	 * @param offset
	 * @param limit
	 * @param filter
	 *            filter přes název palírny a whiskey
	 * @return
	 */
	List<WhiskeyOverviewTO> getWhiskeys(String filter, int offset, int limit);

	/**
	 * Získá počet whiskey v DB
	 * 
	 * @param filterTO
	 */
	int countWhiskeys(WhiskeyOverviewTO filterTO);

	/**
	 * Získá všechny whiskey
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<WhiskeyOverviewTO> getWhiskeys(WhiskeyOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder);

	/**
	 * Získá whiskey dle id
	 * 
	 * @param id
	 */
	WhiskeyTO getWhiskeyById(Long id);

	/**
	 * Založ/uprav novou whiskey
	 * 
	 * @param to
	 */
	WhiskeyTO saveWhiskey(WhiskeyTO to);

	/*
	 * Vína
	 */

	/**
	 * Získá počet vín v DB
	 * 
	 */
	int countWines();

	/**
	 * Získá všechny vína
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<WineOverviewTO> getWines(int offset, int limit);

	/**
	 * Získá počet vín v DB
	 * 
	 * @param filterTO
	 */
	int countWines(WineOverviewTO filterTO);

	/**
	 * Získá všechny vína
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<WineOverviewTO> getWines(WineOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder);

	/**
	 * Získá víno dle id
	 * 
	 * @param id
	 */
	WineTO getWineById(Long id);

	/**
	 * Založ/uprav nové víno
	 * 
	 * @param to
	 */
	WineTO saveWine(WineTO to);

	/*
	 * Jiné
	 */

	/**
	 * Získá počet jiných nápojů v DB
	 * 
	 */
	int countOthers();

	/**
	 * Získá všechny jiné nápoje
	 * 
	 * @param offset
	 * @param limit
	 * @return
	 */
	List<OtherOverviewTO> getOthers(int offset, int limit);

	/**
	 * Získá počet jiných nápojů v DB
	 * 
	 * @param filterTO
	 */
	int countOthers(OtherOverviewTO filterTO);

	/**
	 * Získá všechny jiné nápoje
	 * 
	 * @param filterTO
	 * @param offset
	 * @param limit
	 * @param sortOrder
	 * @return
	 */
	List<OtherOverviewTO> getOthers(OtherOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder);

	/**
	 * Získá jiný nápoj dle id
	 * 
	 * @param id
	 */
	OtherTO getOtherById(Long id);

	/**
	 * Založ/uprav nový jiný nápoj
	 * 
	 * @param to
	 */
	OtherTO saveOther(OtherTO to);

}
