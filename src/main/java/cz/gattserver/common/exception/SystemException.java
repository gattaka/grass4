package cz.gattserver.common.exception;

public class SystemException extends ApplicationException {

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
