package cz.gattserver.grass.articles.plugins.favlink.config;

import cz.gattserver.grass.core.config.AbstractConfiguration;

public class FavlinkConfiguration extends AbstractConfiguration {

	public static final String IMAGE_PATH_ALIAS = "articles-favlink-plugin";

	private String outputPath = "favlink/cache";

	public FavlinkConfiguration() {
		super("cz.gattserver.grass.articles.favlink");
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

}
