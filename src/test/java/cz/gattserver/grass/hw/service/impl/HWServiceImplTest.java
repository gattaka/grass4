package cz.gattserver.grass.hw.service.impl;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass.core.mock.MockFileSystemService;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.ui.util.ImageComparator;
import cz.gattserver.grass.core.util.DBCleanTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;


import cz.gattserver.grass.hw.HWConfiguration;
import cz.gattserver.grass.hw.interfaces.HWFilterTO;
import cz.gattserver.grass.hw.interfaces.HWItemFileTO;
import cz.gattserver.grass.hw.interfaces.HWItemOverviewTO;
import cz.gattserver.grass.hw.interfaces.HWItemState;
import cz.gattserver.grass.hw.interfaces.HWItemTO;
import cz.gattserver.grass.hw.interfaces.HWItemTypeTO;
import cz.gattserver.grass.hw.service.HWService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HWServiceImplTest extends DBCleanTest {

	@Autowired
	private MockFileSystemService fileSystemService;

	@Autowired
	private HWService hwService;

	@Autowired
	private ConfigurationService configurationService;

	@BeforeEach
	public void init() {
		fileSystemService.init();
	}

	private Path prepareFS(FileSystem fs) throws IOException {
		Path rootDir = fs.getPath("/some/path/hw/root/");
		Files.createDirectories(rootDir);

		HWConfiguration conf = new HWConfiguration();
		conf.setRootDir(rootDir.toString());
		configurationService.saveConfiguration(conf);

		return rootDir;
	}

	/*
	 * Images
	 */

	@Test
	public void saveImagesFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage.jpg", itemTO);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve(conf.getImagesDir()).resolve("testImage.jpg");
		assertTrue(Files.exists(smallFile));
	}

	@Test
	public void getHWItemImagesFiles() throws IOException {
		prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);

		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage1.jpg", itemTO);
		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage2.jpg", itemTO);

		List<HWItemFileTO> files = hwService.getHWItemImagesFiles(itemTO.getId());
		assertEquals(2, files.size());
		assertEquals("testImage1.jpg", files.get(0).getName());
		assertEquals("testImage2.jpg", files.get(1).getName());
	}

	@Test
	public void getHWItemImagesFileInputStream() throws IOException {
		prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage.jpg", itemTO);

		InputStream is = hwService.getHWItemImagesFileInputStream(itemTO.getId(), "testImage.jpg");
		assertTrue(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("large.jpg"), is));
	}

	@Test
	public void deleteHWItemImagesFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveImagesFile(this.getClass().getResourceAsStream("large.jpg"), "testImage.jpg", itemTO);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve(conf.getImagesDir()).resolve("testImage.jpg");
		assertTrue(Files.exists(smallFile));

		hwService.deleteHWItemImagesFile(itemTO.getId(), "testImage.jpg");

		assertFalse(Files.exists(smallFile));
	}

	/*
	 * Documents
	 */

	@Test
	public void saveDocumentsFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc.jpg", itemTO.getId());

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve(conf.getDocumentsDir()).resolve("testDoc.jpg");
		assertTrue(Files.exists(smallFile));
	}

	@Test
	public void getHWItemDocumentsFiles() throws IOException {
		prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);

		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc1.jpg", itemTO.getId());
		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc2.jpg", itemTO.getId());

		List<HWItemFileTO> files = hwService.getHWItemDocumentsFiles(itemTO.getId());
		assertEquals(2, files.size());
		assertEquals("testDoc1.jpg", files.get(0).getName());
		assertEquals("testDoc2.jpg", files.get(1).getName());
	}

	@Test
	public void getHWItemDocumentsFileInputStream() throws IOException {
		prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc.jpg", itemTO.getId());

		InputStream is = hwService.getHWItemDocumentsFileInputStream(itemTO.getId(), "testDoc.jpg");
		assertTrue(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("large.jpg"), is));
	}

	@Test
	public void deleteHWItemDocumentsFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		hwService.saveDocumentsFile(this.getClass().getResourceAsStream("large.jpg"), "testDoc.jpg", itemTO.getId());

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve(conf.getDocumentsDir()).resolve("testDoc.jpg");
		assertTrue(Files.exists(smallFile));

		hwService.deleteHWItemDocumentsFile(itemTO.getId(), "testDoc.jpg");

		assertFalse(Files.exists(smallFile));
	}

	/*
	 * Icons
	 */

	@Test
	public void createHWItemIconOutputStream() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");
		Files.createDirectories(hwDir);

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		OutputStream os = hwService.createHWItemIconOutputStream("testIcon.jpg", itemTO.getId());
		IOUtils.copy(this.getClass().getResourceAsStream("large.jpg"), os);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve("icon.jpg");
		assertTrue(Files.exists(smallFile));
	}

	@Test
	public void getHWItemIconFileInputStream() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");
		Files.createDirectories(hwDir);

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		OutputStream os = hwService.createHWItemIconOutputStream("testIcon.jpg", itemTO.getId());
		IOUtils.copy(this.getClass().getResourceAsStream("large.jpg"), os);

		InputStream is = hwService.getHWItemIconFileInputStream(itemTO.getId());
		assertTrue(ImageComparator.isEqualAsFiles(this.getClass().getResourceAsStream("large.jpg"), is));
	}

	@Test
	public void deleteHWItemIconFile() throws IOException {
		Path hwDir = prepareFS(fileSystemService.getFileSystem()).resolve("123456");
		Files.createDirectories(hwDir);

		HWItemTO itemTO = new HWItemTO();
		itemTO.setId(123456L);
		OutputStream os = hwService.createHWItemIconOutputStream("testIcon.jpg", itemTO.getId());
		IOUtils.copy(this.getClass().getResourceAsStream("large.jpg"), os);

		HWConfiguration conf = new HWConfiguration();
		configurationService.loadConfiguration(conf);

		Path smallFile = hwDir.resolve("icon.jpg");
		assertTrue(Files.exists(smallFile));

		hwService.deleteHWItemIconFile(itemTO.getId());
		assertFalse(Files.exists(smallFile));
	}

	/*
	 * Item types
	 */

	@Test
	public void saveHWType() {
		HWItemTypeTO hwItemTypeTO = new HWItemTypeTO();
		hwItemTypeTO.setName("myš");
		Long id = hwService.saveHWType(hwItemTypeTO);

		hwItemTypeTO = hwService.getHWItemType(id);
		assertEquals("myš", hwItemTypeTO.getName());
	}

	@Test
	public void getAllHWTypes() {
		HWItemTypeTO hwItemTypeTO = new HWItemTypeTO();
		hwItemTypeTO.setName("myš");
		hwService.saveHWType(hwItemTypeTO);

		hwItemTypeTO = new HWItemTypeTO();
		hwItemTypeTO.setName("notebook");
		hwService.saveHWType(hwItemTypeTO);

		Set<HWItemTypeTO> types = hwService.getAllHWTypes();

		assertEquals(2, types.size());
		Iterator<HWItemTypeTO> it = types.iterator();
		assertEquals("myš", it.next().getName());
		assertEquals("notebook", it.next().getName());
	}

	@Test
	public void deleteHWItemType() {
		HWItemTypeTO hwItemTypeTO = new HWItemTypeTO();
		hwItemTypeTO.setName("myš");
		Long id = hwService.saveHWType(hwItemTypeTO);

		hwItemTypeTO = new HWItemTypeTO();
		hwItemTypeTO.setName("notebook");
		hwService.saveHWType(hwItemTypeTO);

		Set<HWItemTypeTO> types = hwService.getAllHWTypes();
		assertEquals(2, types.size());

		hwService.deleteHWItemType(id);

		types = hwService.getAllHWTypes();
		assertEquals(1, types.size());
		assertEquals("notebook", types.iterator().next().getName());
	}

	/*
	 * Items
	 */

	@Test
	public void saveHWItem() {
		HWItemTO itemTO = new HWItemTO();
		itemTO.setName("test Name");
		itemTO.setDescription("test description");
		itemTO.setPrice(new BigDecimal(650.50));
		LocalDate purchDate = LocalDate.now();
		itemTO.setPurchaseDate(purchDate);
		itemTO.setState(HWItemState.BROKEN);
		itemTO.setSupervizedFor("Táta");

		HWItemTypeTO hwItemTypeTO = new HWItemTypeTO();
		hwItemTypeTO.setName("notebook");
		Long typeId = hwService.saveHWType(hwItemTypeTO);
		hwItemTypeTO.setId(typeId);
		Set<String> types = new HashSet<>();
		types.add(hwItemTypeTO.getName());

		itemTO.setTypes(types);
		itemTO.setUsedIn(null);
		itemTO.setUsedInName(null);
		itemTO.setWarrantyYears(2);

		Long id = hwService.saveHWItem(itemTO);

		HWItemTO savedItemTO = hwService.getHWItem(id);

		assertEquals("test Name", savedItemTO.getName());
		assertEquals("test description", savedItemTO.getDescription());
		assertEquals(0, new BigDecimal(650.50).compareTo(savedItemTO.getPrice()));
		assertEquals(purchDate, savedItemTO.getPurchaseDate());
		assertEquals(HWItemState.BROKEN, savedItemTO.getState());
		assertEquals("Táta", savedItemTO.getSupervizedFor());
		assertEquals(1, savedItemTO.getTypes().size());
		assertEquals("notebook", savedItemTO.getTypes().iterator().next());
		assertEquals(Integer.valueOf(2), savedItemTO.getWarrantyYears());
	}

	@Test
	public void testHWItemOperations() throws IOException {
		prepareFS(fileSystemService.getFileSystem());
		
		HWItemTO itemTO = new HWItemTO();
		itemTO.setName("test Name");
		itemTO.setDescription("test description");
		itemTO.setPrice(new BigDecimal(650.50));
		LocalDate purchDate = LocalDate.now();
		itemTO.setPurchaseDate(purchDate);
		itemTO.setState(HWItemState.BROKEN);
		itemTO.setSupervizedFor("Táta");

		HWItemTypeTO hwItemTypeTO = new HWItemTypeTO();
		hwItemTypeTO.setName("notebook");
		Long typeId = hwService.saveHWType(hwItemTypeTO);
		hwItemTypeTO.setId(typeId);
		Set<String> types = new HashSet<>();
		types.add(hwItemTypeTO.getName());

		itemTO.setTypes(types);
		itemTO.setUsedIn(null);
		itemTO.setUsedInName(null);
		itemTO.setWarrantyYears(2);

		Long id = hwService.saveHWItem(itemTO);

		HWItemTO itemTO2 = new HWItemTO();
		itemTO2.setName("test komponenta");
		itemTO2.setDescription("test description 2");
		itemTO2.setPrice(new BigDecimal(600.50));
		LocalDate purchDate2 = LocalDate.now().minusDays(3);
		itemTO2.setPurchaseDate(purchDate2);
		itemTO2.setState(HWItemState.DISASSEMBLED);

		HWItemTypeTO hwItemTypeTO2 = new HWItemTypeTO();
		hwItemTypeTO2.setName("RAM");
		Long typeId2 = hwService.saveHWType(hwItemTypeTO2);
		hwItemTypeTO2.setId(typeId2);
		Set<String> types2 = new HashSet<>();
		types2.add(hwItemTypeTO2.getName());

		List<HWItemOverviewTO> list = hwService.getAllHWItems();

		assertEquals(1, list.size());
		assertEquals(id, list.get(0).getId());

		itemTO2.setTypes(types2);
		itemTO2.setUsedIn(list.get(0));
		itemTO2.setUsedInName(list.get(0).getName());
		itemTO2.setWarrantyYears(1);

		Long id2 = hwService.saveHWItem(itemTO2);

		HWItemTO savedItemTO2 = hwService.getHWItem(id2);

		assertEquals("test komponenta", savedItemTO2.getName());
		assertEquals("test description 2", savedItemTO2.getDescription());
		assertEquals(0, new BigDecimal(600.50).compareTo(savedItemTO2.getPrice()));
		assertEquals(purchDate2, savedItemTO2.getPurchaseDate());
		assertEquals(HWItemState.DISASSEMBLED, savedItemTO2.getState());
		assertNull(savedItemTO2.getSupervizedFor());
		assertEquals(1, savedItemTO2.getTypes().size());
		assertEquals("RAM", savedItemTO2.getTypes().iterator().next());
		assertEquals(Integer.valueOf(1), savedItemTO2.getWarrantyYears());
		assertEquals(id, savedItemTO2.getUsedIn().getId());
		assertEquals("test Name", savedItemTO2.getUsedInName());

		assertEquals(2, hwService.countHWItems(new HWFilterTO()));
		assertEquals(1, hwService.countHWItems(new HWFilterTO().setUsedIn("test Name")));

		List<HWItemOverviewTO> items = hwService.getAllHWItems();
		assertEquals(2, items.size());
		assertEquals(id, items.get(0).getId());
		assertEquals(id2, items.get(1).getId());

		items = hwService.getAllParts(id);
		assertEquals(1, items.size());
		assertEquals(id2, items.get(0).getId());

		items = hwService.getHWItemsAvailableForPart(id);
		assertEquals(1, items.size());
		assertEquals(id2, items.get(0).getId());

		items = hwService.getHWItemsAvailableForPart(id2);
		assertEquals(1, items.size());
		assertEquals(id, items.get(0).getId());

		hwService.deleteHWItem(id);

		items = hwService.getAllHWItems();
		assertEquals(1, items.size());
		assertEquals(id2, items.get(0).getId());
	}

}
