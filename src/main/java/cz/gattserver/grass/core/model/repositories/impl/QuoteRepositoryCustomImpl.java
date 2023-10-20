package cz.gattserver.grass.core.model.repositories.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import cz.gattserver.grass.core.model.domain.QQuote;
import cz.gattserver.grass.core.model.repositories.QuoteRepositoryCustom;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

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
