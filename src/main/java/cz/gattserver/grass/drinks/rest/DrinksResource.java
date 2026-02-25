package cz.gattserver.grass.drinks.rest;

import cz.gattserver.grass.drinks.service.DrinksService;
import cz.gattserver.grass.drinks.model.interfaces.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/ws/drinks")
public class DrinksResource {

	private final DrinksService drinksService;

    public DrinksResource(DrinksService drinksService) {
        this.drinksService = drinksService;
    }

    /*
	 * Pivo
	 */

	@RequestMapping("/beer-list")
	public ResponseEntity<List<BeerOverviewTO>> beerList(@RequestParam(value = "page") int page,
														 @RequestParam(value = "pageSize") int pageSize,
														 @RequestParam(value = "filter", required = false) String filter) {
		int count = drinksService.countBeers(filter);
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(drinksService.getBeers(filter, page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/beer-count")
	public ResponseEntity<Integer> beerCount(@RequestParam(value = "filter", required = false) String filter) {
		return new ResponseEntity<>(drinksService.countBeers(filter), HttpStatus.OK);
	}

	@RequestMapping("/beer")
	public @ResponseBody BeerTO beer(@RequestParam(value = "id") Long id) {
		return drinksService.getBeerById(id);
	}

	/*
	 * Rum
	 */

	@RequestMapping("/rum-list")
	public ResponseEntity<List<RumOverviewTO>> rumList(@RequestParam(value = "page") int page,
													   @RequestParam(value = "pageSize") int pageSize,
													   @RequestParam(value = "filter", required = false) String filter) {
		int count = drinksService.countRums(filter);
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(drinksService.getRums(filter, page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/rum-count")
	public ResponseEntity<Integer> rumCount(@RequestParam(value = "filter", required = false) String filter) {
		return new ResponseEntity<>(drinksService.countRums(filter), HttpStatus.OK);
	}

	@RequestMapping("/rum")
	public @ResponseBody RumTO rum(@RequestParam(value = "id"
    ) Long id) {
		return drinksService.getRumById(id);
	}

	/*
	 * Whiskey
	 */

	@RequestMapping("/whiskey-list")
	public ResponseEntity<List<WhiskeyOverviewTO>> whiskeyList(@RequestParam(value = "page") int page,
															   @RequestParam(value = "pageSize") int pageSize,
															   @RequestParam(value = "filter", required = false) String filter) {
		int count = drinksService.countWhiskeys(filter);
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(drinksService.getWhiskeys(filter, page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/whiskey-count")
	public ResponseEntity<Integer> whiskeyCount(@RequestParam(value = "filter", required = false) String filter) {
		return new ResponseEntity<>(drinksService.countWhiskeys(filter), HttpStatus.OK);
	}

	@RequestMapping("/whiskey")
	public @ResponseBody WhiskeyTO whiskey(@RequestParam(value = "id") Long id) {
		return drinksService.getWhiskeyById(id);
	}

	/*
	 * Wine
	 */

	@RequestMapping("/wine-list")
	public ResponseEntity<List<WineOverviewTO>> wineList(@RequestParam(value = "page") int page,
														 @RequestParam(value = "pageSize") int pageSize) {
		int count = drinksService.countWines();
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(drinksService.getWines(page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/wine-count")
	public ResponseEntity<Integer> wineCount() {
		return new ResponseEntity<>(drinksService.countWines(), HttpStatus.OK);
	}

	@RequestMapping("/wine")
	public @ResponseBody WineTO wine(@RequestParam(value = "id") Long id) {
		return drinksService.getWineById(id);
	}

	/*
	 * Other
	 */

	@RequestMapping("/other-list")
	public ResponseEntity<List<OtherOverviewTO>> otherList(@RequestParam(value = "page") int page,
														   @RequestParam(value = "pageSize") int pageSize) {
		int count = drinksService.countOthers();
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(drinksService.getOthers(page, pageSize), HttpStatus.OK);
	}

	@RequestMapping("/other-count")
	public ResponseEntity<Integer> otherCount() {
		return new ResponseEntity<>(drinksService.countOthers(), HttpStatus.OK);
	}

	@RequestMapping("/other")
	public @ResponseBody OtherTO other(@RequestParam(value = "id") Long id) {
		return drinksService.getOtherById(id);
	}

}
