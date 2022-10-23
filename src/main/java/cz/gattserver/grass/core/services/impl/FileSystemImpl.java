package cz.gattserver.grass.core.services.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cz.gattserver.grass.core.services.FileSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileSystemImpl implements FileSystemService {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(FileSystemImpl.class);

	@Override
	public FileSystem getFileSystem() {
		return FileSystems.getDefault();
	}

	@Override
	public FileSystem newZipFileSystem(Path path, boolean create) throws IOException {
		final Map<String, String> env = new HashMap<>();
		if (create)
			env.put("create", "true");
		return FileSystems.newFileSystem(URI.create("jar:" + path.toUri()), env);
	}

	@Override
	public Path createTmpDir(String name) throws IOException {
		return Files.createTempDirectory(name);
	}

	@Override
	public Path grantPermissions(Path path) throws IOException {
		// Nakonec vyřešeno přes jsvc + umask fix
		return path;
	}

	@Override
	public void createDirectoriesWithPerms(Path path) throws IOException {
		// Nakonec vyřešeno přes jsvc + umask fix
		Files.createDirectories(path);
	}

	@Override
	public Path createDirectoryWithPerms(Path path) throws IOException {
		// Nakonec vyřešeno přes jsvc + umask fix
		return Files.createDirectory(path);
	}
}
