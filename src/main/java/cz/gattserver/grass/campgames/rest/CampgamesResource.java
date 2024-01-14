package cz.gattserver.grass.campgames.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.campgames.interfaces.CampgameFilterTO;
import cz.gattserver.grass.campgames.interfaces.CampgameOverviewTO;
import cz.gattserver.grass.campgames.interfaces.CampgameTO;
import cz.gattserver.grass.campgames.service.CampgamesService;

@Controller
@RequestMapping("/ws/campgames")
public class CampgamesResource {

	@Autowired
	private CampgamesService campgamesService;

	@RequestMapping("/list")
	public ResponseEntity<List<CampgameOverviewTO>> list(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize) {
		int count = campgamesService.countCampgames(new CampgameFilterTO());
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(campgamesService.getCampgames(new CampgameFilterTO(), pageSize * page,
				pageSize * (page + 1), new OrderSpecifier[0]), HttpStatus.OK);
	}

	@RequestMapping("/count")
	public ResponseEntity<Integer> count() {
		return new ResponseEntity<>(campgamesService.countCampgames(new CampgameFilterTO()), HttpStatus.OK);
	}

	@RequestMapping("/campgame")
	public @ResponseBody CampgameTO campgame(@RequestParam(value = "id", required = true) Long id) {
		return campgamesService.getCampgame(id);
	}

}
