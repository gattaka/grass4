package cz.gattserver.grass.print3d;

import cz.gattserver.grass.core.server.AbstractConfiguratedPathRequestHandler;
import cz.gattserver.grass.print3d.config.Print3dConfiguration;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.nio.file.Path;

@WebServlet(urlPatterns = "/" + Print3dConfiguration.PRINT3D_PATH + "/*")
public class Print3dRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Override
	protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
		Print3dConfiguration configuration = new Print3dConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return getFileSystemService().getFileSystem().getPath(configuration.getRootDir(), fileName);
	}
}
