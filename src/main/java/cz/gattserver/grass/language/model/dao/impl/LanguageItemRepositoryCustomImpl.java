package cz.gattserver.grass.language.model.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import cz.gattserver.grass.core.model.util.PredicateBuilder;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass.language.model.dao.LanguageItemRepositoryCustom;
import cz.gattserver.grass.language.model.domain.LanguageItem;
import cz.gattserver.grass.language.model.domain.QLanguageItem;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;

@Repository
public class LanguageItemRepositoryCustomImpl implements LanguageItemRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicateLanguageItems(LanguageItemTO filterTO) {
		QLanguageItem l = QLanguageItem.languageItem;
		PredicateBuilder builder = new PredicateBuilder();
		builder.eq(l.language.id, filterTO.getLanguage());
		builder.eq(l.type, filterTO.getType());
		builder.iLike(l.content, filterTO.getContent());
		builder.iLike(l.translation, filterTO.getTranslation());
		return builder.getBuilder();
	}

	@Override
	public long countAllByLanguage(LanguageItemTO filterTO) {
		JPAQuery<LanguageItem> query = new JPAQuery<>(entityManager);
		QLanguageItem l = QLanguageItem.languageItem;
		return query.from(l).where(createPredicateLanguageItems(filterTO)).fetchCount();
	}

	@Override
	public List<LanguageItem> findAllByLanguageSortByName(LanguageItemTO filterTO, int offset, int limit,
			OrderSpecifier<?>[] order) {
		JPAQuery<LanguageItem> query = new JPAQuery<>(entityManager);
		query.offset(offset).limit(limit);
		QLanguageItem l = QLanguageItem.languageItem;
		return query.from(l).where(createPredicateLanguageItems(filterTO)).orderBy(order).fetch();
	}

}
