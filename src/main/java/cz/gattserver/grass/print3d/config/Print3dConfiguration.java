package cz.gattserver.grass.print3d.config;


import cz.gattserver.grass.core.config.AbstractConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Hynek
 *
 */
@Setter
@Getter
public class Print3dConfiguration extends AbstractConfiguration implements Serializable {

    @Serial
    private static final long serialVersionUID = 3185228743362557896L;

    public static final String PRINT3D_PATH = "print3d-files";

    private String rootDir = "rootDir";

    public Print3dConfiguration() {
        super("cz.gattserver.grass3.print3d");
    }

}