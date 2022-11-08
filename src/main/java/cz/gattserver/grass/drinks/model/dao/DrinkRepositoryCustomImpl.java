package cz.gattserver.grass.drinks.model.dao;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.drinks.model.domain.*;
import cz.gattserver.grass.drinks.model.interfaces.*;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class DrinkRepositoryCustomImpl implements DrinkRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	/*
	 * Piva
	 */

	private Predicate createPredicateBeers(BeerOverviewTO filterTO) {
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.BEER);
		if (filterTO != null) {
			builder.iLike(b.brewery, filterTO.getBrewery());
			builder.iLike(d.name, filterTO.getName());
			builder.iLike(b.category, filterTO.getCategory());
			builder.eq(b.degrees, filterTO.getDegrees());
			builder.eq(d.alcohol, filterTO.getAlcohol());
			builder.eq(b.ibu, filterTO.getIbu());
			builder.eq(b.maltType, filterTO.getMaltType());
		}
		return builder.getBuilder();
	}

	private Predicate createPredicateBeers(String filter) {
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.BEER);
		if (StringUtils.isNotBlank(filter)) {
			String matchString = '%' + filter.replace('*', '%') + '%';
			builder.getBuilder()
					.and(ExpressionUtils.or(d.name.likeIgnoreCase(matchString), b.brewery.likeIgnoreCase(matchString)));
		}
		return builder.getBuilder();
	}

	@Override
	public long countBeers(String filter) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateBeers(filter)).fetchCount();
	}

	@Override
	public long countBeers(BeerOverviewTO filterTO) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateBeers(filterTO)).fetchCount();
	}

	@Override
	public List<BeerOverviewTO> findBeers(BeerOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order) {
		JPAQuery<BeerOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		query.offset(offset).limit(limit);

		if (order == null || order.length == 0)
			order = QuerydslUtil.transformOrdering(new String[] { b.brewery.toString(), d.name.toString() },
					new boolean[] { true, true });
		return query
				.select(new QBeerOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, b.id, b.brewery,
						b.ibu, b.degrees, b.category, b.maltType, b.malts, b.hops))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateBeers(filterTO)).orderBy(order).fetch();
	}

	@Override
	public List<BeerOverviewTO> findBeers(String filter, PageRequest pageable) {
		JPAQuery<BeerOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		QuerydslUtil.applyPagination(pageable, query);

		return query
				.select(new QBeerOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, b.id, b.brewery,
						b.ibu, b.degrees, b.category, b.maltType, b.malts, b.hops))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateBeers(filter))
				.orderBy(QuerydslUtil.transformOrdering(new String[] { b.brewery.toString(), d.name.toString() },
						new boolean[] { true, true }))
				.fetch();
	}

	@Override
	public BeerTO findBeerById(Long id) {
		JPAQuery<BeerTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QBeerInfo b = QBeerInfo.beerInfo;
		return query
				.select(new QBeerTO(d.id, d.name, d.type, d.rating, d.image, d.description, d.alcohol, d.country, b.id,
						b.brewery, b.ibu, b.degrees, b.category, b.maltType, b.malts, b.hops))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(d.id.eq(id)).fetchOne();
	}

	/*
	 * Rumy
	 */

	private Predicate createPredicateRums(RumOverviewTO filterTO) {
		QDrink d = QDrink.drink;
		QRumInfo b = QRumInfo.rumInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.RUM);
		if (filterTO != null) {
			builder.eq(b.rumType, filterTO.getRumType());
			builder.iLike(d.name, filterTO.getName());
			builder.iLike(d.country, filterTO.getCountry());
			builder.eq(b.years, filterTO.getYears());
		}
		return builder.getBuilder();
	}

	private Predicate createPredicateRums(String filter) {
		QDrink d = QDrink.drink;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.RUM);
		if (StringUtils.isNotBlank(filter)) {
			String matchString = '%' + filter.replace('*', '%') + '%';
			builder.getBuilder()
					.and(ExpressionUtils.or(d.name.likeIgnoreCase(matchString), d.country.likeIgnoreCase(matchString)));
		}
		return builder.getBuilder();
	}

	@Override
	public long countRums(RumOverviewTO filterTO) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QRumInfo b = QRumInfo.rumInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateRums(filterTO)).fetchCount();
	}

	@Override
	public long countRums(String filter) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QRumInfo b = QRumInfo.rumInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateRums(filter)).fetchCount();
	}

	@Override
	public List<RumOverviewTO> findRums(RumOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order) {
		JPAQuery<RumOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QRumInfo r = QRumInfo.rumInfo;
		query.offset(offset).limit(limit);

		if (order == null || order.length == 0)
			order = QuerydslUtil.transformOrdering(new String[] { d.name.toString() }, new boolean[] { true });

		return query
				.select(new QRumOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, r.id, r.years,
						r.rumType))
				.from(d).join(r).on(d.drinkInfo.eq(r.id)).where(createPredicateRums(filterTO)).orderBy(order).fetch();
	}

	@Override
	public List<RumOverviewTO> findRums(String filter, PageRequest pageable) {
		JPAQuery<RumOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QRumInfo r = QRumInfo.rumInfo;
		QuerydslUtil.applyPagination(pageable, query);

		return query
				.select(new QRumOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, r.id, r.years,
						r.rumType))
				.from(d).join(r).on(d.drinkInfo.eq(r.id)).where(createPredicateRums(filter))
				.orderBy(QuerydslUtil.transformOrdering(new String[] { d.name.toString() }, new boolean[] { true }))
				.fetch();
	}

	@Override
	public RumTO findRumById(Long id) {
		JPAQuery<RumTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QRumInfo b = QRumInfo.rumInfo;
		return query.select(new QRumTO(d.id, d.name, d.type, d.rating, d.image, d.description, d.alcohol, d.country,
				b.id, b.years, b.rumType)).from(d).join(b).on(d.drinkInfo.eq(b.id)).where(d.id.eq(id)).fetchOne();
	}

	/*
	 * Whiskey
	 */

	private Predicate createPredicateWhiskeys(WhiskeyOverviewTO filterTO) {
		QDrink d = QDrink.drink;
		QWhiskeyInfo b = QWhiskeyInfo.whiskeyInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.WHISKY);
		if (filterTO != null) {
			builder.eq(b.whiskeyType, filterTO.getWhiskeyType());
			builder.iLike(d.name, filterTO.getName());
			builder.iLike(d.country, filterTO.getCountry());
			builder.eq(b.years, filterTO.getYears());
		}
		return builder.getBuilder();
	}

	private Predicate createPredicateWhiskeys(String filter) {
		QDrink d = QDrink.drink;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.WHISKY);
		if (StringUtils.isNotBlank(filter)) {
			String matchString = '%' + filter.replace('*', '%') + '%';
			builder.getBuilder()
					.and(ExpressionUtils.or(d.name.likeIgnoreCase(matchString), d.country.likeIgnoreCase(matchString)));
		}
		return builder.getBuilder();
	}

	@Override
	public long countWhiskeys(WhiskeyOverviewTO filterTO) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWhiskeyInfo b = QWhiskeyInfo.whiskeyInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateWhiskeys(filterTO)).fetchCount();
	}

	@Override
	public long countWhiskeys(String filter) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWhiskeyInfo b = QWhiskeyInfo.whiskeyInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateWhiskeys(filter)).fetchCount();
	}

	@Override
	public List<WhiskeyOverviewTO> findWhiskeys(WhiskeyOverviewTO filterTO, int offset, int limit,
			OrderSpecifier<?>[] order) {
		JPAQuery<WhiskeyOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWhiskeyInfo w = QWhiskeyInfo.whiskeyInfo;
		query.offset(offset).limit(limit);

		if (order == null || order.length == 0)
			order = QuerydslUtil.transformOrdering(new String[] { d.name.toString() }, new boolean[] { true });

		return query
				.select(new QWhiskeyOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, w.id, w.years,
						w.whiskeyType))
				.from(d).join(w).on(d.drinkInfo.eq(w.id)).where(createPredicateWhiskeys(filterTO)).orderBy(order)
				.fetch();
	}

	@Override
	public List<WhiskeyOverviewTO> findWhiskeys(String filter, PageRequest pageable) {
		JPAQuery<WhiskeyOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWhiskeyInfo w = QWhiskeyInfo.whiskeyInfo;
		QuerydslUtil.applyPagination(pageable, query);

		return query
				.select(new QWhiskeyOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, w.id, w.years,
						w.whiskeyType))
				.from(d).join(w).on(d.drinkInfo.eq(w.id)).where(createPredicateWhiskeys(filter))
				.orderBy(QuerydslUtil.transformOrdering(new String[] { d.name.toString() }, new boolean[] { true }))
				.fetch();
	}

	@Override
	public WhiskeyTO findWhiskeyById(Long id) {
		JPAQuery<WhiskeyTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWhiskeyInfo b = QWhiskeyInfo.whiskeyInfo;
		return query
				.select(new QWhiskeyTO(d.id, d.name, d.type, d.rating, d.image, d.description, d.alcohol, d.country,
						b.id, b.years, b.whiskeyType))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(d.id.eq(id)).fetchOne();
	}

	/*
	 * Wine
	 */

	private Predicate createPredicateWines(WineOverviewTO filterTO) {
		QDrink d = QDrink.drink;
		QWineInfo b = QWineInfo.wineInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.WINE);
		if (filterTO != null) {
			builder.eq(b.wineType, filterTO.getWineType());
			builder.iLike(d.name, filterTO.getName());
			builder.iLike(d.country, filterTO.getCountry());
			builder.iLike(b.winery, filterTO.getWinery());
			builder.eq(b.year, filterTO.getYear());
		}
		return builder.getBuilder();
	}

	@Override
	public long countWines(WineOverviewTO filterTO) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWineInfo b = QWineInfo.wineInfo;
		return query.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(createPredicateWines(filterTO)).fetchCount();
	}

	@Override
	public List<WineOverviewTO> findWines(WineOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order) {
		JPAQuery<WineOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWineInfo w = QWineInfo.wineInfo;
		query.offset(offset).limit(limit);

		if (order == null || order.length == 0)
			order = QuerydslUtil.transformOrdering(new String[] { w.winery.toString(), d.name.toString() },
					new boolean[] { true, true });

		return query
				.select(new QWineOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, w.id, w.winery,
						w.year, w.wineType))
				.from(d).join(w).on(d.drinkInfo.eq(w.id)).where(createPredicateWines(filterTO)).orderBy(order).fetch();
	}

	@Override
	public WineTO findWineById(Long id) {
		JPAQuery<WineTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QWineInfo b = QWineInfo.wineInfo;
		return query
				.select(new QWineTO(d.id, d.name, d.type, d.rating, d.image, d.description, d.alcohol, d.country, b.id,
						b.winery, b.year, b.wineType))
				.from(d).join(b).on(d.drinkInfo.eq(b.id)).where(d.id.eq(id)).fetchOne();
	}

	/*
	 * Other
	 */

	private Predicate createPredicateOthers(OtherOverviewTO filterTO) {
		QDrink d = QDrink.drink;
		QOtherInfo b = QOtherInfo.otherInfo;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(d.type, DrinkType.OTHER);
		if (filterTO != null) {
			builder.iLike(b.ingredient, filterTO.getIngredient());
			builder.iLike(d.name, filterTO.getName());
			builder.iLike(d.country, filterTO.getCountry());
		}
		return builder.getBuilder();
	}

	@Override
	public long countOthers(OtherOverviewTO filterTO) {
		JPAQuery<Drink> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QOtherInfo o = QOtherInfo.otherInfo;
		return query.from(d).join(o).on(d.drinkInfo.eq(o.id)).where(createPredicateOthers(filterTO)).fetchCount();
	}

	@Override
	public List<OtherOverviewTO> findOthers(OtherOverviewTO filterTO, int offset, int limit,
			OrderSpecifier<?>[] order) {
		JPAQuery<OtherOverviewTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QOtherInfo o = QOtherInfo.otherInfo;
		query.offset(offset).limit(limit);

		if (order == null || order.length == 0)
			order = QuerydslUtil.transformOrdering(new String[] { d.name.toString() }, new boolean[] { true });

		return query
				.select(new QOtherOverviewTO(d.id, d.name, d.type, d.rating, d.alcohol, d.country, o.id, o.ingredient))
				.from(d).join(o).on(d.drinkInfo.eq(o.id)).where(createPredicateOthers(filterTO)).orderBy(order).fetch();
	}

	@Override
	public OtherTO findOtherById(Long id) {
		JPAQuery<OtherTO> query = new JPAQuery<>(entityManager);
		QDrink d = QDrink.drink;
		QOtherInfo b = QOtherInfo.otherInfo;
		return query.select(new QOtherTO(d.id, d.name, d.type, d.rating, d.image, d.description, d.alcohol, d.country,
				b.id, b.ingredient)).from(d).join(b).on(d.drinkInfo.eq(b.id)).where(d.id.eq(id)).fetchOne();
	}
}
