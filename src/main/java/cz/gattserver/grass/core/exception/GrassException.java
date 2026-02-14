package cz.gattserver.grass.core.exception;

public class GrassException extends RuntimeException {

	public GrassException() {
	}

	public GrassException(String msg, Throwable e) {
		super(msg, e);
	}

}