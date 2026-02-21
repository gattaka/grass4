package cz.gattserver.grass.pg.config;


import cz.gattserver.grass.core.config.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Hynek
 * 
 */
@Setter
@Getter
public class PGConfiguration extends AbstractConfiguration {

	public static final String PG_PATH = "pg-files";

	private String rootDir = "rootDir";

	private String miniaturesDir = "foto_mini";

	private String previewsDir = "video_preview";

	private String slideshowDir = "foto_slideshow";

	public PGConfiguration() {
		super("cz.gattserver.grass3.pg");
	}

}
