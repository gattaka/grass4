package cz.gattserver.grass.pg;

import cz.gattserver.grass.core.mock.MockFileSystemService;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.pg.config.PGConfiguration;
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
public class PGRequestHandlerTest {

    @Autowired
    private MockFileSystemService fileSystemService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private PGRequestHandler pgRequestHandler;

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
    public void test() throws IOException {
        Path rootDir = prepareFS(fileSystemService.getFileSystem());
        Path testFile = Files.createFile(rootDir.resolve("testFile"));
        Files.write(testFile, new byte[]{1, 1, 1});

        Path file = pgRequestHandler.getPath("testFile", null);
        assertTrue(Files.exists(file));
        assertEquals(3L, Files.size(file));
        assertEquals("testFile", file.getFileName().toString());
        assertEquals("/some/path/pg/root/testFile", file.toString());
    }

}
