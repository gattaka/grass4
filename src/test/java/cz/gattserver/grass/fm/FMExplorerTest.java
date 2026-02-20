package cz.gattserver.grass.fm;

import cz.gattserver.grass.core.mock.MockFileSystemService;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.fm.config.FMConfiguration;
import cz.gattserver.grass.fm.interfaces.FMItemTO;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FMExplorerTest {

    @Autowired
    private MockFileSystemService fileSystemService;

    @Autowired
    private ConfigurationService configurationService;


    @BeforeEach
    public void init() {
        fileSystemService.init();
    }

    private Path prepareFS(FileSystem fs) throws IOException {
        Path rootDir = fs.getPath("/some/path/fm/root/");
        Files.createDirectories(rootDir);

        FMConfiguration fmc = new FMConfiguration();
        fmc.setRootDir(rootDir.toString());
        configurationService.saveConfiguration(fmc);

        return rootDir;
    }

    @Test
    public void test() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);

        FMExplorer explorer = new FMExplorer(fs);
        assertEquals(FileProcessState.NOT_VALID, explorer.goToDir("../../fm"));

        explorer = new FMExplorer(fs);
        assertEquals(FileProcessState.MISSING, explorer.goToDir("sub1"));

        String subDirName = "sub2";
        Path subDir = rootDir.resolve(subDirName);
        Files.createDirectory(subDir);
        explorer = new FMExplorer(fs);
        assertEquals(FileProcessState.SUCCESS, explorer.goToDir(subDirName));
    }

    @Test
    public void testCreateDir() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);

        FMExplorer explorer = new FMExplorer(fs);
        String newDirName = "newDir";
        assertEquals(FileProcessState.SUCCESS, explorer.createNewDir(newDirName));

        Path newDir = rootDir.resolve(newDirName);
        assertTrue(Files.exists(newDir));

        assertEquals(FileProcessState.ALREADY_EXISTS, explorer.createNewDir(newDirName));
        assertEquals(FileProcessState.NOT_VALID, explorer.createNewDir("../testDir"));
    }

    @Test
    public void testSort() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path currentAbsolutePath = fs.getPath("..").toAbsolutePath();
        FMItemTO adir = FMUtils.mapPathToItem(Files.createDirectory(fs.getPath("Adir")), currentAbsolutePath);
        FMItemTO bdir = FMUtils.mapPathToItem(Files.createDirectory(fs.getPath("Bdir")), currentAbsolutePath);
        FMItemTO afile = FMUtils.mapPathToItem(Files.createFile(fs.getPath("Afile")), currentAbsolutePath);
        FMItemTO bfile = FMUtils.mapPathToItem(Files.createFile(fs.getPath("Bfile")), currentAbsolutePath);

        assertEquals(0, FMUtils.sortFile(adir, adir, null));
        assertEquals(-1, FMUtils.sortFile(adir, bdir, null));
        assertEquals(1, FMUtils.sortFile(bdir, adir, null));

        assertEquals(0, FMUtils.sortFile(afile, afile, null));
        assertEquals(-1, FMUtils.sortFile(afile, bfile, null));
        assertEquals(1, FMUtils.sortFile(bfile, afile, null));

        assertEquals(-1, FMUtils.sortFile(adir, afile, null));
        assertEquals(1, FMUtils.sortFile(afile, adir, null));
        assertEquals(-1, FMUtils.sortFile(bdir, afile, null));
        assertEquals(1, FMUtils.sortFile(afile, bdir, null));

        // TODO to samé s QuerySortOrder listem
    }

    @Test
    public void testDeleteFile() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        String subDirName = "subDir1/subDir2";
        Path subDir = Files.createDirectories(rootDir.resolve(subDirName));

        FMExplorer explorer = new FMExplorer(fs);
        explorer.goToDir("subDir1");

        assertEquals(FileProcessState.NOT_VALID, explorer.deleteFile("../.."));
        assertTrue(Files.exists(subDir));

        assertEquals(FileProcessState.MISSING, explorer.deleteFile("nonexisting"));
        assertTrue(Files.exists(subDir));

        assertEquals(FileProcessState.SUCCESS, explorer.deleteFile("subDir2"));
        assertFalse(Files.exists(subDir));
    }

    @Test
    public void testGetDownloadLink() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        String subDirName = "subDir1/subDir2";
        Files.createDirectories(rootDir.resolve(subDirName));

        FMExplorer explorer = new FMExplorer(fs);
        explorer.goToDir("subDir1");

        assertEquals("http://test/web/" + FMConfiguration.FM_PATH + "/subDir1/testFile",
                explorer.getDownloadLink("http://test/web", "testFile"));
    }

    @Test
    public void testRenameFile() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        String subDirName = "subDir1/subDir2";
        Path subDir = Files.createDirectories(rootDir.resolve(subDirName));

        FMExplorer explorer = new FMExplorer(fs);
        explorer.goToDir("subDir1");

        assertEquals(FileProcessState.NOT_VALID, explorer.renameFile("subDir2", "../../../sssDir"));
        assertTrue(Files.exists(subDir));

        assertEquals(FileProcessState.NOT_VALID, explorer.renameFile("../../someDir", "sssDir"));
        assertTrue(Files.exists(subDir));

        assertEquals(FileProcessState.NOT_VALID, explorer.renameFile("..", "sssDir"));
        assertTrue(Files.exists(subDir));

        assertEquals(FileProcessState.MISSING, explorer.renameFile("nonexistent", "sssDir"));
        assertTrue(Files.exists(subDir));

        assertEquals(FileProcessState.ALREADY_EXISTS, explorer.renameFile("subDir2", "../subDir1"));
        assertTrue(Files.exists(subDir));

        assertEquals(FileProcessState.SUCCESS, explorer.renameFile("subDir2", "sssDir"));
        assertTrue(Files.exists(rootDir.resolve("subDir1").resolve("sssDir")));
    }

    @Test
    public void testGoToDir() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        String subDirName = "subDir1/subDir2";
        Files.createDirectories(rootDir.resolve(subDirName));
        Files.createFile(rootDir.resolve("testFile"));

        FMExplorer explorer = new FMExplorer(fs);
        assertEquals(FileProcessState.MISSING, explorer.goToDir("nonExisting"));
        assertEquals(FileProcessState.NOT_VALID, explorer.goToDir("../../test"));
        assertEquals(FileProcessState.SUCCESS, explorer.goToDir("subDir1/subDir2"));
        assertEquals(FileProcessState.DIRECTORY_REQUIRED, explorer.goToDir("testFile"));
    }

    @Test
    public void testGoToDirFromCurrentDir() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        String subDirName = "subDir1/subDir2";
        Files.createDirectories(rootDir.resolve(subDirName));

        FMExplorer explorer = new FMExplorer(fs);
        assertEquals(FileProcessState.SUCCESS, explorer.goToDirFromCurrentDir("subDir1"));
        assertEquals(FileProcessState.SUCCESS, explorer.goToDirFromCurrentDir("subDir2"));
    }

    @Test
    public void testGoToDirByURL() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        String subDirName = "subDir1/subDir2";
        Files.createDirectories(rootDir.resolve(subDirName));

        FMExplorer explorer = new FMExplorer(fs);
        assertEquals(FileProcessState.SUCCESS,
                explorer.goToDirByURL("http://test/web", "fm-test", "http://test/web/fm-test/subDir1"));
        assertEquals(FileProcessState.SUCCESS, explorer.goToDirFromCurrentDir("subDir2"));

        assertEquals(FileProcessState.SUCCESS,
                explorer.goToDirByURL("http://test/web", "fm-test", "http://test/web/fm-test"));
        assertEquals(FileProcessState.SUCCESS, explorer.goToDirFromCurrentDir("subDir1"));

        assertEquals(FileProcessState.SYSTEM_ERROR,
                explorer.goToDirByURL("http://test/web", "fm-test", "http://test/web/fm-test-other-module"));
        assertEquals(FileProcessState.SUCCESS, explorer.goToDirFromCurrentDir("subDir2"));
    }

    @Test
    public void testGetCurrentURL() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        String subDirName = "subDir1/subDir2";
        Files.createDirectories(rootDir.resolve(subDirName));

        FMExplorer explorer = new FMExplorer(fs);
        assertEquals("http://test/web/fm-test/", explorer.getCurrentURL("http://test/web", "fm-test"));
        explorer.goToDir("subDir1/subDir2");
        assertEquals("http://test/web/fm-test/subDir1/subDir2", explorer.getCurrentURL("http://test/web", "fm-test"));
    }

    @Test
    public void testSaveFile() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        FMExplorer explorer = new FMExplorer(fs);
        byte[] content = new byte[]{1, 1, 1, 1, 1};
        explorer.saveFile(new ByteArrayInputStream(content), "testFile");

        Path testFile = rootDir.resolve("testFile");
        assertTrue(Files.exists(testFile));
        assertFalse(Files.isDirectory(testFile));

        byte[] arr = Files.readAllBytes(testFile);
        assertArrayEquals(content, arr);
    }

    @Test
    public void testListing() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        String subDirName2 = "subDir1/subDir2";
        Path subDir2 = Files.createDirectories(rootDir.resolve(subDirName2));
        Path subDir3 = Files.createDirectories(subDir2.resolve("subDir3"));
        Path file1 = Files.createFile(subDir2.resolve("file1"));
        Path file2 = Files.createFile(subDir2.resolve("file2"));
        Path file3 = Files.createFile(subDir3.resolve("file3"));
        Path file4 = Files.createFile(subDir3.resolve("file4"));

        Files.write(file1, new byte[]{1, 1, 1});
        Files.write(file2, new byte[]{1});
        Files.write(file3, new byte[]{1, 1, 1, 1});
        Files.write(file4, new byte[]{1, 1, 1, 1, 1});

        FMExplorer explorer = new FMExplorer(fs);
        explorer.goToDir(subDirName2);

        assertEquals(4, explorer.listCount(null));

        Iterator<FMItemTO> it = explorer.listing(null, 0, 10, null).iterator();
        FMItemTO item = it.next();
        assertEquals("..", item.name());
        assertNotNull(item.lastModified());
        assertEquals("", item.size());
        assertTrue(item.directory());
        assertNull(item.pathFromFMRoot());

        item = it.next();
        assertEquals("subDir3", item.name());
        assertNotNull(item.lastModified());
        assertEquals("9 B", item.size());
        assertTrue(item.directory());
        assertNull(item.pathFromFMRoot());

        item = it.next();
        assertEquals("file1", item.name());
        assertNotNull(item.lastModified());
        assertEquals("3 B", item.size());
        assertFalse(item.directory());
        assertNull(item.pathFromFMRoot());

        item = it.next();
        assertEquals("file2", item.name());
        assertNotNull(item.lastModified());
        assertEquals("1 B", item.size());
        assertFalse(item.directory());
        assertNull(item.pathFromFMRoot());

        assertEquals(3, explorer.listCount("file"));

        it = explorer.listing("file", 0, 10, null).iterator();
        item = it.next();
        assertEquals("..", item.name());
        assertNotNull(item.lastModified());
        assertEquals("", item.size());
        assertTrue(item.directory());
        assertNull(item.pathFromFMRoot());

        item = it.next();
        assertEquals("file1", item.name());
        assertNotNull(item.lastModified());
        assertEquals("3 B", item.size());
        assertFalse(item.directory());
        assertNull(item.pathFromFMRoot());

        item = it.next();
        assertEquals("file2", item.name());
        assertNotNull(item.lastModified());
        assertEquals("1 B", item.size());
        assertFalse(item.directory());
        assertNull(item.pathFromFMRoot());

        assertFalse(it.hasNext());
    }

    @Test
    public void testBreadcrumb() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        String subDirName2 = "subDir1/subDir2";
        Path subDir2 = Files.createDirectories(rootDir.resolve(subDirName2));
        Files.createDirectories(subDir2.resolve("subDir3"));
        Files.createFile(subDir2.resolve("file1"));
        Files.createFile(subDir2.resolve("file2"));

        FMExplorer explorer = new FMExplorer(fs);
        explorer.goToDir(subDirName2);

        Iterator<FMItemTO> it = explorer.getBreadcrumbChunks().iterator();
        FMItemTO item = it.next();
        assertEquals("subDir2", item.name());
        assertEquals("subDir1/subDir2", item.pathFromFMRoot());

        item = it.next();
        assertEquals("subDir1", item.name());
        assertEquals("subDir1", item.pathFromFMRoot());

        item = it.next();
        assertEquals("/", item.name());
        assertEquals("", item.pathFromFMRoot());
    }

    @Test
    public void test_failNullFS() throws IOException {
        assertThrows(NullPointerException.class, () -> new FMExplorer(null), "Filesystem nesmí být null");
    }

    @Test
    public void test_failRoot() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();

        Path rootDir = fs.getPath("/some/path/fm/root/");

        FMConfiguration fmc = new FMConfiguration();
        fmc.setRootDir(rootDir.toString());
        configurationService.saveConfiguration(fmc);

        assertThrows(IllegalStateException.class, () -> new FMExplorer(fs),
                "Kořenový adresář FM modulu musí existovat");
    }

}
