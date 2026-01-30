package cz.gattserver.grass.language.facades.impl;

import java.util.Arrays;
import java.util.List;

import cz.gattserver.grass.core.util.DBCleanTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

import cz.gattserver.grass.language.facades.LanguageService;
import cz.gattserver.grass.language.model.dao.LanguageRepository;
import cz.gattserver.grass.language.model.domain.ItemType;
import cz.gattserver.grass.language.model.dto.LanguageItemTO;
import cz.gattserver.grass.language.model.dto.LanguageTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageServiceImplTest extends DBCleanTest {

	@Autowired
	private LanguageService languageService;

	@Autowired
	private LanguageRepository languageRepository;

	private LanguageTO createLanguage(String name) {
		LanguageTO languageTO = new LanguageTO();
		languageTO.setName(name);
		languageTO.setId(languageService.saveLanguage(languageTO));
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
		assertEquals(0, languageService.getLanguages().size());
		createLanguage("TestLanguage");
		createLanguage("TestLanguage2");
		assertEquals(2, languageService.getLanguages().size());
		assertEquals("TestLanguage", languageService.getLanguages().get(0).getName());
		assertEquals("TestLanguage2", languageService.getLanguages().get(1).getName());
	}

	@Test
	public void testCountLanguageItems() {
		assertEquals(0, languageService.countLanguageItems(new LanguageItemTO()));

		LanguageTO languageTO = createLanguage("testSaveLanguage");
		LanguageItemTO itemTO = new LanguageItemTO();
		itemTO.setContent("content");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("translation");
		languageService.saveLanguageItem(itemTO);

		assertEquals(1, languageService.countLanguageItems(new LanguageItemTO()));
		assertEquals(0, languageService.countLanguageItems(new LanguageItemTO().setContent("cc")));
		assertEquals(1, languageService.countLanguageItems(new LanguageItemTO().setContent("content")));

		itemTO = new LanguageItemTO();
		itemTO.setContent("content2");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("translation2");
		languageService.saveLanguageItem(itemTO);

		assertEquals(2, languageService.countLanguageItems(new LanguageItemTO()));
		assertEquals(2, languageService.countLanguageItems(new LanguageItemTO().setContent("co*")));
		assertEquals(1, languageService.countLanguageItems(new LanguageItemTO().setContent("content2")));
	}

	@Test
	public void testGetLanguageItems() {
		LanguageTO languageTO = createLanguage("testSaveLanguage");
		LanguageTO languageTO2 = createLanguage("testSaveLanguage2");

		LanguageItemTO itemTO = new LanguageItemTO();
		itemTO.setContent("aaa");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("trans1");
		languageService.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("trans2");
		languageService.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO2.getId());
		itemTO.setTranslation("trans3");
		languageService.saveLanguageItem(itemTO);

		List<LanguageItemTO> items = languageService.getLanguageItems(
				new LanguageItemTO().setLanguage(languageTO.getId()), 0, 5,
				Arrays.asList(new QuerySortOrder("content", SortDirection.ASCENDING)));
		assertEquals(2, items.size());
		assertEquals("aaa", items.get(0).getContent());
		assertEquals("abab", items.get(1).getContent());

		items = languageService.getLanguageItems(new LanguageItemTO().setLanguage(languageTO.getId()), 0, 5,
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
		languageService.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setTranslation("trans2");
		languageService.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO2.getId());
		itemTO.setTranslation("trans3");
		languageService.saveLanguageItem(itemTO);

		LanguageItemTO itemByContent = languageService.getLanguageItemByContent(languageTO.getId(), "abab");
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
		itemTO.setId(languageService.saveLanguageItem(itemTO));

		LanguageItemTO item = languageService.getLanguageItemById(itemTO.getId());
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
		languageService.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("abab");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setType(ItemType.WORD);
		itemTO.setTranslation("trans2");
		languageService.saveLanguageItem(itemTO);

		itemTO = new LanguageItemTO();
		itemTO.setContent("add");
		itemTO.setLanguage(languageTO.getId());
		itemTO.setType(ItemType.WORD);
		itemTO.setTranslation("trans3");
		languageService.saveLanguageItem(itemTO);

		List<LanguageItemTO> items = languageService.getLanguageItemsForTest(languageTO.getId(), 0, 1, 10,
				ItemType.PHRASE);
		assertEquals(1, items.size());
		assertEquals("aaa", items.get(0).getContent());

		items = languageService.getLanguageItemsForTest(languageTO.getId(), 0, 1, 10, ItemType.WORD);
		assertEquals(2, items.size());
		assertEquals("abab", items.get(0).getContent());
		assertEquals("add", items.get(1).getContent());

		items = languageService.getLanguageItemsForTest(languageTO.getId(), 0, 1, 10, null);
		assertEquals(3, items.size());
		assertEquals("aaa", items.get(0).getContent());
		assertEquals("abab", items.get(1).getContent());
		assertEquals("add", items.get(2).getContent());
	}

}
