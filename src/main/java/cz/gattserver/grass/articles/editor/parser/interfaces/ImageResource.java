package cz.gattserver.grass.articles.editor.parser.interfaces;

import java.io.InputStream;

public class ImageResource {

    public interface Load {
        InputStream get();
    }

    public ImageResource(String icon, Load load) {
    }
}
