package cz.gattserver.grass.language.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.language.model.domain.Language;
import cz.gattserver.grass.language.model.domain.LanguageItem;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;
import cz.gattserver.grass.language.model.dto.LanguageTO;

@Component("languageMapper")
public class Mapper {

	/**
	 * Převede {@link LanguageItem} na {@link LanguageItemTO}
	 * 
	 * @param e
	 * @return
	 */
	public LanguageItemTO mapLanguageItem(LanguageItem e) {
		if (e == null)
			return null;

		LanguageItemTO itemTO = new LanguageItemTO();

		itemTO.setId(e.getId());
		itemTO.setContent(e.getContent());
		itemTO.setLanguage(e.getLanguage().getId());
		itemTO.setLastTested(e.getLastTested());
		itemTO.setTested(e.getTested());
		itemTO.setSuccessRate(e.getSuccessRate());
		itemTO.setTranslation(e.getTranslation());
		itemTO.setType(e.getType());

		return itemTO;
	}

	/**
	 * Převede list {@link LanguageItem} na list {@link LanguageItemTO}
	 * 
	 * @param items
	 * @return
	 */
	public List<LanguageItemTO> mapLanguageItems(Collection<LanguageItem> items) {
		List<LanguageItemTO> tos = new ArrayList<>();
		for (LanguageItem e : items)
			tos.add(mapLanguageItem(e));
		return tos;
	}

	/**
	 * Převede {@link LanguageItemTO} na {@link LanguageItem}
	 * 
	 * @param e
	 * @return
	 */
	public LanguageItem mapLanguageItem(LanguageItemTO e) {
		if (e == null)
			return null;

		LanguageItem item = new LanguageItem();

		item.setId(e.getId());
		item.setContent(e.getContent());

		Language language = new Language();
		language.setId(e.getLanguage());
		item.setLanguage(language);

		item.setLastTested(e.getLastTested());
		item.setTested(e.getTested());
		item.setSuccessRate(e.getSuccessRate());
		item.setTranslation(e.getTranslation());
		item.setType(e.getType());

		return item;
	}

	/**
	 * Převede {@link Language} na {@link LanguageTO}
	 * 
	 * @param e
	 * @return
	 */
	public LanguageTO mapLanguage(Language e) {
		if (e == null)
			return null;

		LanguageTO to = new LanguageTO();

		to.setId(e.getId());
		to.setName(e.getName());

		return to;
	}

	/**
	 * Převede list {@link Language} na list {@link LanguageTO}
	 * 
	 * @param items
	 * @return
	 */
	public List<LanguageTO> mapLanguages(Collection<Language> items) {
		List<LanguageTO> tos = new ArrayList<>();
		for (Language e : items)
			tos.add(mapLanguage(e));
		return tos;
	}
}