package cz.gattserver.grass.songs.model.dao;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.songs.model.domain.QSong;
import cz.gattserver.grass.songs.model.interfaces.QSongOverviewTO;
import cz.gattserver.grass.songs.model.interfaces.SongOverviewTO;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
public class SongsRepositoryCustomImpl implements SongsRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicate(SongOverviewTO filterTO) {
		QSong s = QSong.song;
		PredicateBuilder builder = new PredicateBuilder();
		builder.iLike(s.name, filterTO.getName());
		builder.iLike(s.author, filterTO.getAuthor());
		builder.eq(s.year, filterTO.getYear());
		if (filterTO.getPublicated() != null && filterTO.getPublicated())
			builder.getBuilder().andAnyOf(s.publicated.isNull(), s.publicated.isTrue());
		return builder.getBuilder();
	}

	@Override
	public long count(SongOverviewTO filterTO) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QSong s = QSong.song;
		return query.from(s).where(createPredicate(filterTO)).fetchCount();
	}

	@Override
	public List<SongOverviewTO> findOrderByName(SongOverviewTO filterTO, int offset, int limit) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QSong s = QSong.song;
		query.offset(offset).limit(limit);
		return query.select(new QSongOverviewTO(s.name, s.author, s.year, s.id, s.publicated)).from(s)
				.where(createPredicate(filterTO)).orderBy(new OrderSpecifier<>(Order.ASC, s.name)).fetch();
	}

	@Override
	public List<SongOverviewTO> find(SongOverviewTO filterTO, List<GridSortOrder<SongOverviewTO>> list) {
		JPAQuery<SongOverviewTO> query = new JPAQuery<>(entityManager);
		QSong s = QSong.song;
		query = query.select(new QSongOverviewTO(s.name, s.author, s.year, s.id, s.publicated)).from(s)
				.where(createPredicate(filterTO));
		if (list.isEmpty())
			query = query.orderBy(new OrderSpecifier<>(Order.ASC, s.name));
		for (GridSortOrder<SongOverviewTO> o : list) {
			Order order = o.getDirection() == SortDirection.ASCENDING ? Order.ASC : Order.DESC;
			if (o.getSorted().getKey() != null) {
				switch (o.getSorted().getKey()) {
				case "name":
					query = query.orderBy(new OrderSpecifier<>(order, s.name));
					break;
				case "author":
					query = query.orderBy(new OrderSpecifier<>(order, s.author));
					break;
				case "year":
					query = query.orderBy(new OrderSpecifier<>(order, s.year));
					break;
				}
			} else {
				query = query.orderBy(new OrderSpecifier<>(order, s.name));
			}
		}
		return query.fetch();
	}
}
