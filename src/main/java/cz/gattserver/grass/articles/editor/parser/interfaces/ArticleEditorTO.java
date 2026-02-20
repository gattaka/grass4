package cz.gattserver.grass.articles.editor.parser.interfaces;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ArticleEditorTO {

    private Long nodeId;
    private String nodeName;

    private String contextRoot;
    private Long existingArticleId;

    /*
     * Vždy draft
     */
    private Long draftId;
    private String draftName;
    private Set<String> draftTags = new HashSet<>();
    private String draftText;
    private boolean draftPublicated;
    // mix souborů z adresáře existujícího článku a z draft adresáře
    private Set<AttachmentTO> draftAttachments = new HashSet<>();

    public ArticleEditorTO(String contextRoot) {
        this.contextRoot = contextRoot;
    }
}