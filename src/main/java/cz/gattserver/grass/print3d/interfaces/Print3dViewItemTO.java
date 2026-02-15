package cz.gattserver.grass.print3d.interfaces;

import java.nio.file.Path;

public class Print3dViewItemTO {

	private Path path;
	private String onlyName;
	private String extension;
	private String size;
	private Print3dItemType type;

	public Print3dViewItemTO(Path path, String onlyName, String extension, String size, Print3dItemType type) {
		this.path = path;
		this.onlyName = onlyName;
		this.extension = extension;
		this.size = size;
		this.type = type;
	}

    public Print3dViewItemTO(Path path, String onlyName, String size) {
        this.path = path;
        this.onlyName = onlyName;
        this.size = size;
    }

    public Print3dViewItemTO() {
	}

	public String getOnlyName() {
		return onlyName;
	}

	public Print3dViewItemTO setOnlyName(String onlyName) {
		this.onlyName = onlyName;
		return this;
	}

	public String getExtension() {
		return extension;
	}

	public Print3dViewItemTO setExtension(String extension) {
		this.extension = extension;
		return this;
	}

	public Print3dItemType getType() {
		return type;
	}

	public Print3dViewItemTO setType(Print3dItemType type) {
		this.type = type;
		return this;
	}

	public String getName() {
		if (onlyName == null)
			return null;
		if (extension == null)
			return onlyName;
		return onlyName + "." + extension;
	}

	public Path getPath() {
		return path;
	}

	public Print3dViewItemTO setPath(Path path) {
		this.path = path;
		return this;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
