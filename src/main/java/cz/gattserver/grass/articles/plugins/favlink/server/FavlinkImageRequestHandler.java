package cz.gattserver.grass.articles.plugins.favlink.server;

import cz.gattserver.grass.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass.core.server.AbstractConfiguratedPathRequestHandler;

import javax.servlet.annotation.WebServlet;
import java.io.FileNotFoundException;
import java.nio.file.Path;

@WebServlet(urlPatterns = "/" + FavlinkConfiguration.IMAGE_PATH_ALIAS + "/*")
public class FavlinkImageRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		FavlinkConfiguration configuration = new FavlinkConfiguration();
		getConfigurationService().loadConfiguration(configuration);
		return getFileSystemService().getFileSystem().getPath(configuration.getOutputPath(), fileName);
	}

}
