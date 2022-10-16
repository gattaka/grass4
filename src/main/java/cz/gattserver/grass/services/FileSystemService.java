package cz.gattserver.grass.services;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

public interface FileSystemService {

	Path createTmpDir(String name) throws IOException;

	FileSystem getFileSystem();

	FileSystem newZipFileSystem(Path path, boolean create) throws IOException;

	Path grantPermissions(Path path) throws IOException;

	void createDirectoriesWithPerms(Path path) throws IOException;

	Path createDirectoryWithPerms(Path path) throws IOException;

}
