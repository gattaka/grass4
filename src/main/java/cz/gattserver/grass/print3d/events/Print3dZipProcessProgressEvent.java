package cz.gattserver.grass.print3d.events;


import cz.gattserver.grass.core.events.ProgressEvent;

import java.io.Serial;
import java.io.Serializable;

public record Print3dZipProcessProgressEvent(String description) implements ProgressEvent, Serializable {

    @Serial
    private static final long serialVersionUID = 3284338817357369704L;

}