package cz.gattserver.grass.articles.editor.parser.exceptions;

/**
 * Obecná výjimka pro hlášení chyby během parsování
 * 
 * @author Hynek
 *
 */
public class ParserException extends RuntimeException {

	private static final long serialVersionUID = -4168431404585234070L;

	public ParserException(Throwable cause) {
		super(cause);
	}

	public ParserException(String message) {
		super(message);
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserException() {
	}
}
