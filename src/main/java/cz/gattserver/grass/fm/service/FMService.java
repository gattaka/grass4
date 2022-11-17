package cz.gattserver.grass.fm.service;

import java.nio.file.Path;
import java.util.Set;

public interface FMService {

	void zipFiles(Set<Path> items);

	void deleteZipFile(Path zipFile);

}
