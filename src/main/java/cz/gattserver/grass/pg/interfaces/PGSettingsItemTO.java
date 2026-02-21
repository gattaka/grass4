package cz.gattserver.grass.pg.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
public class PGSettingsItemTO implements Comparable<PGSettingsItemTO>, Serializable {

    @Serial
    private static final long serialVersionUID = -6240185413941887320L;

    private Path path;
    private PhotogalleryRESTOverviewTO overviewTO;
    private Long size;
    private Long filesCount;
    private Date date;

    @Override
    public int compareTo(PGSettingsItemTO o) {
        return path.getFileName().toString().compareTo(o.getPath().getFileName().toString());
    }
}