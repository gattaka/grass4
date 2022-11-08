package cz.gattserver.grass.drinks.model.dao;

import com.querydsl.core.types.OrderSpecifier;
import cz.gattserver.grass.drinks.model.interfaces.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface DrinkRepositoryCustom {

	/*
	 * Piva
	 */

	long countBeers(BeerOverviewTO filterTO);

	long countBeers(String filter);

	List<BeerOverviewTO> findBeers(BeerOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	List<BeerOverviewTO> findBeers(String filter, PageRequest pageable);

	BeerTO findBeerById(Long id);

	/*
	 * Rumy
	 */

	long countRums(RumOverviewTO filterTO);

	long countRums(String filter);

	List<RumOverviewTO> findRums(RumOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	List<RumOverviewTO> findRums(String filter, PageRequest pageable);

	RumTO findRumById(Long id);

	/*
	 * Whiskey
	 */

	long countWhiskeys(WhiskeyOverviewTO filterTO);

	long countWhiskeys(String filter);

	List<WhiskeyOverviewTO> findWhiskeys(WhiskeyOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	List<WhiskeyOverviewTO> findWhiskeys(String filter, PageRequest pageable);

	WhiskeyTO findWhiskeyById(Long id);

	/*
	 * Vína
	 */

	long countWines(WineOverviewTO filterTO);

	List<WineOverviewTO> findWines(WineOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	WineTO findWineById(Long id);

	/*
	 * Jiné
	 */

	long countOthers(OtherOverviewTO filterTO);

	List<OtherOverviewTO> findOthers(OtherOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	OtherTO findOtherById(Long id);
}
