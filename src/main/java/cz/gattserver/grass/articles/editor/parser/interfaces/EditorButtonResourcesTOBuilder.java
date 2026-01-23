package cz.gattserver.grass.articles.editor.parser.interfaces;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder pro immutable {@link EditorButtonResourcesTO}
 *
 * @author Hynek
 */
public class EditorButtonResourcesTOBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String tag;
    private String tagFamily;
    private String description;
    private String prefix;
    private String suffix;
    private String imagePath;

    /**
     * @param tag         název tagu, použitý v tagové značce
     * @param tagFamily   rodina tagů, do které bude ve výběru začleněn
     * @param description nápis na vkládacím prvku v editoru (popisek tlačítka)
     * @param prefix      počáteční tag + (nepovinné) nějaké věci, které se mají vložit
     *                    před označený text
     * @param suffix      koncový tag + (nepovinné) nějaké věci, které se mají vložit za
     *                    označený text
     * @param imagePath   cesta k ikoně pluginu
     */
    public EditorButtonResourcesTOBuilder(String tag, String tagFamily, String description, String prefix,
                                          String suffix, String imagePath) {
        this.tag = tag;
        this.tagFamily = tagFamily;
        this.description = description;
        this.prefix = prefix;
        this.suffix = suffix;
        this.imagePath = imagePath;
    }

    /**
     * Konstruktor pro případy "běžných" prvků, kdy je všechno stejné - jak
     * popisek, tak počteční a koncový tag. Z logiky věci vyplývá, že zadávaný
     * parametr je pouze název elementu/tagu bez hranatých závorek nebo lomítek
     *
     * @param tag       název tagu, použitý v tagové značce
     * @param tagFamily rodina tagů, do které bude ve výběru začleněn
     */
    public EditorButtonResourcesTOBuilder(String tag, String tagFamily) {
        this.tag = tag;
        this.tagFamily = tagFamily;
        this.imagePath = null;
    }

    /**
     * Vezme nasetovaná data a vytvoří z nich {@link EditorButtonResourcesTO}
     *
     * @return {@link EditorButtonResourcesTO} instance
     */
    public EditorButtonResourcesTO build() {
        Validate.notBlank(tag);
        String pfx = this.prefix;
        String sfx = this.suffix;
        String defaultPrefix = '[' + tag + ']';
        String defaultSuffix = "[/" + tag + ']';
        if (StringUtils.isBlank(pfx)) pfx = defaultPrefix;
        if (StringUtils.isBlank(sfx)) sfx = defaultSuffix;

        // Pokud nemá ani text tlačítka, ani obrázek, dej jako text tlačítka
        // jméno tagu
        String dsc = this.description;
        if (StringUtils.isBlank(dsc) && imagePath == null) dsc = tag;

        if (!pfx.startsWith(defaultPrefix)) throw new IllegalArgumentException("Prefix musí začínat: " + defaultPrefix);
        if (!sfx.endsWith(defaultSuffix)) throw new IllegalArgumentException("Suffix musí končit: " + defaultSuffix);

        return new EditorButtonResourcesTO(tag, tagFamily, dsc, pfx, sfx, imagePath);
    }

    public EditorButtonResourcesTOBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public EditorButtonResourcesTOBuilder setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public EditorButtonResourcesTOBuilder setSuffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    /**
     * Nastaví zdroj obrázku
     *
     * @param imagePath zdroj
     * @return <code>this</code> pro řetězení
     */
    public EditorButtonResourcesTOBuilder setImage(String imagePath) {
        this.imagePath = "articles/" + imagePath;
        return this;
    }

}