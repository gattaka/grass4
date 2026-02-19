package cz.gattserver.grass.articles.editor.parser.interfaces;

import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleDraftOverviewTO {

    /**
     * DB identifikátor
     */
    private Long id;

    /**
     * Náhled článku
     */
    private String text;

    /**
     * Meta-informace o obsahu
     */
    private ContentNodeTO contentNode;
}