package cz.gattserver.grass.songs.model;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.songs.interfaces.ChordTO;
import cz.gattserver.grass.songs.interfaces.QChordTO;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ChordsRepositoryCustomImpl extends QuerydslRepositorySupport implements ChordsRepositoryCustom {

    private final QChord c = QChord.chord;

    public ChordsRepositoryCustomImpl() {
        super(Chord.class);
    }

    @Override
    public List<ChordTO> findAllOrderByName(ChordTO filterTO) {
        PredicateBuilder builder = new PredicateBuilder();
        JPQLQuery<Chord> query = from(c);
        builder.iLike(c.name, filterTO.getName());
        return query.select(new QChordTO(c.id, c.name, c.configuration)).from(c).where(builder.getBuilder())
                .orderBy(new OrderSpecifier<>(Order.ASC, c.name)).fetch();
    }

    @Override
    public ChordTO findAndMapById(Long id) {
        return from(c).select(new QChordTO(c.id, c.name, c.configuration)).from(c).where(c.id.eq(id))
                .orderBy(new OrderSpecifier<>(Order.ASC, c.name)).fetchOne();
    }

    @Override
    public ChordTO findByName(String name) {
        return from(c).select(new QChordTO(c.id, c.name, c.configuration)).from(c).where(c.name.eq(name))
                .orderBy(new OrderSpecifier<>(Order.ASC, c.name)).fetchOne();
    }
}