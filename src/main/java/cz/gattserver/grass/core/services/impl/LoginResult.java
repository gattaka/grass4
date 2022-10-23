package cz.gattserver.grass.core.services.impl;

public enum LoginResult {

	SUCCESS(true),

	FAILED_CREDENTIALS(false),

	FAILED_LOCKED(false),

	FAILED_DISABLED(false);

	private boolean success;

	private LoginResult(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}
}
