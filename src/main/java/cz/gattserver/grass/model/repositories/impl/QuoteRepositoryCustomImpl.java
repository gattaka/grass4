package cz.gattserver.grass.model.repositories.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass.model.domain.QQuote;
import cz.gattserver.grass.model.repositories.QuoteRepositoryCustom;

@Repository
public class QuoteRepositoryCustomImpl implements QuoteRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public String findRandom(long randomIndex) {
		JPAQuery<String> query = new JPAQuery<>(entityManager);
		QQuote q = QQuote.quote;
		return query.select(q.name).from(q).orderBy(q.id.asc()).offset(randomIndex).limit(1).fetchOne();
	}

}
