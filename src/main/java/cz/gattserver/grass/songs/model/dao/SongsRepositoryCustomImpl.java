package cz.gattserver.grass.songs.model.dao;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.core.model.util.QuerydslUtil;
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
	public List<SongOverviewTO> findSongs(
			SongOverviewTO filterTO, int offset, int limit,
			OrderSpecifier<?>[] order) {
		JPAQuery<SongOverviewTO> query = new JPAQuery<>(entityManager);
		QSong s = QSong.song;
		query.offset(offset).limit(limit);
		query = query.select(new QSongOverviewTO(s.name, s.author, s.year, s.id, s.publicated)).from(s)
				.where(createPredicate(filterTO)).orderBy(order);
		if (order == null || order.length == 0) {
			query.orderBy(new OrderSpecifier<>(Order.ASC, s.name));
		} else {
			query.orderBy(order);
		}
		return query.fetch();
	}

	@Override
	public List<Long> findSongsIds(SongOverviewTO filterTO, OrderSpecifier<?>[] order) {
		JPAQuery<Long> query = new JPAQuery<>(entityManager);
		QSong s = QSong.song;
		query = query.select(s.id).from(s)
				.where(createPredicate(filterTO)).orderBy(order);
		if (order == null || order.length == 0) {
			query.orderBy(new OrderSpecifier<>(Order.ASC, s.name));
		} else {
			query.orderBy(order);
		}
		return query.fetch();
	}
}