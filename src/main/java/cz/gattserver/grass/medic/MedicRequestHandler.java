package cz.gattserver.grass.medic;

import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.medic.service.MedicService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.Serial;
import java.nio.file.Path;

@Component
public class MedicRequestHandler extends AbstractGrassRequestHandler {

	@Serial
	private static final long serialVersionUID = 5193217920856378073L;

	@Value("${medic.root.path}")
	private String rootPath;

	private final FileSystemService fileSystemService;

	public MedicRequestHandler(FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
	}

	@Override
	protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
		if (!fileName.matches("/[0-9]+/[^/]+"))
			throw new GrassPageException(404);
		String[] chunks = fileName.split("/");
		try {
			Long.parseLong(chunks[1]);
		} catch (NumberFormatException e) {
			throw new GrassPageException(404);
		}

		return fileSystemService.getFileSystem().getPath(rootPath, chunks[1], chunks[2]);
	}

	@Override
	protected String getMimeType(Path file) {
		String type = super.getMimeType(file);
		return type + "; charset=utf-8";
	}
}