package cz.gattserver.common.slideshow;

public interface SlideshowItem {

    MediaType getType();

    ExifInfoTO getExifInfoTO();

    String getName();

}