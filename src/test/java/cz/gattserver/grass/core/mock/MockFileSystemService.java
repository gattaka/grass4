package cz.gattserver.grass.core.mock;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import cz.gattserver.grass.core.services.FileSystemService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;

@Primary
@Component(value = "MockFileSystemService")
public class MockFileSystemService implements FileSystemService {

	private FileSystem fileSystem;

	@PostConstruct
	public void init() {
		fileSystem = Jimfs.newFileSystem(Configuration.unix());
	}

	@Override
	public FileSystem getFileSystem() {
		return fileSystem;
	}

	@Override
	public FileSystem newZipFileSystem(Path path, boolean create) throws IOException {
		if (create) {
			return FileSystems.newFileSystem(URI.create("jar:" + path.toUri()),
					Collections.singletonMap("create", "true"), Jimfs.class.getClassLoader());
		} else {
			return FileSystems.newFileSystem(path, Jimfs.class.getClassLoader());
		}
	}

	@Override
	public Path createTmpDir(String name) throws IOException {
		return Files.createDirectories(fileSystem.getPath("tmp", name + new Date().getTime()));
	}

	@Override
	public Path grantPermissions(Path path) throws IOException {
		// mock fs nemá perms
		return path;
	}

	@Override
	public void createDirectoriesWithPerms(Path path) throws IOException {
		Files.createDirectories(path);
	}

	@Override
	public Path createDirectoryWithPerms(Path path) throws IOException {
		return Files.createDirectory(path);
	}

}
