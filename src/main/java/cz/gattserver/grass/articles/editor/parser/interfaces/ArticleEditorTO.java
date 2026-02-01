package cz.gattserver.grass.articles.editor.parser.interfaces;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ArticleEditorTO {

    /*
     * Kategorie
     */
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

    public Set<AttachmentTO> getDraftAttachments() {
        return draftAttachments;
    }

    public void setDraftAttachments(Set<AttachmentTO> draftAttachments) {
        this.draftAttachments = draftAttachments;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Long getExistingArticleId() {
        return existingArticleId;
    }

    public void setExistingArticleId(Long existingArticleId) {
        this.existingArticleId = existingArticleId;
    }

    public String getDraftName() {
        return draftName;
    }

    public void setDraftName(String draftName) {
        this.draftName = draftName;
    }

    public Set<String> getDraftTags() {
        return draftTags;
    }

    public void setDraftTags(Set<String> draftTags) {
        this.draftTags = draftTags;
    }

    public String getDraftText() {
        return draftText;
    }

    public void setDraftText(String draftText) {
        this.draftText = draftText;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public boolean isDraftPublicated() {
        return draftPublicated;
    }

    public void setDraftPublicated(boolean draftPublicated) {
        this.draftPublicated = draftPublicated;
    }

    public Long getDraftId() {
        return draftId;
    }

    public void setDraftId(Long draftId) {
        this.draftId = draftId;
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
    }
}
