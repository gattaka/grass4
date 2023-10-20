package cz.gattserver.grass.core.model.repositories.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import cz.gattserver.grass.core.model.domain.QContentTag;
import cz.gattserver.grass.core.model.repositories.ContentTagRepositoryCustom;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;

@Repository
public class ContentTagRepositoryCustomImpl implements ContentTagRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public int countContentTagContents(Long id) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		return query.select(c.contentNodes.size()).from(c).where(c.id.eq(id)).fetchOne();
	}

	@Override
	public List<String> findByFilter(String filter, int offset, int limit) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		query.offset(offset);
		query.limit(limit);
		return query.select(c.name).from(c).where(c.name.lower().like("%" + filter.toLowerCase() + "%")).fetch();
	}

	@Override
	public Integer countByFilter(String filter) {
		JPAQuery<Integer> query = new JPAQuery<>(entityManager);
		QContentTag c = QContentTag.contentTag;
		return (int) query.select(c).from(c).where(c.name.lower().like("%" + filter.toLowerCase() + "%")).fetchCount();
	}

}
