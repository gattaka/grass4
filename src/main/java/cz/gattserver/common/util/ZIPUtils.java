package cz.gattserver.common.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ZIPUtils} based on
 * <ul>
 * <li><a href=
 * "http://fahdshariff.blogspot.cz/2011/08/java-7-working-with-zip-files.html">http://fahdshariff.blogspot.cz/2011/08/java-7-working-with-zip-files.html</a></li>
 * <li><a href=
 * "https://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html">https://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html</a></li>
 * </ul>
 * 
 * zvážit
 * http://www.pixeldonor.com/2013/oct/12/concurrent-zip-compression-java-nio/
 * 
 */
public class ZIPUtils {

	private ZIPUtils() {
	}

	/**
	 * List the contents of the specified zip file
	 * 
	 * @param zipFileSystem
	 */
	public static List<Path> list(FileSystem zipFileSystem) throws IOException {
		final List<Path> result = new ArrayList<>();
		final Path root = zipFileSystem.getRootDirectories().iterator().next();

		// walk the file tree and print out the directory and filenames
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				print(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				print(dir);
				return FileVisitResult.CONTINUE;
			}

			private void print(Path file) {
				result.add(file);
			}
		});

		return result;
	}

}
