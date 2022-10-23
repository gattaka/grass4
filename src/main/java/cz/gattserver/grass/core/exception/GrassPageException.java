package cz.gattserver.grass.core.exception;

public class GrassPageException extends RuntimeException {

	private static final long serialVersionUID = -8947153927785372443L;
	private static final String ERR = "Error: ";

	private final int status;

	public GrassPageException(int status, Throwable e) {
		super(ERR + status, e);
		this.status = status;
	}

	public GrassPageException(int status, String msg, Throwable e) {
		super(ERR + status + ", " + msg, e);
		this.status = status;
	}

	public GrassPageException(int status) {
		super(ERR + status);
		this.status = status;
	}

	public GrassPageException(int status, String msg) {
		super(ERR + status + ", " + msg);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
