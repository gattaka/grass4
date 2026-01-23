package cz.gattserver.grass.articles.editor.parser.interfaces;

import com.vaadin.flow.component.html.Image;

/**
 * Třída obsahující všechny potřebné informace pro začlenění pluginu do UI
 * nabídky elementů v editoru
 *
 * @author gatt
 */
public class EditorButtonResourcesTO implements Comparable<EditorButtonResourcesTO> {

    private final String tag;
    private final String tagFamily;
    private final String description;
    private final String prefix;
    private final String suffix;
    private final String imagePath;

    protected EditorButtonResourcesTO(String tag, String tagFamily, String description, String prefix, String suffix,
                                      String imagePath) {
        this.tag = tag;
        this.tagFamily = tagFamily;
        this.description = description;
        this.prefix = prefix;
        this.suffix = suffix;
        this.imagePath = imagePath;
    }

    /**
     * Získá popisek elementu
     *
     * @return popisek
     */
    public String getDescription() {
        return description;
    }

    /**
     * Vkládaný text před označenou část článku
     *
     * @return vkládaný text
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Vkládaný text za označenou část článku
     *
     * @return vkládaný text
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Získá název rodiny elementů, pod kterou má být seskupen plugin v editoru
     *
     * @return název rodiny pluginů - např. LaTeX, HTML, FancyNadpisy apod.
     */
    public String getTagFamily() {
        return tagFamily;
    }

    /**
     * Získá zdroj pro obrázek tlačítka
     *
     * @return resource obrázku
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Získá tag
     *
     * @return název tagu
     */
    public String getTag() {
        return tag;
    }

    public int compareTo(EditorButtonResourcesTO o) {
        return tag.compareTo(o.getTag());
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EditorButtonResourcesTO) {
            return tag.equals(((EditorButtonResourcesTO) obj).getTag());
        } else return false;
    }

}
