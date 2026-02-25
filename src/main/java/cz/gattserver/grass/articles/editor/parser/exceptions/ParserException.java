package cz.gattserver.grass.articles.editor.parser.exceptions;

import java.io.Serial;

/**
 * Obecná výjimka pro hlášení chyby během parsování
 * 
 * @author Hynek
 *
 */
public class ParserException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6434432342382364093L;

	public ParserException(String message) {
		super(message);
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParserException() {
	}
}
