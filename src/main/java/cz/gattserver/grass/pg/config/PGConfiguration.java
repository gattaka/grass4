package cz.gattserver.grass.pg.config;


import cz.gattserver.grass.core.config.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Hynek
 * 
 */
@Setter
@Getter
public class PGConfiguration extends AbstractConfiguration implements Serializable {

    @Serial
    private static final long serialVersionUID = -4927306254703367064L;

    public static final String PG_PATH = "pg-files";

    private String rootDir = "rootDir";

	private String miniaturesDir = "foto_mini";

	private String previewsDir = "video_preview";

	private String slideshowDir = "foto_slideshow";

	public PGConfiguration() {
		super("cz.gattserver.grass3.pg");
	}

}
