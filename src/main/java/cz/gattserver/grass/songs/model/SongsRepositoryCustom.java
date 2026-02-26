package cz.gattserver.grass.songs.model;

import com.querydsl.core.types.OrderSpecifier;
import cz.gattserver.grass.songs.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.interfaces.SongTO;

import java.util.List;

public interface SongsRepositoryCustom {

	long count(SongOverviewTO filterTO);

	List<SongOverviewTO> findSongs(SongOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order);

	List<Long> findSongsIds(SongOverviewTO filterTO, OrderSpecifier<?>[] order);

    SongTO findAndMapById(Long id);

}