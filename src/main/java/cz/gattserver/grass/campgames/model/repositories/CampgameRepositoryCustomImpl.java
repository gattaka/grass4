package cz.gattserver.grass.campgames.model.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cz.gattserver.grass.campgames.interfaces.CampgameFilterTO;
import cz.gattserver.grass.campgames.model.domain.Campgame;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass.campgames.model.domain.CampgameKeyword;
import cz.gattserver.grass.campgames.model.domain.QCampgame;
import cz.gattserver.grass.campgames.model.domain.QCampgameKeyword;
import cz.gattserver.grass.core.model.util.PredicateBuilder;

@Repository
public class CampgameRepositoryCustomImpl implements CampgameRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateCampgames(CampgameFilterTO filter) {
		QCampgame c = QCampgame.campgame;
		QCampgameKeyword k = QCampgameKeyword.campgameKeyword;
		PredicateBuilder builder = new PredicateBuilder();
		builder.iLike(c.name, filter.getName());
		builder.iLike(c.players, filter.getPlayers());
		builder.iLike(c.playTime, filter.getPlayTime());
		builder.iLike(c.preparationTime, filter.getPreparationTime());
		if (filter.getKeywords() != null)
			for (String type : filter.getKeywords()) {
				JPAQuery<CampgameKeyword> subQuery = new JPAQuery<>();
				subQuery.from(k).where(k.name.eq(type), c.keywords.contains(k));
				builder.exists(subQuery);
			}
		return builder.getBuilder();
	}

	@Override
	public long countCampgames(CampgameFilterTO filter) {
		JPAQuery<Campgame> query = new JPAQuery<>(entityManager);
		QCampgame c = QCampgame.campgame;
		return query.from(c).where(createPredicateCampgames(filter)).fetchCount();
	}

	@Override
	public List<Campgame> getCampgames(CampgameFilterTO filter, int offset, int limit, OrderSpecifier<?>[] order) {
		JPAQuery<Campgame> query = new JPAQuery<>(entityManager);
		query.limit(limit);
		query.offset(offset);
		QCampgame c = QCampgame.campgame;
		return query.from(c).where(createPredicateCampgames(filter)).orderBy(order).fetch();
	}

}
