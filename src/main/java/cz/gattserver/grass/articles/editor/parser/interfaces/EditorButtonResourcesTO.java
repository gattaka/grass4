package cz.gattserver.grass.articles.editor.parser.interfaces;

import lombok.Builder;

/**
 * Třída obsahující všechny potřebné informace pro začlenění pluginu do UI
 * nabídky elementů v editoru
 *
 * @author gatt
 */
public record EditorButtonResourcesTO(String tag, String tagFamily, String description,
                                      String prefix, String suffix, String imagePath)
        implements Comparable<EditorButtonResourcesTO> {

    public int compareTo(EditorButtonResourcesTO o) {
        return tag.compareTo(o.tag());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EditorButtonResourcesTO that)) return false;

        return tag.equals(that.tag);
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }
}