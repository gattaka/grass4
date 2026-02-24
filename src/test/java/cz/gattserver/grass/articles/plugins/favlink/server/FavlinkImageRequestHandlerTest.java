package cz.gattserver.grass.articles.plugins.favlink.server;

import cz.gattserver.grass.core.mock.MockFileSystemService;
import cz.gattserver.grass.core.util.DBCleanTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FavlinkImageRequestHandlerTest extends DBCleanTest {

    @Autowired
    private MockFileSystemService fileSystemService;

    @Autowired
    private FavlinkImageRequestHandler favlinkImageRequestHandler;

    @BeforeEach
    public void init() {
        fileSystemService.init();
    }

    private Path prepareFS(FileSystem fs) throws IOException {
        Path outputDir = fs.getPath("files/favlink");
        Files.createDirectories(outputDir);
        return outputDir;
    }

    @Test
    public void test() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path outputDir = prepareFS(fs);
        Path testFile = Files.createFile(outputDir.resolve("testFile"));
        Files.write(testFile, new byte[]{1, 1, 1});

        Path file = favlinkImageRequestHandler.getPath("testFile", null);
        assertTrue(Files.exists(file));
        assertEquals(3L, Files.size(file));
        assertEquals("testFile", file.getFileName().toString());
        assertEquals("files/favlink/testFile", file.toString());
    }
}