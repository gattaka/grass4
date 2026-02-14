package cz.gattserver.common.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public abstract class ApplicationException extends RuntimeException {

	protected final String id;
	protected final String timeStamp;
	protected final String localizedErrorMessage;
	protected final String originalExceptionStackTrace;

	public ApplicationException(String localizedErrorMessage) {
		this(localizedErrorMessage, null);
	}

	public ApplicationException(String localizedErrorMessage, Throwable throwable) {
		super(throwable);
		this.id = UUID.randomUUID().toString();
		this.timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		this.localizedErrorMessage = localizedErrorMessage;
		this.originalExceptionStackTrace = throwable == null ? null : ExceptionUtils.getStackTrace(throwable);
	}

	public String getId() {
		return id;
	}

	public String getLocalizedErrorMessage() {
		return localizedErrorMessage;
	}

	public String getTimeStamp() {
		return timeStamp;
	}
}
