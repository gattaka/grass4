package cz.gattserver.grass.pg.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * TO objekt pro přenos základních obsahových informací o fotogalerii
 *
 * @author Hynek
 *
 */
@Setter
@Getter
@AllArgsConstructor
public class PhotogalleryCreateTO {

    private String name;
    private String galleryDir;
    private Collection<String> tags;
    private boolean publicated;
    private boolean reprocess;

}