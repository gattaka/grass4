package cz.gattserver.grass.print3d.interfaces;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Setter
@Getter
public class Print3dSettingsItemTO {

	private Path path;

	public Print3dSettingsItemTO(Path path) {
		this.path = path;
	}

}
