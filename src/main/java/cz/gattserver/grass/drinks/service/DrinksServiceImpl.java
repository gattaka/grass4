package cz.gattserver.grass.drinks.service;

import com.vaadin.flow.data.provider.QuerySortOrder;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
import cz.gattserver.grass.drinks.model.dao.*;
import cz.gattserver.grass.drinks.model.domain.*;
import cz.gattserver.grass.drinks.model.interfaces.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Component
public class DrinksServiceImpl implements DrinksService {

	private final DrinkRepository drinkRepository;
	private final BeerInfoRepository beerInfoRepository;
	private final RumInfoRepository rumInfoRepository;
	private final WhiskeyInfoRepository whiskeyInfoRepository;
	private final WineInfoRepository wineInfoRepository;
	private final OtherInfoRepository otherInfoRepository;

    public DrinksServiceImpl(DrinkRepository drinkRepository, BeerInfoRepository beerInfoRepository,
                             RumInfoRepository rumInfoRepository, WhiskeyInfoRepository whiskeyInfoRepository,
                             WineInfoRepository wineInfoRepository, OtherInfoRepository otherInfoRepository) {
        this.drinkRepository = drinkRepository;
        this.beerInfoRepository = beerInfoRepository;
        this.rumInfoRepository = rumInfoRepository;
        this.whiskeyInfoRepository = whiskeyInfoRepository;
        this.wineInfoRepository = wineInfoRepository;
        this.otherInfoRepository = otherInfoRepository;
    }

    @Override
	public void deleteDrink(Long id) {
		drinkRepository.deleteById(id);
	}

	/*
	 * Piva
	 */
	@Override
	public int countBeers(String filter) {
		return (int) drinkRepository.countBeers(filter);
	}

	@Override
	public List<BeerOverviewTO> getBeers(String filter, int page, int size) {
		return drinkRepository.findBeers(filter, PageRequest.of(page, size));
	}

	@Override
	public int countBeers(BeerOverviewTO filterTO) {
		return (int) drinkRepository.countBeers(filterTO);
	}

	@Override
	public List<BeerOverviewTO> getBeers(BeerOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		return drinkRepository.findBeers(filterTO, offset, limit, QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public BeerTO getBeerById(Long id) {
		return drinkRepository.findBeerById(id);
	}

	private Drink createDrink(DrinkType type, DrinkTO to, Long drinkInfoId) {
		Drink d = new Drink();
		d.setName(to.getName());
		d.setType(type);
		d.setRating(to.getRating());
		d.setImage(to.getImage());
		d.setDescription(to.getDescription());
		d.setAlcohol(to.getAlcohol());
		d.setCountry(to.getCountry());
		d.setDrinkInfo(drinkInfoId);
		d.setId(to.getId());
		return d;
	}

	@Override
	public BeerTO saveBeer(BeerTO to) {
		BeerInfo b = new BeerInfo(to.getBrewery(), to.getIbu(), to.getDegrees(), to.getCategory(), to.getMaltType(),
				to.getMalts(), to.getHops());
		b.setId(to.getInfoId());
		b = beerInfoRepository.save(b);

		Drink d = createDrink(DrinkType.BEER, to, b.getId());
		d = drinkRepository.save(d);

		to.setId(d.getId());
		return to;
	}

	/*
	 * Rumy
	 */

	@Override
	public int countRums(String filter) {
		return (int) drinkRepository.countRums(filter);
	}

	@Override
	public List<RumOverviewTO> getRums(String filter, int page, int size) {
		return drinkRepository.findRums(filter, PageRequest.of(page, size));
	}

	@Override
	public int countRums(RumOverviewTO filterTO) {
		return (int) drinkRepository.countRums(filterTO);
	}

	@Override
	public List<RumOverviewTO> getRums(RumOverviewTO filterTO, int offset, int limit, List<QuerySortOrder> sortOrder) {
		return drinkRepository.findRums(filterTO, offset, limit, QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public RumTO getRumById(Long id) {
		return drinkRepository.findRumById(id);
	}

	@Override
	public RumTO saveRum(RumTO to) {
		RumInfo b = new RumInfo(to.getYears(), to.getRumType());
		b.setId(to.getInfoId());
		b = rumInfoRepository.save(b);

		Drink d = createDrink(DrinkType.RUM, to, b.getId());
		d = drinkRepository.save(d);

		to.setId(d.getId());
		return to;
	}

	/*
	 * Whiskey
	 */

	@Override
	public int countWhiskeys(String filter) {
		return (int) drinkRepository.countWhiskeys(filter);
	}

	@Override
	public List<WhiskeyOverviewTO> getWhiskeys(String filter, int page, int size) {
		return drinkRepository.findWhiskeys(filter, PageRequest.of(page, size));
	}

	@Override
	public int countWhiskeys(WhiskeyOverviewTO filterTO) {
		return (int) drinkRepository.countWhiskeys(filterTO);
	}

	@Override
	public List<WhiskeyOverviewTO> getWhiskeys(WhiskeyOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		return drinkRepository.findWhiskeys(filterTO, offset, limit, QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public WhiskeyTO getWhiskeyById(Long id) {
		return drinkRepository.findWhiskeyById(id);
	}

	@Override
	public WhiskeyTO saveWhiskey(WhiskeyTO to) {
		WhiskeyInfo b = new WhiskeyInfo(to.getYears(), to.getWhiskeyType());
		b.setId(to.getInfoId());
		b = whiskeyInfoRepository.save(b);

		Drink d = createDrink(DrinkType.WHISKY, to, b.getId());
		d = drinkRepository.save(d);

		to.setId(d.getId());
		return to;
	}

	/*
	 * Vína
	 */

	@Override
	public int countWines() {
		return (int) drinkRepository.countWines(null);
	}

	@Override
	public List<WineOverviewTO> getWines(int page, int size) {
		return drinkRepository.findWines(null, page * size, size, null);
	}

	@Override
	public int countWines(WineOverviewTO filterTO) {
		return (int) drinkRepository.countWines(filterTO);
	}

	@Override
	public List<WineOverviewTO> getWines(WineOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		return drinkRepository.findWines(filterTO, offset, limit, QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public WineTO getWineById(Long id) {
		return drinkRepository.findWineById(id);
	}

	@Override
	public WineTO saveWine(WineTO to) {
		WineInfo b = new WineInfo(to.getWinery(), to.getYear(), to.getWineType());
		b.setId(to.getInfoId());
		b = wineInfoRepository.save(b);
		to.setInfoId(b.getId());

		Drink d = createDrink(DrinkType.WINE, to, b.getId());
		d = drinkRepository.save(d);

		to.setId(d.getId());
		return to;
	}

	/*
	 * Jiné
	 */

	@Override
	public int countOthers() {
		return (int) drinkRepository.countOthers(null);
	}

	@Override
	public List<OtherOverviewTO> getOthers(int page, int size) {
		return drinkRepository.findOthers(null, page * size, size, null);
	}

	@Override
	public int countOthers(OtherOverviewTO filterTO) {
		return (int) drinkRepository.countOthers(filterTO);
	}

	@Override
	public List<OtherOverviewTO> getOthers(OtherOverviewTO filterTO, int offset, int limit,
			List<QuerySortOrder> sortOrder) {
		return drinkRepository.findOthers(filterTO, offset, limit, QuerydslUtil.transformOrdering(sortOrder, s -> s));
	}

	@Override
	public OtherTO getOtherById(Long id) {
		return drinkRepository.findOtherById(id);
	}

	@Override
	public OtherTO saveOther(OtherTO to) {
		OtherInfo b = new OtherInfo(to.getIngredient());
		b.setId(to.getInfoId());
		b = otherInfoRepository.save(b);
		to.setInfoId(b.getId());

		Drink d = createDrink(DrinkType.OTHER, to, b.getId());
		d = drinkRepository.save(d);

		to.setId(d.getId());
		return to;
	}
}
