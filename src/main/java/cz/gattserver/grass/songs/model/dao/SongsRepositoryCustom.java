package cz.gattserver.grass.songs.model.dao;

import com.vaadin.flow.component.grid.GridSortOrder;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;

import java.util.List;

public interface SongsRepositoryCustom {

	long count(SongOverviewTO filterTO);

	List<SongOverviewTO> findOrderByName(SongOverviewTO filterTO, int offset, int limit);

	List<SongOverviewTO> find(SongOverviewTO filterTO, List<GridSortOrder<SongOverviewTO>> list);

}
