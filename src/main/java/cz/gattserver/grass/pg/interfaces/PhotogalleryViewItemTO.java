package cz.gattserver.grass.pg.interfaces;

import java.nio.file.Path;

public class PhotogalleryViewItemTO {

	private Path file;
	private String name;
	private PhotogalleryItemType type;
	private ExifInfoTO exifInfoTO;

	public ExifInfoTO getExifInfoTO() {
		return exifInfoTO;
	}

	public PhotogalleryViewItemTO setExifInfoTO(ExifInfoTO exifInfoTO) {
		this.exifInfoTO = exifInfoTO;
		return this;
	}

	public String getName() {
		return name;
	}

	public PhotogalleryViewItemTO setName(String name) {
		this.name = name;
		return this;
	}

	public PhotogalleryItemType getType() {
		return type;
	}

	public PhotogalleryViewItemTO setType(PhotogalleryItemType type) {
		this.type = type;
		return this;
	}

	public Path getFile() {
		return file;
	}

	public PhotogalleryViewItemTO setFile(Path file) {
		this.file = file;
		return this;
	}

}
