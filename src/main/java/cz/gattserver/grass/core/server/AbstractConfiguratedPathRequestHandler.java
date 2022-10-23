package cz.gattserver.grass.core.server;

import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.web.common.spring.SpringContextHelper;

public abstract class AbstractConfiguratedPathRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = -8278020128728088151L;

	private transient ConfigurationService configurationService;
	private transient FileSystemService fileSystemService;

	protected ConfigurationService getConfigurationService() {
		// získává se takhle aby nemusela být transient/serializable
		if (configurationService == null)
			configurationService = SpringContextHelper.getBean(ConfigurationService.class);
		return configurationService;
	}

	protected FileSystemService getFileSystemService() {
		// získává se takhle aby nemusela být transient/serializable
		if (fileSystemService == null)
			fileSystemService = SpringContextHelper.getBean(FileSystemService.class);
		return fileSystemService;
	}

}
