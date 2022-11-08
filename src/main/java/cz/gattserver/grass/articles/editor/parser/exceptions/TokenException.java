package cz.gattserver.grass.articles.editor.parser.exceptions;

import cz.gattserver.common.util.StringPreviewCreator;
import cz.gattserver.grass.articles.editor.lexer.Token;
import org.apache.commons.lang3.Validate;

/**
 * Výjimka přesně pro případy, kdy byl očekáván nějaký {@link Token} a místo
 * toho byla nalezen jiný
 * 
 * @author Hynek
 *
 */
public class TokenException extends RuntimeException {

	private static final long serialVersionUID = -4168431404585234070L;

	private static final String EXPECTED_TOKEN_CHUNK = "Expected Token: ";
	private static final String ACTUAL_TOKEN_CHUNK = " Actual Token: ";
	private static final String EXPECTED_CONTENT_CHUNK = "Expected content: ";
	private static final String ACTUAL_CONTENT_CHUNK = " Actual content: ";
	
	private final String message;

	private void validateExpected(Object expected) {
		Validate.notNull(expected, "Expected nesmí být null");
	}

	private void validateActual(Object actual) {
		Validate.notNull(actual, "Actual nesmí být null");
	}

	public TokenException(Token expected, Token actual, String actualContent) {
		validateExpected(expected);
		validateActual(actual);
		this.message = EXPECTED_TOKEN_CHUNK + expected + ACTUAL_TOKEN_CHUNK + actual + " ("
				+ StringPreviewCreator.createPreview(actualContent, 20) + ")";
	}

	public TokenException(String expectedContent, String actualContent) {
		validateExpected(expectedContent);
		validateActual(actualContent);
		this.message = EXPECTED_CONTENT_CHUNK + expectedContent + ACTUAL_CONTENT_CHUNK
				+ StringPreviewCreator.createPreview(actualContent, 20);
	}

	public TokenException(Token expected, String tag) {
		validateExpected(expected);
		this.message = EXPECTED_TOKEN_CHUNK + expected + " (" + tag + ")" + ACTUAL_TOKEN_CHUNK + Token.EOF;
	}

	public TokenException(Token[] expected) {
		validateExpected(expected);
		this.message = "Expected Tokens: " + expected + ACTUAL_TOKEN_CHUNK + Token.EOF;
	}

	public TokenException(Token expected) {
		validateExpected(expected);
		this.message = EXPECTED_TOKEN_CHUNK + expected + ACTUAL_TOKEN_CHUNK + Token.EOF;
	}

	@Override
	public String toString() {
		return message;
	}
}
