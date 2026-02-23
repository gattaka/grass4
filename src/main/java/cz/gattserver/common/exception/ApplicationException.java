package cz.gattserver.common.exception;

import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serial;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public abstract class ApplicationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -3276373462934874664L;

    protected final String id;
	protected final String timeStamp;
	protected final String localizedErrorMessage;
	protected final String originalExceptionStackTrace;

	public ApplicationException(String localizedErrorMessage, Throwable throwable) {
		super(throwable);
		this.id = UUID.randomUUID().toString();
		this.timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		this.localizedErrorMessage = localizedErrorMessage;
		this.originalExceptionStackTrace = throwable == null ? null : ExceptionUtils.getStackTrace(throwable);
	}
}