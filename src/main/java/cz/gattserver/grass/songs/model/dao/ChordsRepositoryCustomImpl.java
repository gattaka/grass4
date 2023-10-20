package cz.gattserver.grass.songs.model.dao;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import cz.gattserver.grass.core.model.util.PredicateBuilder;
import cz.gattserver.grass.songs.model.domain.Chord;
import cz.gattserver.grass.songs.model.domain.QChord;
import cz.gattserver.grass.songs.model.interfaces.ChordTO;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ChordsRepositoryCustomImpl implements ChordsRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Chord> findAllOrderByName(ChordTO filterTO) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QChord c = QChord.chord;
		PredicateBuilder builder = new PredicateBuilder();
		builder.iLike(c.name, filterTO.getName());
		return query.select(c).from(c).where(builder.getBuilder()).orderBy(new OrderSpecifier<>(Order.ASC, c.name))
				.fetch();
	}
}
