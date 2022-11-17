package cz.gattserver.grass.print3d.config;


import cz.gattserver.grass.core.config.AbstractConfiguration;

/**
 * @author Hynek
 * 
 */
public class Print3dConfiguration extends AbstractConfiguration {

	public static final String PRINT3D_PATH = "print3d-files";

	private String rootDir = "rootDir";

	public Print3dConfiguration() {
		super("cz.gattserver.grass3.print3d");
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

}
