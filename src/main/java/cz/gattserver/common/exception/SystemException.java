package cz.gattserver.common.exception;

import java.io.Serial;

public class SystemException extends ApplicationException {

    @Serial
    private static final long serialVersionUID = -296340491520121296L;

    public SystemException(String errorMessage, Throwable originalException) {
		super(errorMessage, originalException);
	}

	@Override
	public String toString() {
		return "SystemException {\n" + "  id:                           " + id + "\n"
				+ "  timestamp:                    " + timeStamp + "\n" + "  localizedErrorMessage:        "
				+ localizedErrorMessage + "\n" + "  originalExceptionStackTrace:  " + "{\n" + "    "
				+ originalExceptionStackTrace + "  }\n" + "}";
	}
}