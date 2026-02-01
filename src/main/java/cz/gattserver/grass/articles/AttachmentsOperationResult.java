package cz.gattserver.grass.articles;

import cz.gattserver.grass.articles.editor.parser.interfaces.AttachmentTO;

/**
 * Stav zpracování přílohy článku
 */
public class AttachmentsOperationResult {

	private AttachmentsState state;
	private AttachmentTO attachment;

	private AttachmentsOperationResult(AttachmentsState state) {
		this.state = state;
	}

	public static AttachmentsOperationResult success(AttachmentTO attachment) {
		AttachmentsOperationResult result = new AttachmentsOperationResult(AttachmentsState.SUCCESS);
		result.attachment = attachment;
		return result;
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

	public AttachmentsState getState() {
		return state;
	}

	public AttachmentTO getAttachment() {
		return attachment;
	}
}