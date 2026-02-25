package cz.gattserver.grass.articles;

import cz.gattserver.grass.articles.editor.parser.interfaces.AttachmentTO;

/**
 * Stav zpracování přílohy článku
 */
public record AttachmentsOperationResult(AttachmentsState state, AttachmentTO attachment) {

    private AttachmentsOperationResult(AttachmentsState state) {
        this(state, null);
    }

    public static AttachmentsOperationResult success(AttachmentTO attachment) {
        return new AttachmentsOperationResult(AttachmentsState.SUCCESS, attachment);
    }

    public static AttachmentsOperationResult notValid() {
        return new AttachmentsOperationResult(AttachmentsState.NOT_VALID);
    }

    public static AttachmentsOperationResult systemError() {
        return new AttachmentsOperationResult(AttachmentsState.SYSTEM_ERROR);
    }

    public static AttachmentsOperationResult alreadyExists() {
        return new AttachmentsOperationResult(AttachmentsState.ALREADY_EXISTS);
    }


}