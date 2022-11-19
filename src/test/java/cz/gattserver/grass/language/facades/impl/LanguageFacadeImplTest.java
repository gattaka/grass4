package cz.gattserver.grass.language.facades.impl;

import java.util.Arrays;
import java.util.List;

import cz.gattserver.grass.core.util.DBCleanTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import cz.gattserver.grass.language.facades.LanguageFacade;
import cz.gattserver.grass.language.model.dao.LanguageRepository;
import cz.gattserver.grass.language.model.domain.ItemType;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;
import cz.gattserver.grass.language.model.dto.LanguageTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageFacadeImplTest extends DBCleanTest {

	@Autowired
	private LanguageFacade languageFacade;

	@Autowired
	private LanguageRepository languageRepository;

	private LanguageTO createLanguage(String name) {
		LanguageTO languageTO = new LanguageTO();
		languageTO.setName(name);
		languageTO.setId(languageFacade.saveLanguage(languageTO));
		return languageTO;
	}

	@Test
	public void testSaveLanguage() {
		assertEquals(0, languageRepository.count());
		createLanguage("testSaveLanguage");
		assertEquals(1, languageRepository.count());
		assertEquals("testSaveLanguage", languageRepository.findAll().get(0).getName());
	}

	@Test
	public void testGetLanguages() {
		assertEquals(0, languageFacade.getLanguages().size());
		createLanguage("TestLanguage");
		createLanguage("TestLanguage2");
		assertEquals(2, languageFacade.getLanguages().size());
		assertEquals("TestLanguage", languageFacade.getLanguages().get(0).getName());
		assertEquals("TestLanguage2", languageFacade.getLanguages().get(1).getName());
	}

	@Test
	public void testCountLanguageItems() {
		assertEquals(0, languageFacade.countLanguageItems(new LanguageItemTO()));

		LanguageTO languageTO = createLanguage("testSaveLanguage");
		LanguageItemTO itemTO = new LanguageItemTO();
		itemTO.setContent("content");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("translation");
		languageFacade.saveLanguageItem(itemTO);

		assertEquals(1, languageFacade.countLanguageItems(new LanguageItemTO()));
		assertEquals(0, languageFacade.countLanguageItems(new LanguageItemTO().setContent("cc")));
		assertEquals(1, languageFacade.countLanguageItems(new LanguageItemTO().setContent("content")));

		itemTO = new LanguageItemTO();
		itemTO.setContent("content2");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("translation2");
		languageFacade.saveLanguageItem(itemTO);

		assertEquals(2, languageFacade.countLanguageItems(new LanguageItemTO()));
		assertEquals(2, languageFacade.countLanguageItems(new LanguageItemTO().setContent("co*")));
		assertEquals(1, languageFacade.countLanguageItems(new LanguageItemTO().setContent("content2")));
	}

	@Test
	public void testGetLanguageItems() {
		LanguageTO languageTO = createLanguage("testSaveLanguage");
		LanguageTO languageTO2 = createLanguage("testSaveLanguage2");

		LanguageItemTO itemTO = new LanguageItemTO();
		itemTO.setContent("aaa");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("trans1");
		languageFacade.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("trans2");
		languageFacade.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO2.getId());
		itemTO.setTranslation("trans3");
		languageFacade.saveLanguageItem(itemTO);

		List<LanguageItemTO> items = languageFacade.getLanguageItems(
				new LanguageItemTO().setLanguage(languageTO.getId()), 0, 5,
				Arrays.asList(new QuerySortOrder("content", SortDirection.ASCENDING)));
		assertEquals(2, items.size());
		assertEquals("aaa", items.get(0).getContent());
		assertEquals("abab", items.get(1).getContent());

		items = languageFacade.getLanguageItems(new LanguageItemTO().setLanguage(languageTO.getId()), 0, 5,
				Arrays.asList(new QuerySortOrder("content", SortDirection.DESCENDING)));
		assertEquals(2, items.size());
		assertEquals("abab", items.get(0).getContent());
		assertEquals("aaa", items.get(1).getContent());
	}

	@Test
	public void testGetLanguagItemByContent() {
		LanguageTO languageTO = createLanguage("testSaveLanguage");
		LanguageTO languageTO2 = createLanguage("testSaveLanguage2");

		LanguageItemTO itemTO = new LanguageItemTO();
		itemTO.setContent("aaa");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("trans1");
		languageFacade.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("trans2");
		languageFacade.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO2.getId());
		itemTO.setTranslation("trans3");
		languageFacade.saveLanguageItem(itemTO);

		LanguageItemTO itemByContent = languageFacade.getLanguageItemByContent(languageTO.getId(), "abab");
		assertEquals("trans2", itemByContent.getTranslation());
	}

	@Test
	public void testGetLanguagItemById() {
		LanguageTO languageTO = createLanguage("testSaveLanguage");

		LanguageItemTO itemTO = new LanguageItemTO();
		itemTO.setContent("aaa");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("trans1");
		itemTO.setType(ItemType.PHRASE);
		itemTO.setId(languageFacade.saveLanguageItem(itemTO));

		LanguageItemTO item = languageFacade.getLanguageItemById(itemTO.getId());
		assertEquals("aaa", item.getContent());
		assertEquals("trans1", item.getTranslation());
		assertEquals(ItemType.PHRASE, item.getType());
	}

	@Test
	public void testGetLanguagItemsForTest() {
		LanguageTO languageTO = createLanguage("testSaveLanguage");

		LanguageItemTO itemTO = new LanguageItemTO();
		itemTO.setContent("aaa");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setType(ItemType.PHRASE);
		itemTO.setTranslation("trans1");
		languageFacade.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setType(ItemType.WORD);
		itemTO.setTranslation("trans2");
		languageFacade.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("add");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setType(ItemType.WORD);
		itemTO.setTranslation("trans3");
		languageFacade.saveLanguageItem(itemTO);

		List<LanguageItemTO> items = languageFacade.getLanguageItemsForTest(languageTO.getId(), 0, 1, 10,
				ItemType.PHRASE);
		assertEquals(1, items.size());
		assertEquals("aaa", items.get(0).getContent());

		items = languageFacade.getLanguageItemsForTest(languageTO.getId(), 0, 1, 10, ItemType.WORD);
		assertEquals(2, items.size());
		assertEquals("abab", items.get(0).getContent());
		assertEquals("add", items.get(1).getContent());

		items = languageFacade.getLanguageItemsForTest(languageTO.getId(), 0, 1, 10, null);
		assertEquals(3, items.size());
		assertEquals("aaa", items.get(0).getContent());
		assertEquals("abab", items.get(1).getContent());
		assertEquals("add", items.get(2).getContent());
	}

}
