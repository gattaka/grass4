package cz.gattserver.grass.hw;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.server.AbstractConfiguratedPathRequestHandler;
import cz.gattserver.grass.hw.service.HWService;
import org.springframework.beans.factory.annotation.Autowired;


@WebServlet(urlPatterns = "/" + HWConfiguration.HW_PATH + "/*")
public class HWRequestHandler extends AbstractConfiguratedPathRequestHandler {

	@Autowired
	private HWService hwService;

	@Override
	public void init() {
		SpringContextHelper.inject(this);
		super.init();
	}

	@Override
	protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
		if (!fileName.matches("/[0-9]+/(icon|img|print3d|doc)/[^/]+"))
			throw new GrassPageException(400);
		String[] chunks = fileName.split("/");
		Long id = Long.parseLong(chunks[1]);
		String type = chunks[2];
		String name = chunks[3];
		if ("print3d".equals(type))
			return hwService.findHWItemPrint3dFilePath(id, name);
		if ("img".equals(type))
			return hwService.findHWItemImagesFilePath(id, name);
		if ("icon".equals(type))
			try {
				return hwService.findHWItemIconFile(id);
			} catch (IOException e) {
				throw new GrassPageException(404, "Nezdařilo se načíst požadovaný soubor");
			}
		return hwService.findHWItemDocumentsFilePath(id, name);
	}


	@Override
	protected String getMimeType(Path file) {
		String type = super.getMimeType(file);
		return type + "; charset=utf-8";
	}

}
