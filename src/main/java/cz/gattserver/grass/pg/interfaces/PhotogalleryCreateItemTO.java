package cz.gattserver.grass.pg.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Setter
@Getter
@AllArgsConstructor
public class PhotogalleryCreateItemTO {

    private String name;
    private Path path;

}