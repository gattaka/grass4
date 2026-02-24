package cz.gattserver.grass.fm;

import cz.gattserver.grass.core.mock.MockFileSystemService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FMRequestHandlerTest {

    @Autowired
    private MockFileSystemService fileSystemService;

    @Autowired
    private FMRequestHandler fmRequestHandler;

    @BeforeEach
    public void init() {
        fileSystemService.init();
    }

    private Path prepareFS(FileSystem fs) throws IOException {
        Path rootDir = fs.getPath("files/fm");
        Files.createDirectories(rootDir);
        return rootDir;
    }

    @Test
    public void test() throws IOException {
        FileSystem fs = fileSystemService.getFileSystem();
        Path rootDir = prepareFS(fs);
        Path testFile = Files.createFile(rootDir.resolve("testFile"));
        Files.write(testFile, new byte[]{1, 1, 1});

        Path file = fmRequestHandler.getPath("testFile", null);
        assertTrue(Files.exists(file));
        assertEquals(3L, Files.size(file));
        assertEquals("testFile", file.getFileName().toString());
        assertEquals("files/fm/testFile", file.toString());
    }
}