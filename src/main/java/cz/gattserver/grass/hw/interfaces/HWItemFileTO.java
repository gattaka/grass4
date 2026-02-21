package cz.gattserver.grass.hw.interfaces;

import cz.gattserver.common.slideshow.ExifInfoTO;
import cz.gattserver.common.slideshow.MediaType;
import cz.gattserver.common.slideshow.SlideshowItem;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class HWItemFileTO implements SlideshowItem {

	private String name;
	private String size;
	private LocalDateTime lastModified;

    @Override
    public MediaType getType() {
        return MediaType.IMAGE;
    }

    @Override
    public ExifInfoTO getExifInfoTO() {
        return null;
    }

}
