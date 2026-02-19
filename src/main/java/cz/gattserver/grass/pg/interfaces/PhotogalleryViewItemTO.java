package cz.gattserver.grass.pg.interfaces;

import cz.gattserver.common.slideshow.ExifInfoTO;
import cz.gattserver.common.slideshow.MediaType;
import cz.gattserver.common.slideshow.SlideshowItem;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class PhotogalleryViewItemTO implements SlideshowItem, Serializable {

    @Serial
    private static final long serialVersionUID = -321662240494516193L;

    private String name;
    private MediaType type;
    private ExifInfoTO exifInfoTO;
    private String slideshowPath;
    private String fullPath;
    private String miniaturePath;

}