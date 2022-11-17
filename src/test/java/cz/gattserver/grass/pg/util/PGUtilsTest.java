package cz.gattserver.grass.pg.util;

import cz.gattserver.grass.core.mock.MockFileSystemService;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.ui.util.ImageComparator;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.pg.config.PGConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PGUtilsTest {

	@Autowired
	private MockFileSystemService fileSystemService;

	@Autowired
	private ConfigurationService configurationService;

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
	public void testGetExtension() throws IOException {
		Path root = prepareFS(fileSystemService.getFileSystem());
		assertEquals("png", PGUtils.getExtension(root.resolve("test.png")));
		assertEquals("ss", PGUtils.getExtension(root.resolve("test.ss")));
		assertEquals("", PGUtils.getExtension(root.resolve("test")));
		assertEquals("", PGUtils.getExtension(root.resolve("test.")));
		assertEquals("", PGUtils.getExtension(root.resolve(".png")));
		assertEquals("s", PGUtils.getExtension(root.resolve("test.image.s")));
	}

	@Test
	public void testResizeImage() throws IOException {
		Path rootDir = prepareFS(fileSystemService.getFileSystem());
		Files.createDirectories(rootDir);

		Path landscape = rootDir.resolve("landscape.png");
		Path landscapeOutput = rootDir.resolve("landscapeOutput.png");
		Files.copy(this.getClass().getResourceAsStream("landscape.png"), landscape);
		PGUtils.resizeImage(landscape, landscapeOutput);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(landscapeOutput),
				this.getClass().getResourceAsStream("landscapeMiniature.png")) < 0.01);

		Path square = rootDir.resolve("square.png");
		Path squareOutput = rootDir.resolve("squareOutput.png");
		Files.copy(this.getClass().getResourceAsStream("square.png"), square);
		PGUtils.resizeImage(square, squareOutput);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(squareOutput),
				this.getClass().getResourceAsStream("squareMiniature.png")) < 0.01);

		Path portrait = rootDir.resolve("portrait.png");
		Path portraitOutput = rootDir.resolve("portraitOutput.png");
		Files.copy(this.getClass().getResourceAsStream("portrait.png"), portrait);
		PGUtils.resizeImage(portrait, portraitOutput);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(portraitOutput),
				this.getClass().getResourceAsStream("portraitMiniature.png")) < 0.01);
	}

	@Test
	public void testResizeVideoPreviewImage() throws IOException {
		Path rootDir = prepareFS(fileSystemService.getFileSystem());
		Files.createDirectories(rootDir);

		Path landscapeOutput = rootDir.resolve("landscapeOutput.png");
		PGUtils.resizeVideoPreviewImage(ImageIO.read(this.getClass().getResourceAsStream("landscape.png")),
				landscapeOutput);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(landscapeOutput),
				this.getClass().getResourceAsStream("landscapeMiniature.png")) < 0.01);

		Path squareOutput = rootDir.resolve("squareOutput.png");
		PGUtils.resizeVideoPreviewImage(ImageIO.read(this.getClass().getResourceAsStream("square.png")), squareOutput);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(squareOutput),
				this.getClass().getResourceAsStream("squareMiniature.png")) < 0.01);

		Path portraitOutput = rootDir.resolve("portraitOutput.png");
		PGUtils.resizeVideoPreviewImage(ImageIO.read(this.getClass().getResourceAsStream("portrait.png")),
				portraitOutput);
		assertTrue(ImageComparator.isEqualAsImagePixels(Files.newInputStream(portraitOutput),
				this.getClass().getResourceAsStream("portraitMiniature.png")) < 0.01);
	}

	@Test
	public void testIsImage() throws IOException {
		Path root = prepareFS(fileSystemService.getFileSystem());
		assertTrue(PGUtils.isRasterImage(root.resolve("test.PNG")));
		assertTrue(PGUtils.isRasterImage(root.resolve("test.png")));
		assertTrue(PGUtils.isRasterImage(root.resolve("test.jpeg")));
		assertTrue(PGUtils.isRasterImage(root.resolve("test.jpg")));
		assertTrue(PGUtils.isRasterImage(root.resolve("test.gif")));
		assertTrue(PGUtils.isRasterImage(root.resolve("test.bmp")));
		assertFalse(PGUtils.isRasterImage(root.resolve("test.ng")));
		assertFalse(PGUtils.isRasterImage(root.resolve("test")));
		assertFalse(PGUtils.isRasterImage(root.resolve("testjpeg")));
	}
	
	@Test
	public void testIsVideo() throws IOException {
		Path root = prepareFS(fileSystemService.getFileSystem());
		assertTrue(PGUtils.isVideo(root.resolve("test.MP4")));
		assertTrue(PGUtils.isVideo(root.resolve("test.mp4")));
		assertTrue(PGUtils.isVideo(root.resolve("test.ogg")));
		assertTrue(PGUtils.isVideo(root.resolve("test.webm")));
		assertTrue(PGUtils.isVideo(root.resolve("test.mov")));
		assertTrue(PGUtils.isVideo(root.resolve("test.avi")));
		assertFalse(PGUtils.isVideo(root.resolve("test.ng")));
		assertFalse(PGUtils.isVideo(root.resolve("test")));
		assertFalse(PGUtils.isVideo(root.resolve("testmp4")));
	}

}
