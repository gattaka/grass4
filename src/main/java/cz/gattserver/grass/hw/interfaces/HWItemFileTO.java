package cz.gattserver.grass.hw.interfaces;

import cz.gattserver.common.slideshow.ExifInfoTO;
import cz.gattserver.common.slideshow.MediaType;
import cz.gattserver.common.slideshow.SlideshowItem;

import java.time.LocalDateTime;

public class HWItemFileTO implements SlideshowItem {

	private String name;
	private String size;
	private LocalDateTime lastModified;

	public String getSize() {
		return size;
	}

	public HWItemFileTO setSize(String size) {
		this.size = size;
		return this;
	}

    @Override
    public MediaType getType() {
        return MediaType.IMAGE;
    }

    @Override
    public ExifInfoTO getExifInfoTO() {
        return null;
    }

    public String getName() {
		return name;
	}

    public HWItemFileTO setName(String name) {
		this.name = name;
		return this;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public HWItemFileTO setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
		return this;
	}

}
