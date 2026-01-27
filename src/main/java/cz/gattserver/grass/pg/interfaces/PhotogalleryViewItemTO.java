package cz.gattserver.grass.pg.interfaces;

import cz.gattserver.common.slideshow.ExifInfoTO;
import cz.gattserver.common.slideshow.MediaType;
import cz.gattserver.common.slideshow.SlideshowItem;

public class PhotogalleryViewItemTO implements SlideshowItem {

    private String name;
    private MediaType type;
    private ExifInfoTO exifInfoTO;
    private String slideshowPath;
    private String fullPath;
    private String miniaturePath;

    @Override
    public ExifInfoTO getExifInfoTO() {
        return exifInfoTO;
    }

    public PhotogalleryViewItemTO setExifInfoTO(ExifInfoTO exifInfoTO) {
        this.exifInfoTO = exifInfoTO;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    public PhotogalleryViewItemTO setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public MediaType getType() {
        return type;
    }

    public PhotogalleryViewItemTO setType(MediaType type) {
        this.type = type;
        return this;
    }

    public String getSlideshowPath() {
        return slideshowPath;
    }

    public void setSlideshowPath(String slideshowPath) {
        this.slideshowPath = slideshowPath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getMiniaturePath() {
        return miniaturePath;
    }

    public void setMiniaturePath(String miniaturePath) {
        this.miniaturePath = miniaturePath;
    }
}