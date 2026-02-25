package cz.gattserver.grass.hw.interfaces;

import cz.gattserver.common.slideshow.ExifInfoTO;
import cz.gattserver.common.slideshow.MediaType;
import cz.gattserver.common.slideshow.SlideshowItem;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
public class HWItemFileTO implements SlideshowItem, Serializable {

    @Serial
    private static final long serialVersionUID = 648049593899110826L;

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