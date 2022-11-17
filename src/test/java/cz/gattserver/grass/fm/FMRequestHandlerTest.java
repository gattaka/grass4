package cz.gattserver.grass.fm;

import cz.gattserver.grass.core.mock.MockFileSystemService;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.fm.config.FMConfiguration;
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
        Path testFile = Files.createFile(rootDir.resolve("testFile"));
        Files.write(testFile, new byte[]{1, 1, 1});

        Path file = new FMRequestHandler().getPath("testFile", null);
        assertTrue(Files.exists(file));
        assertEquals(3L, Files.size(file));
        assertEquals("testFile", file.getFileName().toString());
        assertEquals("/some/path/fm/root/testFile", file.toString());
    }

}
