package cz.gattserver.grass.articles.editor.parser.exceptions;

/**
 * Obecná výjimka pro hlášení chyby během parsování
 * 
 * @author Hynek
 *
 */
public class ParserException extends RuntimeException {

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
