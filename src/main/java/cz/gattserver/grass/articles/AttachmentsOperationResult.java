package cz.gattserver.grass.articles;

/**
 * Stav zpracování přílohy článku
 */
public class AttachmentsOperationResult {

	private AttachmentsState state;
	private String attachmentDirId;

	private AttachmentsOperationResult(AttachmentsState state) {
		this.state = state;
	}

	public static AttachmentsOperationResult success(String attachmentDirId) {
		AttachmentsOperationResult result = new AttachmentsOperationResult(AttachmentsState.SUCCESS);
		result.attachmentDirId = attachmentDirId;
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

	public String getAttachmentDirId() {
		return attachmentDirId;
	}
}