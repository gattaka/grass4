package cz.gattserver.grass.pg.config;


import cz.gattserver.grass.core.config.AbstractConfiguration;

/**
 * @author Hynek
 * 
 */
public class PGConfiguration extends AbstractConfiguration {

	public static final String PG_PATH = "pg-files";

	private String rootDir = "rootDir";

	private String miniaturesDir = "foto_mini";

	private String previewsDir = "video_preview";

	private String slideshowDir = "foto_slideshow";

	public PGConfiguration() {
		super("cz.gattserver.grass3.pg");
	}

	public String getSlideshowDir() {
		return slideshowDir;
	}

	public void setSlideshowDir(String slideshowDir) {
		this.slideshowDir = slideshowDir;
	}

	public String getMiniaturesDir() {
		return miniaturesDir;
	}

	public void setMiniaturesDir(String miniaturesDir) {
		this.miniaturesDir = miniaturesDir;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public String getPreviewsDir() {
		return previewsDir;
	}

	public void setPreviewsDir(String previewDir) {
		this.previewsDir = previewDir;
	}

}
