package io.polaris.core.string;

/**
 * @author Qt
 * @since  Aug 11, 2023
 */
public class TokenParsers {


	public static String parse(String text, String openToken, String closeToken, TokenHandler handler) {
		return parse(text, openToken, closeToken, handler, true);
	}

	public static String parse(String text, String openToken, String closeToken, TokenHandler handler, boolean trimEscapeCharacter) {
		if (text == null || text.isEmpty()) {
			return "";
		}
		// 查找 openToken
		int start = text.indexOf(openToken, 0);
		if (start == -1) {
			return text;
		}

		char[] src = text.toCharArray();
		int offset = 0;

		final StringBuilder builder = new StringBuilder();
		StringBuilder expression = null;

		for (; start > -1; start = text.indexOf(openToken, offset)) {
			if (start > 0 && src[start - 1] == '\\') {
				// 转义符处理
				if (trimEscapeCharacter) {
					builder.append(src, offset, start - offset - 1).append(openToken);
				} else {
					builder.append(src, offset, start - offset).append(openToken);
				}
				offset = start + openToken.length();
				continue;
			}

			// 查找 closeToken
			if (expression == null) {
				expression = new StringBuilder();
			} else {
				expression.setLength(0);
			}
			builder.append(src, offset, start - offset);
			offset = start + openToken.length();
			int end = text.indexOf(closeToken, offset);
			while (end > -1) {
				if (end > offset && src[end - 1] == '\\') {
					// 转义符处理
					if (trimEscapeCharacter) {
						expression.append(src, offset, end - offset - 1).append(closeToken);
					} else {
						expression.append(src, offset, end - offset).append(closeToken);
					}
					offset = end + closeToken.length();
					end = text.indexOf(closeToken, offset);
				} else {
					expression.append(src, offset, end - offset);
					offset = end + closeToken.length();
					break;
				}
			}
			if (end == -1) {
				// 找不到 endToken
				builder.append(src, start, src.length - start);
				offset = src.length;
			} else {
				// 处理 token
				builder.append(handler.handleToken(expression.toString().trim()));
				offset = end + closeToken.length();
			}
		}

		if (offset < src.length) {
			builder.append(src, offset, src.length - offset);
		}
		return builder.toString();
	}

	@Deprecated
	static String parse0(String text, String openToken, String closeToken, TokenHandler handler, boolean trimEscapeCharacter) {
		if (text == null || text.isEmpty()) {
			return "";
		}
		// 查找 openToken
		int start = text.indexOf(openToken, 0);
		if (start == -1) {
			return text;
		}

		int offset = 0;

		final StringBuilder builder = new StringBuilder();
		StringBuilder expression = null;

		//char[] src = text.toCharArray();
		for (; start > -1; start = text.indexOf(openToken, offset)) {
			//if (start > 0 && src[start - 1] == '\\') {//src[start - 1]
			if (start > 0 && text.charAt(start - 1) == '\\') {
				// 转义符处理
				if (trimEscapeCharacter) {
					//builder.append(src, offset, start - offset - 1).append(openToken);
					builder.append(text, offset, start - 1).append(openToken);
				} else {
					//builder.append(src, offset, start - offset).append(openToken);
					builder.append(text, offset, start).append(openToken);
				}
				offset = start + openToken.length();
				continue;
			}

			// 查找 closeToken
			if (expression == null) {
				expression = new StringBuilder();
			} else {
				expression.setLength(0);
			}
			//builder.append(src, offset, start - offset);
			builder.append(text, offset, start);
			offset = start + openToken.length();
			int end = text.indexOf(closeToken, offset);
			while (end > -1) {
				// if (end > offset && src[end - 1] == '\\') {
				if (end > offset && text.charAt(end - 1) == '\\') {
					// 转义符处理
					if (trimEscapeCharacter) {
						expression.append(text, offset, end - 1).append(closeToken);
						// expression.append(src, offset, end - offset - 1).append(closeToken);
					} else {
						expression.append(text, offset, end).append(closeToken);
						// expression.append(src, offset, end - offset).append(closeToken);
					}
					offset = end + closeToken.length();
					end = text.indexOf(closeToken, offset);
				} else {
					expression.append(text, offset, end);
					// expression.append(src, offset, end - offset);
					offset = end + closeToken.length();
					break;
				}
			}
			if (end == -1) {
				// 找不到 endToken
				builder.append(text, start, text.length());
				//builder.append(src, start, src.length - start);
				offset = text.length();
				//offset = src.length;
			} else {
				// 处理 token
				builder.append(handler.handleToken(expression.toString()));
				offset = end + closeToken.length();
			}
		}

		//if (offset < src.length) {
		//	builder.append(src, offset, src.length - offset);
		//}
		if (offset < text.length()) {
			builder.append(text, offset, text.length());
		}
		return builder.toString();
	}

}
