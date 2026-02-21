package cz.gattserver.grass.print3d.interfaces;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;

public record Print3dViewItemTO(Path path, String onlyName, String extension, String size, Print3dItemType type)
        implements Serializable {

    @Serial
    private static final long serialVersionUID = 300196469450761540L;

    public String getName() {
        if (onlyName == null) return null;
        if (extension == null) return onlyName;
        return onlyName + "." + extension;
    }
}