package cz.gattserver.grass.pg.impl;

import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.UnauthorizedAccessException;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.mock.CoreMockService;
import cz.gattserver.grass.core.mock.MockFileSystemService;
import cz.gattserver.grass.core.model.domain.ContentNode;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.ui.util.ImageComparator;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.core.util.MockUtils;
import cz.gattserver.grass.modules.PGModule;
import cz.gattserver.grass.pg.events.impl.PGEventsHandler;
import cz.gattserver.grass.pg.test.PGZipProcessMockEventsHandler;
import cz.gattserver.grass.pg.config.PGConfiguration;
import cz.gattserver.grass.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass.pg.interfaces.*;
import cz.gattserver.grass.pg.model.domain.Photogallery;
import cz.gattserver.grass.pg.model.repositories.PhotogalleryRepository;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.pg.util.ZIPUtils;
import cz.gattserver.grass.test.MockSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PGServiceImplTest extends DBCleanTest {

    private static Logger logger = LoggerFactory.getLogger(PGServiceImplTest.class);

    @Autowired
    private MockFileSystemService fileSystemService;

    @Autowired
    private MockSecurityService mockSecurityService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private PGService pgService;

    @Autowired
    private PhotogalleryRepository photogalleryRepository;

    @Autowired
    private CoreMockService coreMockService;

    @Autowired
    private ContentNodeService contentNodeService;

    @Autowired
    private EventBus eventBus;

    @BeforeEach
    public void init() {
        fileSystemService.init();
    }

    private Path prepareFS(FileSystem fs) throws IOException {
        Path rootDir = fs.getPath("/some/path/pg/root/");
        Files.createDirectories(rootDir);

        PGConfiguration conf = new PGConfiguration();
        conf.setRootDir(rootDir.toString());
        configurationService.saveConfiguration(conf);

        return rootDir;
    }

    @Test
    public void testLoadConfiguration() {
        PGConfiguration conf = new PGConfiguration();
        conf.setRootDir("test-root-dir");
        conf.setMiniaturesDir("test-mini-dir");
        conf.setPreviewsDir("test-prev-dir");
        conf.setSlideshowDir("test-slideshow-dir");
        configurationService.saveConfiguration(conf);

        conf = pgService.loadConfiguration();
        assertEquals("test-root-dir", conf.getRootDir());
        assertEquals("test-mini-dir", conf.getMiniaturesDir());
        assertEquals("test-prev-dir", conf.getPreviewsDir());
        assertEquals("test-slideshow-dir", conf.getSlideshowDir());
    }

    @Test
    public void testStoreConfiguration() {
        PGConfiguration conf = new PGConfiguration();
        conf.setRootDir("test-root-dir");
        conf.setMiniaturesDir("test-mini-dir");
        conf.setPreviewsDir("test-prev-dir");
        conf.setSlideshowDir("test-slideshow-dir");
        pgService.storeConfiguration(conf);

        configurationService.loadConfiguration(conf);
        assertEquals("test-root-dir", conf.getRootDir());
        assertEquals("test-mini-dir", conf.getMiniaturesDir());
        assertEquals("test-prev-dir", conf.getPreviewsDir());
        assertEquals("test-slideshow-dir", conf.getSlideshowDir());
    }

    @Test
    public void testDeletePhotogallery() throws IOException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("test");
        Path testDir = galleryDir.resolve("deep/file/deletions");
        Files.createDirectories(testDir);

        Path testFile = testDir.getParent().resolve("testFile1");
        Files.createFile(testFile);

        Path testFile2 = testDir.resolve("testFile2");
        Files.createFile(testFile2);

        assertTrue(Files.exists(galleryDir));
        assertTrue(Files.exists(testDir));
        assertTrue(Files.exists(testFile));
        assertTrue(Files.exists(testFile2));

        Photogallery photogallery = new Photogallery();
        photogallery.setPhotogalleryPath(galleryDir.getFileName().toString());
        photogallery = photogalleryRepository.save(photogallery);
        assertNotNull(photogallery);

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        Long contentNodeId1 = contentNodeService.save(PGModule.ID, photogallery.getId(), "Test galerie", null, true,
                nodeId1, userId1, false, LocalDateTime.now(), null);

        ContentNode contentNode = new ContentNode();
        contentNode.setId(contentNodeId1);
        photogallery.setContentNode(contentNode);
        photogallery = photogalleryRepository.save(photogallery);

        pgService.deletePhotogallery(photogallery.getId());

        assertFalse(Files.exists(galleryDir));
        assertFalse(Files.exists(testDir));
        assertFalse(Files.exists(testFile));
        assertFalse(Files.exists(testFile2));
        assertTrue(Files.exists(root));

        photogallery = photogalleryRepository.findById(photogallery.getId()).orElse(null);
        assertNull(photogallery);
    }

    @Test
    public void testSavePhotogallery() throws IOException, InterruptedException, ExecutionException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path animatedSmallFile = galleryDir.resolve("01.gif");
        Files.copy(this.getClass().getResourceAsStream("animatedSmall.gif"), animatedSmallFile);
        assertTrue(Files.exists(animatedSmallFile));

        Path animatedSmallFlawedFile = galleryDir.resolve("01b.gif");
        Files.copy(this.getClass().getResourceAsStream("animatedSmallFlawed.gif"), animatedSmallFlawedFile);
        assertTrue(Files.exists(animatedSmallFlawedFile));

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Path orientedLargeFile = galleryDir.resolve("04.jpg");
        Files.copy(this.getClass().getResourceAsStream("orientedLarge.jpg"), orientedLargeFile);
        assertTrue(Files.exists(orientedLargeFile));

        Path x264MP4File = galleryDir.resolve("05.mp4");
        Files.copy(this.getClass().getResourceAsStream("x264.mp4"), x264MP4File);
        assertTrue(Files.exists(x264MP4File));

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());

        eventBus.unsubscribe(eventsHandler);

        PGConfiguration conf = new PGConfiguration();
        configurationService.loadConfiguration(conf);

        double acceptableDifference = 0.1; // 10%

        // Animated small
        Path animatedSmallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("01.gif");
        Path animatedSmallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("01.gif");
        assertTrue(Files.exists(animatedSmallFile));
        assertTrue(Files.exists(animatedSmallMiniature));
        assertFalse(Files.exists(animatedSmallSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(animatedSmallMiniature),
                this.getClass().getResourceAsStream("animatedSmallMiniature.gif")) < acceptableDifference);

        // Animated small (flawed)
        Path animatedSmallFlawedMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("01b.gif");
        Path animatedSmallFlawedSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("01b.gif");
        assertTrue(Files.exists(animatedSmallFlawedFile));
        assertTrue(Files.exists(animatedSmallFlawedMiniature));
        assertFalse(Files.exists(animatedSmallFlawedSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(animatedSmallFlawedMiniature),
                this.getClass().getResourceAsStream("animatedSmallFlawedMiniature.gif")) < acceptableDifference);

        // Large
        Path largeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("02.jpg");
        Path largeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("02.jpg");
        assertTrue(Files.exists(largeFile));
        assertTrue(Files.exists(largeMiniature));
        assertTrue(Files.exists(largeSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(largeMiniature),
                this.getClass().getResourceAsStream("largeMiniature.jpg")) < acceptableDifference);
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(largeSlideshow),
                this.getClass().getResourceAsStream("largeSlideshow.jpg")) < acceptableDifference);

        // Small
        Path smallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("03.jpg");
        Path smallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("03.jpg");
        assertTrue(Files.exists(smallFile));
        assertTrue(Files.exists(smallMiniature));
        assertFalse(Files.exists(smallSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(smallMiniature),
                this.getClass().getResourceAsStream("smallMiniature.jpg")) < acceptableDifference);

        // Oriented large
        Path orientedLargeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("04.jpg");
        Path orientedLargeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("04.jpg");
        assertTrue(Files.exists(orientedLargeFile));
        assertTrue(Files.exists(orientedLargeMiniature));
        assertTrue(Files.exists(orientedLargeSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(orientedLargeMiniature),
                this.getClass().getResourceAsStream("orientedLargeMiniature.jpg")) < acceptableDifference);
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(orientedLargeSlideshow),
                this.getClass().getResourceAsStream("orientedLargeSlideshow.jpg")) < acceptableDifference);

        // X264 MP4
        Path x264MP4Preview = galleryDir.resolve(conf.getPreviewsDir()).resolve("05.mp4.png");
        assertTrue(Files.exists(x264MP4File));
        assertTrue(Files.exists(x264MP4Preview));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(x264MP4Preview),
                this.getClass().getResourceAsStream("x264Preview.png")) < acceptableDifference);
    }

    @Test
    public void testModifyPhotogallery() throws IOException, InterruptedException, ExecutionException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path animatedSmallFile = galleryDir.resolve("01.gif");
        Files.copy(this.getClass().getResourceAsStream("animatedSmall.gif"), animatedSmallFile);
        assertTrue(Files.exists(animatedSmallFile));

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());
        long galleryId = event.getGalleryId();

        eventBus.unsubscribe(eventsHandler);

        Path animatedSmallFlawedFile = galleryDir.resolve("01b.gif");
        Files.copy(this.getClass().getResourceAsStream("animatedSmallFlawed.gif"), animatedSmallFlawedFile);
        assertTrue(Files.exists(animatedSmallFlawedFile));

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Path orientedLargeFile = galleryDir.resolve("04.jpg");
        Files.copy(this.getClass().getResourceAsStream("orientedLarge.jpg"), orientedLargeFile);
        assertTrue(Files.exists(orientedLargeFile));

        operationId = UUID.randomUUID();

        eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        future = eventsHandler.expectEvent(operationId);

        pgService.modifyPhotogallery(operationId, galleryId, payloadTO, LocalDateTime.now());

        mock = future.get();
        event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());

        PGConfiguration conf = new PGConfiguration();
        configurationService.loadConfiguration(conf);

        double acceptableDifference = 0.1; // 10%

        // Animated small
        Path animatedSmallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("01.gif");
        Path animatedSmallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("01.gif");
        assertTrue(Files.exists(animatedSmallFile));
        assertTrue(Files.exists(animatedSmallMiniature));
        assertFalse(Files.exists(animatedSmallSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(animatedSmallMiniature),
                this.getClass().getResourceAsStream("animatedSmallMiniature.gif")) < acceptableDifference);

        // Animated small (flawed)
        Path animatedSmallFlawedMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("01b.gif");
        Path animatedSmallFlawedSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("01b.gif");
        assertTrue(Files.exists(animatedSmallFlawedFile));
        assertTrue(Files.exists(animatedSmallFlawedMiniature));
        assertFalse(Files.exists(animatedSmallFlawedSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(animatedSmallFlawedMiniature),
                this.getClass().getResourceAsStream("animatedSmallFlawedMiniature.gif")) < acceptableDifference);

        // Large
        Path largeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("02.jpg");
        Path largeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("02.jpg");
        assertTrue(Files.exists(largeFile));
        assertTrue(Files.exists(largeMiniature));
        assertTrue(Files.exists(largeSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(largeMiniature),
                this.getClass().getResourceAsStream("largeMiniature.jpg")) < acceptableDifference);
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(largeSlideshow),
                this.getClass().getResourceAsStream("largeSlideshow.jpg")) < acceptableDifference);

        // Small
        Path smallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("03.jpg");
        Path smallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("03.jpg");
        assertTrue(Files.exists(smallFile));
        assertTrue(Files.exists(smallMiniature));
        assertFalse(Files.exists(smallSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(smallMiniature),
                this.getClass().getResourceAsStream("smallMiniature.jpg")) < acceptableDifference);

        // Oriented large
        Path orientedLargeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("04.jpg");
        Path orientedLargeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("04.jpg");
        assertTrue(Files.exists(orientedLargeFile));
        assertTrue(Files.exists(orientedLargeMiniature));
        assertTrue(Files.exists(orientedLargeSlideshow));
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(orientedLargeMiniature),
                this.getClass().getResourceAsStream("orientedLargeMiniature.jpg")) < acceptableDifference);
        assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(orientedLargeSlideshow),
                this.getClass().getResourceAsStream("orientedLargeSlideshow.jpg")) < acceptableDifference);
    }

    @Test
    public void testCreateGalleryDir() throws IOException {
        Path root = prepareFS(fileSystemService.getFileSystem());

        String dir = pgService.createGalleryDir();
        assertTrue(dir.startsWith("pgGal_"));

        Path galleryDir = root.resolve(dir);
        assertTrue(Files.exists(galleryDir));
        assertTrue(Files.isDirectory(galleryDir));
    }

    @Test
    public void testGetPhotogalleryForDetail() throws IOException, InterruptedException, ExecutionException {
        Path root = prepareFS(fileSystemService.getFileSystem());

        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());
        long galleryId = event.getGalleryId();

        eventBus.unsubscribe(eventsHandler);

        PhotogalleryTO to = pgService.getPhotogalleryForDetail(galleryId);
        assertEquals("testGallery", to.getPhotogalleryPath());
        assertEquals("Test galerie", to.getContentNode().getName());
        assertTrue(to.getContentNode().isPublicated());
    }

    private long createMockGallery(Path root, Long userId, Long nodeId, int variant, boolean publicated)
            throws IOException, InterruptedException, ExecutionException {
        Path galleryDir = root.resolve("testGallery" + variant);
        Files.createDirectories(galleryDir);

        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie" + variant,
                galleryDir.getFileName().toString(), null, publicated, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId, userId, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());
        long galleryId = event.getGalleryId();

        eventBus.unsubscribe(eventsHandler);

        return galleryId;
    }

    @Test
    public void testGetAllPhotogalleriesForREST() throws IOException, InterruptedException, ExecutionException {
        Path root = prepareFS(fileSystemService.getFileSystem());

        Long userId1 = coreMockService.createMockUser(1);
        Long userId2 = coreMockService.createMockUser(2);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        Long nodeId2 = coreMockService.createMockRootNode(2);

        Long id1 = createMockGallery(root, userId1, nodeId1, 1, true);
        Long id2 = createMockGallery(root, userId1, nodeId2, 2, true);
        Long id3 = createMockGallery(root, userId2, nodeId1, 3, true);
        Long id4 = createMockGallery(root, userId2, nodeId2, 4, false);

        int count = pgService.countAllPhotogalleriesForREST(userId1, null);
        assertEquals(3, count);

        List<PhotogalleryRESTOverviewTO> list = pgService.getAllPhotogalleriesForREST(userId1, null,
                PageRequest.of(0, 2));
        assertEquals(2, list.size());
        assertEquals("Test galerie3", list.get(0).getName());
        assertEquals(id3, list.get(0).getId());
        assertEquals("Test galerie2", list.get(1).getName());
        assertEquals(id2, list.get(1).getId());

        list = pgService.getAllPhotogalleriesForREST(userId1, null, PageRequest.of(1, 2));
        assertEquals(1, list.size());
        assertEquals("Test galerie1", list.get(0).getName());
        assertEquals(id1, list.get(0).getId());

        count = pgService.countAllPhotogalleriesForREST(userId2, null);
        assertEquals(4, count);

        list = pgService.getAllPhotogalleriesForREST(userId2, null, PageRequest.of(0, 2));
        assertEquals(2, list.size());
        assertEquals("Test galerie4", list.get(0).getName());
        assertEquals(id4, list.get(0).getId());
        assertEquals("Test galerie3", list.get(1).getName());
        assertEquals(id3, list.get(1).getId());

        list = pgService.getAllPhotogalleriesForREST(userId2, null, PageRequest.of(1, 2));
        assertEquals(2, list.size());
        assertEquals("Test galerie2", list.get(0).getName());
        assertEquals(id2, list.get(0).getId());
        assertEquals("Test galerie1", list.get(1).getName());
        assertEquals(id1, list.get(1).getId());
    }

    @Test
    public void testGetPhotogalleryForREST()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());

        Long userId = coreMockService.createMockUser(1);
        Long nodeId = coreMockService.createMockRootNode(1);

        Path galleryDir = root.resolve("testGalleryREST");
        Files.createDirectories(galleryDir);

        Path animatedSmallFile = galleryDir.resolve("01.gif");
        Files.copy(this.getClass().getResourceAsStream("animatedSmall.gif"), animatedSmallFile);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);

        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId, userId, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());
        long galleryId = event.getGalleryId();

        eventBus.unsubscribe(eventsHandler);

        PhotogalleryRESTTO to = pgService.getPhotogalleryForREST(galleryId);

        assertEquals(MockUtils.MOCK_USER_NAME + 1, to.getAuthor());
        assertEquals(2, to.getFiles().size());
        Iterator<String> it = to.getFiles().iterator();
        assertEquals("01.gif", it.next());
        assertEquals("02.jpg", it.next());
        assertEquals(Long.valueOf(galleryId), to.getId());
        assertEquals("Test galerie", to.getName());
    }

    public void testGetPhotogalleryForREST_succes1()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        Long id1 = createMockGallery(root, userId1, nodeId1, 1, false);

        UserInfoTO user = mockSecurityService.getCurrentUser();
        user.setId(userId1);

        pgService.getPhotogalleryForREST(id1);
    }

    public void testGetPhotogalleryForREST_succes2()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        Long id1 = createMockGallery(root, userId1, nodeId1, 1, false);

        UserInfoTO user = mockSecurityService.getCurrentUser();
        user.getRoles().add(CoreRole.ADMIN);

        pgService.getPhotogalleryForREST(id1);
    }

    @Test
    public void testGetPhotogalleryForREST_exception()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        Long id1 = createMockGallery(root, userId1, nodeId1, 1, false);

        assertThrows(UnauthorizedAccessException.class, () ->
                pgService.getPhotogalleryForREST(id1)
        );
    }

    @Test
    public void testGetPhotoForREST()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());
        long galleryId = event.getGalleryId();

        eventBus.unsubscribe(eventsHandler);

        PGConfiguration conf = new PGConfiguration();
        configurationService.loadConfiguration(conf);

        Path photoPath = pgService.getPhotoForREST(galleryId, "02.jpg", PhotoVersion.SLIDESHOW);
        assertEquals(galleryDir.resolve(conf.getSlideshowDir()).resolve("02.jpg"), photoPath);
        photoPath = pgService.getPhotoForREST(galleryId, "03.jpg", PhotoVersion.SLIDESHOW);
        assertEquals(galleryDir.resolve("03.jpg"), photoPath);
        photoPath = pgService.getPhotoForREST(galleryId, "02.jpg", PhotoVersion.MINI);
        assertEquals(galleryDir.resolve(conf.getMiniaturesDir()).resolve("02.jpg"), photoPath);
        photoPath = pgService.getPhotoForREST(galleryId, "03.jpg", PhotoVersion.MINI);
        assertEquals(galleryDir.resolve(conf.getMiniaturesDir()).resolve("03.jpg"), photoPath);

    }

    @Test
    public void testZipGallery()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        future.get();

        eventBus.unsubscribe(eventsHandler);

        PGConfiguration conf = new PGConfiguration();
        configurationService.loadConfiguration(conf);

        // Large
        Path largeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("02.jpg");
        Path largeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("02.jpg");
        assertTrue(Files.exists(largeFile));
        assertTrue(Files.exists(largeMiniature));
        assertTrue(Files.exists(largeSlideshow));

        // Small
        Path smallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("03.jpg");
        Path smallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("03.jpg");
        assertTrue(Files.exists(smallFile));
        assertTrue(Files.exists(smallMiniature));
        assertFalse(Files.exists(smallSlideshow));

        PGZipProcessMockEventsHandler eventsHandler2 = new PGZipProcessMockEventsHandler();
        eventBus.subscribe(eventsHandler2);
        CompletableFuture<PGZipProcessMockEventsHandler> future2 = eventsHandler2.expectEvent();

        pgService.zipGallery("testGallery");

        future2.get();
        Path zipPath = eventsHandler2.getZipFile();

        if (!eventsHandler2.isSuccess()) {
            logger.error(eventsHandler2.getResultDetails(), eventsHandler2.getResultException());
            assertTrue(false);
        } else {
            logger.info("zipPath: {}", zipPath);
        }
        eventBus.unsubscribe(eventsHandler2);

        assertTrue(Files.exists(zipPath));

        List<Path> zipContents = ZIPUtils.list(fileSystemService.newZipFileSystem(zipPath, false));
        assertEquals(3, zipContents.size());
        Iterator<Path> it = zipContents.iterator();
        assertEquals("/", it.next().toString());
        assertEquals("/03.jpg", it.next().toString());
        assertEquals("/02.jpg", it.next().toString());

        pgService.deleteZipFile(zipPath);
    }

    @Test
    public void testDeleteFiles()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        future.get();

        eventBus.unsubscribe(eventsHandler);

        PGConfiguration conf = new PGConfiguration();
        configurationService.loadConfiguration(conf);

        // Large
        Path largeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("02.jpg");
        Path largeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("02.jpg");
        assertTrue(Files.exists(largeFile));
        assertTrue(Files.exists(largeMiniature));
        assertTrue(Files.exists(largeSlideshow));

        // Small
        Path smallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("03.jpg");
        Path smallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("03.jpg");
        assertTrue(Files.exists(smallFile));
        assertTrue(Files.exists(smallMiniature));
        assertFalse(Files.exists(smallSlideshow));

        Set<PhotogalleryViewItemTO> items = new HashSet<>();
        items.add(new PhotogalleryViewItemTO().setName("02.jpg"));
        List<PhotogalleryViewItemTO> removed = pgService.deleteFiles(items, "testGallery");

        assertEquals(1, removed.size());

        // Large
        largeMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("02.jpg");
        largeSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("02.jpg");
        assertFalse(Files.exists(largeFile));
        assertFalse(Files.exists(largeMiniature));
        assertFalse(Files.exists(largeSlideshow));

        // Small
        smallMiniature = galleryDir.resolve(conf.getMiniaturesDir()).resolve("03.jpg");
        smallSlideshow = galleryDir.resolve(conf.getSlideshowDir()).resolve("03.jpg");
        assertTrue(Files.exists(smallFile));
        assertTrue(Files.exists(smallMiniature));
        assertFalse(Files.exists(smallSlideshow));

    }

    @Test
    public void testGetFullImage() throws IOException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path file = pgService.getFullImage("testGallery", "02.jpg");
        assertEquals(largeFile, file);
    }

    @Test
    public void testGetFullImage_failed() throws IOException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        assertThrows(IllegalArgumentException.class, () ->
                pgService.getFullImage("../../testGallery", "02.jpg")
        );
    }

    @Test
    public void testGetFullImage_failed2() throws IOException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        assertThrows(IllegalArgumentException.class, () ->
                pgService.getFullImage("testGallery", "../../../02.jpg")
        );
    }

    @Test
    public void testUploadFile() throws IOException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        pgService.uploadFile(this.getClass().getResourceAsStream("large.jpg"), "02.jpg", "testGallery");

        assertTrue(Files.exists(galleryDir.resolve("02.jpg")));
        assertTrue(ImageComparator.isEqualAsFiles(Files.newInputStream(galleryDir.resolve("02.jpg")),
                this.getClass().getResourceAsStream("large.jpg")));
    }

    @Test
    public void testGetItems()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        List<PhotogalleryViewItemTO> items = pgService.getItems("testGallery");
        assertEquals(2, items.size());
        Iterator<PhotogalleryViewItemTO> it = items.iterator();

        PhotogalleryViewItemTO to = it.next();
        assertEquals("02.jpg", to.getName());
        to = it.next();
        assertEquals("03.jpg", to.getName());
    }

    @Test
    public void testGetViewItemsCount()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        assertEquals(2, pgService.getViewItemsCount("testGallery"));
    }

    @Test
    public void testGetViewItems()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Path x264MP4File = galleryDir.resolve("05.mp4");
        Files.copy(this.getClass().getResourceAsStream("x264.mp4"), x264MP4File);
        assertTrue(Files.exists(x264MP4File));

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());

        eventBus.unsubscribe(eventsHandler);

        PGConfiguration conf = new PGConfiguration();
        configurationService.loadConfiguration(conf);

        List<PhotogalleryViewItemTO> items = pgService.getViewItems("testGallery", 0, 10);
        assertEquals(3, items.size());
        Iterator<PhotogalleryViewItemTO> it = items.iterator();

        PhotogalleryViewItemTO to = it.next();
        assertEquals("02.jpg", to.getName());
        assertEquals(galleryDir.resolve(conf.getMiniaturesDir()).resolve("02.jpg"), to.getFile());
        assertEquals(PhotogalleryItemType.IMAGE, to.getType());
        to = it.next();
        assertEquals("03.jpg", to.getName());
        assertEquals(galleryDir.resolve(conf.getMiniaturesDir()).resolve("03.jpg"), to.getFile());
        assertEquals(PhotogalleryItemType.IMAGE, to.getType());
        to = it.next();
        assertEquals("05.mp4", to.getName());
        assertEquals(galleryDir.resolve(conf.getPreviewsDir()).resolve("05.mp4.png"), to.getFile());
        assertEquals(PhotogalleryItemType.VIDEO, to.getType());
    }

    @Test
    public void testGetSlideshowItem()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Path x264MP4File = galleryDir.resolve("05.mp4");
        Files.copy(this.getClass().getResourceAsStream("x264.mp4"), x264MP4File);
        assertTrue(Files.exists(x264MP4File));

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());

        eventBus.unsubscribe(eventsHandler);

        PGConfiguration conf = new PGConfiguration();
        configurationService.loadConfiguration(conf);

        PhotogalleryViewItemTO to = pgService.getSlideshowItem("testGallery", 0);
        assertEquals("02.jpg", to.getName());
        assertEquals(galleryDir.resolve(conf.getSlideshowDir()).resolve("02.jpg"), to.getFile());
        assertEquals(PhotogalleryItemType.IMAGE, to.getType());

        to = pgService.getSlideshowItem("testGallery", 1);
        assertEquals("03.jpg", to.getName());
        assertEquals(galleryDir.resolve("03.jpg"), to.getFile());
        assertEquals(PhotogalleryItemType.IMAGE, to.getType());

        to = pgService.getSlideshowItem("testGallery", 2);
        assertEquals("05.mp4", to.getName());
        assertEquals(galleryDir.resolve("05.mp4"), to.getFile());
        assertEquals(PhotogalleryItemType.VIDEO, to.getType());
    }

    @Test
    public void testCheckGallery()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());

        eventBus.unsubscribe(eventsHandler);

        assertTrue(pgService.checkGallery("testGallery"));
    }

    @Test
    public void testCheckGallery2()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path x264MP4File = galleryDir.resolve("05.mp4");
        Files.copy(this.getClass().getResourceAsStream("x264.mp4"), x264MP4File);
        assertTrue(Files.exists(x264MP4File));

        Long userId1 = coreMockService.createMockUser(1);
        Long nodeId1 = coreMockService.createMockRootNode(1);
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO("Test galerie", galleryDir.getFileName().toString(),
                null, true, false);

        UUID operationId = UUID.randomUUID();

        PGEventsHandler eventsHandler = new PGEventsHandler();
        eventBus.subscribe(eventsHandler);
        CompletableFuture<PGEventsHandler> future = eventsHandler.expectEvent(operationId);

        pgService.savePhotogallery(operationId, payloadTO, nodeId1, userId1, LocalDateTime.now());

        PGEventsHandler mock = future.get();
        PGProcessResultEvent event = mock.getResultAndDelete(operationId);

        assertTrue(event.isSuccess());
        assertNotNull(event.getGalleryId());

        eventBus.unsubscribe(eventsHandler);

        assertTrue(pgService.checkGallery("testGallery"));
    }

    @Test
    public void testCheckGallery_failed()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Path x264MP4File = galleryDir.resolve("05.mp4");
        Files.copy(this.getClass().getResourceAsStream("x264.mp4"), x264MP4File);
        assertTrue(Files.exists(x264MP4File));

        assertFalse(pgService.checkGallery("testGallery"));
    }

    @Test
    public void testDeleteDraftGallery()
            throws IOException, InterruptedException, ExecutionException, UnauthorizedAccessException {
        Path root = prepareFS(fileSystemService.getFileSystem());
        Path galleryDir = root.resolve("testGallery");
        Files.createDirectories(galleryDir);

        Path largeFile = galleryDir.resolve("02.jpg");
        Files.copy(this.getClass().getResourceAsStream("large.jpg"), largeFile);
        assertTrue(Files.exists(largeFile));

        Path smallFile = galleryDir.resolve("03.jpg");
        Files.copy(this.getClass().getResourceAsStream("small.jpg"), smallFile);
        assertTrue(Files.exists(smallFile));

        Path x264MP4File = galleryDir.resolve("05.mp4");
        Files.copy(this.getClass().getResourceAsStream("x264.mp4"), x264MP4File);
        assertTrue(Files.exists(x264MP4File));

        pgService.deleteDraftGallery("testGallery");

        assertFalse(Files.exists(largeFile));
        assertFalse(Files.exists(smallFile));
        assertFalse(Files.exists(x264MP4File));
        assertFalse(Files.exists(galleryDir));
    }
}
