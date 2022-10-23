package cz.gattserver.grass.core.exception;

public class GrassException extends RuntimeException {

	private static final long serialVersionUID = 1349573580267562770L;

	public GrassException() {
	}

	public GrassException(String msg, Throwable e) {
		super(msg, e);
	}

}
