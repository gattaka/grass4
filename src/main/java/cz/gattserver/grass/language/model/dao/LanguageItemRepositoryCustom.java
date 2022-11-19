package cz.gattserver.grass.language.model.dao;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass.language.model.domain.LanguageItem;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;

public interface LanguageItemRepositoryCustom {

	long countAllByLanguage(LanguageItemTO filterTO);

	List<LanguageItem> findAllByLanguageSortByName(LanguageItemTO filterTO, int offset, int limit,
			OrderSpecifier<?>[] order);

}
