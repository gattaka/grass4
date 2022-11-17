package cz.gattserver.grass.print3d.interfaces;

import java.nio.file.Path;

public class Print3dSettingsItemTO {

	private Path path;

	public Print3dSettingsItemTO(Path path) {
		this.path = path;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

}
