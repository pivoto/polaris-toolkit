package io.polaris.core.string;

/**
 * @author Qt
 * @since  Aug 11, 2023
 */
public class TokenParser {

	private final String openToken;
	private final String closeToken;
	private final TokenHandler handler;
	private final boolean trimEscapeCharacter;

	public TokenParser(String openToken, String closeToken, TokenHandler handler) {
		this(openToken, closeToken, handler, true);
	}

	public TokenParser(String openToken, String closeToken, TokenHandler handler, boolean trimEscapeCharacter) {
		this.openToken = openToken;
		this.closeToken = closeToken;
		this.handler = handler;
		this.trimEscapeCharacter = trimEscapeCharacter;
	}

	public String parse(String text) {
		return TokenParsers.parse(text, openToken, closeToken, handler, trimEscapeCharacter);
	}


}
