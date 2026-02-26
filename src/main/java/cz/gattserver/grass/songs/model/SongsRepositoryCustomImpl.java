package cz.gattserver.grass.songs.model;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.songs.interfaces.QSongOverviewTO;
import cz.gattserver.grass.songs.interfaces.QSongTO;
import cz.gattserver.grass.songs.interfaces.SongOverviewTO;
import cz.gattserver.grass.songs.interfaces.SongTO;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class SongsRepositoryCustomImpl extends QuerydslRepositorySupport implements SongsRepositoryCustom {

    private final QSong s = QSong.song;

    public SongsRepositoryCustomImpl() {
        super(Song.class);
    }

    private Predicate createPredicate(SongOverviewTO filterTO) {
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
        return from(s).where(createPredicate(filterTO)).select(s.id).stream().count();
    }

    @Override
    public List<SongOverviewTO> findSongs(SongOverviewTO filterTO, int offset, int limit, OrderSpecifier<?>[] order) {
        JPQLQuery<SongOverviewTO> query = from(s).offset(offset).limit(limit)
                .select(new QSongOverviewTO(s.id, s.name, s.author, s.year, s.publicated)).from(s)
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
        JPQLQuery<Long> query = from(s).select(s.id).from(s).where(createPredicate(filterTO)).orderBy(order);
        if (order == null || order.length == 0) {
            query.orderBy(new OrderSpecifier<>(Order.ASC, s.name));
        } else {
            query.orderBy(order);
        }
        return query.fetch();
    }

    @Override
    public SongTO findAndMapById(Long id) {
        return from(s).select(new QSongTO(s.id, s.name, s.author, s.year, s.text, s.publicated, s.embedded))
                .where(s.id.eq(id)).fetchOne();
    }
}