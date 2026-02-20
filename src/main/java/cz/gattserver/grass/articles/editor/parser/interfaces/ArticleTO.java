package cz.gattserver.grass.articles.editor.parser.interfaces;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ArticleTO extends ArticleRESTTO {

    private String text;
    private String searchableOutput;
    private Set<String> pluginJSCodes;
    private String attachmentsDirId;

    public ArticleTO(String text) {
        this.text = text;
    }

}