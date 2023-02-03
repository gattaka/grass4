package cz.gattserver.grass.fm.service.impl;

import cz.gattserver.common.util.ReferenceHolder;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.fm.events.FMZipProcessProgressEvent;
import cz.gattserver.grass.fm.events.FMZipProcessResultEvent;
import cz.gattserver.grass.fm.events.FMZipProcessStartEvent;
import cz.gattserver.grass.fm.service.FMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Set;

@Transactional
@Service
public class FMServiceImpl implements FMService {

	private static Logger logger = LoggerFactory.getLogger(FMServiceImpl.class);

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private EventBus eventBus;

	@Async
	@Override
	public void zipFiles(Set<Path> items) {
		logger.info("zipFM thread: " + Thread.currentThread().getId());

		final ReferenceHolder<Integer> total = new ReferenceHolder<>();
		final ReferenceHolder<Integer> progress = new ReferenceHolder<>();

		total.setValue(items.size());
		eventBus.publish(new FMZipProcessStartEvent(total.getValue() + 1));

		progress.setValue(1);

		String zipFileName = "grassFMTmpFile-" + new Date().getTime() + ".zip";
		try {
			Path zipFile = fileSystemService.createTmpDir("grassFMTmpFolder").resolve(zipFileName);
			try (FileSystem zipFileSystem = fileSystemService.newZipFileSystem(zipFile, true)) {
				final Path root = zipFileSystem.getRootDirectories().iterator().next();
				for (Path path : items) {
					eventBus.publish(new FMZipProcessProgressEvent("Přidávám '" + path.getFileName() + "' do ZIPu "
							+ progress.getValue() + "/" + total.getValue()));
					progress.setValue(progress.getValue() + 1);
					Path dest = root.resolve(path.getFileName().toString());

					if (Files.isDirectory(path)) {
						Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
							public Path resolve(Path dest, Path file) {
								// https://stackoverflow.com/questions/22611919/why-do-i-get-providermismatchexception-when-i-try-to-relativize-a-path-agains
								Path relative = path.relativize(file);
								Path ret = dest;
								for (final Path component : relative)
									ret = ret.resolve(component.getFileName().toString());
								return ret;
							}

							@Override
							public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
									throws IOException {
								Files.createDirectories(resolve(dest, dir));
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
									throws IOException {
								Files.copy(file, resolve(dest, file));
								return FileVisitResult.CONTINUE;
							}
						});
					} else {
						Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
					}
				}
				eventBus.publish(new FMZipProcessResultEvent(zipFile));
			} catch (Exception e) {
				String msg = "Nezdařilo se vytvořit ZIP";
				eventBus.publish(new FMZipProcessResultEvent(msg, e));
				logger.error(msg, e);
			}
		} catch (Exception e) {
			String msg = "Nezdařilo se vytvořit dočasný adresář pro ZIP";
			eventBus.publish(new FMZipProcessResultEvent(msg, e));
			logger.error(msg, e);
		}
	}

	@Override
	public void deleteZipFile(Path zipFile) {
		try {
			Files.delete(zipFile);
			logger.info("zipFile " + zipFile.getFileName() + " deleted");
		} catch (IOException e) {
			logger.error("Nezdařilo se smazat ZIP soubor {}", zipFile.getFileName().toString());
		}
	}
}
