package cz.gattserver.grass.rest;

import cz.gattserver.grass.songs.service.SongsService;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.model.interfaces.SongTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/ws/songs")
public class SongsResource {

	private final SongsService songsService;

    public SongsResource(SongsService songsService) {
        this.songsService = songsService;
    }

    @RequestMapping("/list")
	public ResponseEntity<List<SongOverviewTO>> list(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize,
			@RequestParam(value = "filter", required = false) String filter) {
		SongOverviewTO overviewTO = new SongOverviewTO();
		overviewTO.setName(filter);
		int count = songsService.getSongsCount(overviewTO);
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(songsService.getSongs(overviewTO, page * pageSize, pageSize, null), HttpStatus.OK);
	}

	@RequestMapping("/count")
	public ResponseEntity<Integer> count(@RequestParam(value = "filter", required = false) String filter) {
		SongOverviewTO overviewTO = new SongOverviewTO();
		overviewTO.setName(filter);
		return new ResponseEntity<>(songsService.getSongsCount(overviewTO), HttpStatus.OK);
	}

	@RequestMapping("/song")
	public @ResponseBody SongTO song(@RequestParam(value = "id", required = true) Long id) {
		return songsService.getSongById(id);
	}
}