package io.polaris.core.net;

import io.polaris.core.tuple.Pair;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Qt
 * @since 1.8
 */
public class URLEncoders {
	private static final char QP_SEP_A = '&';
	private static final char QP_SEP_S = ';';
	private static final String NAME_VALUE_SEPARATOR = "=";
	private static final char PATH_SEPARATOR = '/';

	private static final BitSet PATH_SEPARATORS = new BitSet(256);

	/**
	 * Unreserved characters, i.e. alphanumeric, plus: {@code _ - ! . ~ ' ( ) *}
	 * <p>
	 * This list is the same as the {@code unreserved} list in
	 * <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>
	 */
	private static final BitSet UNRESERVED = new BitSet(256);
	/**
	 * Punctuation characters: , ; : $ & + =
	 * <p>
	 * These are the additional characters allowed by userinfo.
	 */
	private static final BitSet PUNCTUATION = new BitSet(256);
	/**
	 * Characters which are safe to use in userinfo,
	 * i.e. {@link #UNRESERVED} plus {@link #PUNCTUATION}
	 */
	private static final BitSet USERINFO = new BitSet(256);
	/**
	 * Characters which are safe to use in a path,
	 * i.e. {@link #UNRESERVED} plus {@link #PUNCTUATION}  plus / @
	 */
	private static final BitSet PATH_SAFE = new BitSet(256);
	/**
	 * Characters which are safe to use in a query or a fragment,
	 * i.e. {@link #RESERVED} plus {@link #UNRESERVED}
	 */
	private static final BitSet URIC = new BitSet(256);
	/**
	 * Reserved characters, i.e. {@code ;/?:@&=+$,[]}
	 * <p>
	 * This list is the same as the {@code reserved} list in
	 * <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC 2396</a>
	 * as augmented by
	 * <a href="http://www.ietf.org/rfc/rfc2732.txt">RFC 2732</a>
	 */
	private static final BitSet RESERVED = new BitSet(256);
	/**
	 * Safe characters for x-www-form-urlencoded data, as per java.net.URLEncoder and browser behaviour,
	 * i.e. alphanumeric plus {@code "-", "_", ".", "*"}
	 */
	private static final BitSet URL_ENCODER = new BitSet(256);

	private static final BitSet PATH_SPECIAL = new BitSet(256);

	static {
		PATH_SEPARATORS.set(PATH_SEPARATOR);

		// unreserved chars
		// alpha characters
		for (int i = 'a'; i <= 'z'; i++) {
			UNRESERVED.set(i);
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			UNRESERVED.set(i);
		}
		// numeric characters
		for (int i = '0'; i <= '9'; i++) {
			UNRESERVED.set(i);
		}
		UNRESERVED.set('_'); // these are the charactes of the "mark" list
		UNRESERVED.set('-');
		UNRESERVED.set('.');
		UNRESERVED.set('*');
		URL_ENCODER.or(UNRESERVED); // skip remaining unreserved characters
		UNRESERVED.set('!');
		UNRESERVED.set('~');
		UNRESERVED.set('\'');
		UNRESERVED.set('(');
		UNRESERVED.set(')');

		// punct chars
		PUNCTUATION.set(',');
		PUNCTUATION.set(';');
		PUNCTUATION.set(':');
		PUNCTUATION.set('$');
		PUNCTUATION.set('&');
		PUNCTUATION.set('+');
		PUNCTUATION.set('=');
		// Safe for userinfo
		USERINFO.or(UNRESERVED);
		USERINFO.or(PUNCTUATION);

		// URL path safe
		PATH_SAFE.or(UNRESERVED);
		PATH_SAFE.set(';'); // param separator
		PATH_SAFE.set(':'); // RFC 2396
		PATH_SAFE.set('@');
		PATH_SAFE.set('&');
		PATH_SAFE.set('=');
		PATH_SAFE.set('+');
		PATH_SAFE.set('$');
		PATH_SAFE.set(',');

		PATH_SPECIAL.or(PATH_SAFE);
		PATH_SPECIAL.set('/');

		RESERVED.set(';');
		RESERVED.set('/');
		RESERVED.set('?');
		RESERVED.set(':');
		RESERVED.set('@');
		RESERVED.set('&');
		RESERVED.set('=');
		RESERVED.set('+');
		RESERVED.set('$');
		RESERVED.set(',');
		RESERVED.set('['); // added by RFC 2732
		RESERVED.set(']'); // added by RFC 2732

		URIC.or(RESERVED);
		URIC.or(UNRESERVED);
	}

	private static final int RADIX = 16;


	static List<Pair<String, String>> createEmptyList() {
		return new ArrayList<>(0);
	}

	static String urlEncode(String content, Charset charset, BitSet safeChars, boolean blankAsPlus) {
		if (content == null) {
			return null;
		}
		charset = charset != null ? charset : StandardCharsets.UTF_8;
		StringBuilder buf = new StringBuilder();
		ByteBuffer bb = charset.encode(content);
		while (bb.hasRemaining()) {
			int b = bb.get() & 0xff;
			if (safeChars.get(b)) {
				buf.append((char) b);
			} else if (blankAsPlus && b == ' ') {
				buf.append('+');
			} else {
				buf.append("%");
				char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, RADIX));
				char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, RADIX));
				buf.append(hex1);
				buf.append(hex2);
			}
		}
		return buf.toString();
	}

	static String urlDecode(String content, Charset charset, boolean plusAsBlank) {
		if (content == null) {
			return null;
		}
		charset = charset != null ? charset : StandardCharsets.UTF_8;
		ByteBuffer bb = ByteBuffer.allocate(content.length());
		CharBuffer cb = CharBuffer.wrap(content);
		while (cb.hasRemaining()) {
			char c = cb.get();
			if (c == '%' && cb.remaining() >= 2) {
				char uc = cb.get();
				char lc = cb.get();
				int u = Character.digit(uc, 16);
				int l = Character.digit(lc, 16);
				if (u != -1 && l != -1) {
					bb.put((byte) ((u << 4) + l));
				} else {
					bb.put((byte) '%');
					bb.put((byte) uc);
					bb.put((byte) lc);
				}
			} else if (plusAsBlank && c == '+') {
				bb.put((byte) ' ');
			} else {
				bb.put((byte) c);
			}
		}
		bb.flip();
		return charset.decode(bb).toString();
	}

	public static String decodeFormFields(String content) {
		return decodeFormFields(content, StandardCharsets.UTF_8);
	}

	public static String decodeFormFields(String content, String charset) {
		return decodeFormFields(content, charset != null ? Charset.forName(charset) : StandardCharsets.UTF_8);
	}

	public static String decodeFormFields(String content, Charset charset) {
		return decodeFormFields(content, charset, true);
	}

	public static String decodeFormFields(String content, Charset charset, boolean blankAsPlus) {
		return urlDecode(content, charset, blankAsPlus);
	}

	public static String encodeFormFields(String content) {
		return encodeFormFields(content, StandardCharsets.UTF_8);
	}

	public static String encodeFormFields(String content, String charset) {
		return encodeFormFields(content, charset != null ? Charset.forName(charset) : StandardCharsets.UTF_8);
	}

	public static String encodeFormFields(String content, Charset charset) {
		return encodeFormFields(content, charset, false);
	}

	public static String encodeFormFields(String content, Charset charset, boolean blankAsPlus) {
		return urlEncode(content, charset, URL_ENCODER, blankAsPlus);
	}

	public static String encodeUserInfo(String content) {
		return urlEncode(content, StandardCharsets.UTF_8, USERINFO, false);
	}

	public static String encodeUserInfo(String content, Charset charset) {
		return urlEncode(content, charset, USERINFO, false);
	}


	public static String encodeUric(String content) {
		return urlEncode(content, StandardCharsets.UTF_8, URIC, false);
	}

	public static String encodeUric(String content, Charset charset) {
		return urlEncode(content, charset, URIC, false);
	}

	public static String encodePath(String content) {
		return urlEncode(content, StandardCharsets.UTF_8, PATH_SPECIAL, false);
	}

	public static String encodePath(String content, Charset charset) {
		return urlEncode(content, charset, PATH_SPECIAL, false);
	}

	public static String format(Iterable<? extends Pair<String, String>> parameters) {
		return format(parameters, StandardCharsets.UTF_8, QP_SEP_A);
	}

	public static String format(Iterable<? extends Pair<String, String>> parameters, Charset charset) {
		return format(parameters, charset, QP_SEP_A);
	}

	public static String format(Iterable<? extends Pair<String, String>> parameters, char parameterSeparator) {
		return format(parameters, StandardCharsets.UTF_8, parameterSeparator);
	}

	public static String format(Iterable<? extends Pair<String, String>> parameters, Charset charset, char parameterSeparator) {
		StringBuilder result = new StringBuilder();
		for (Pair<String, String> parameter : parameters) {
			String encodedName = encodeFormFields(parameter.getKey(), charset);
			String encodedValue = encodeFormFields(parameter.getValue(), charset);
			if (result.length() > 0) {
				result.append(parameterSeparator);
			}
			result.append(encodedName);
			if (encodedValue != null) {
				result.append(NAME_VALUE_SEPARATOR);
				result.append(encodedValue);
			}
		}
		return result.toString();
	}

	public static String formatSegments(String... segments) {
		return formatSegments(Arrays.asList(segments), StandardCharsets.UTF_8);
	}

	public static String formatSegments(Iterable<String> segments) {
		return formatSegments(segments, StandardCharsets.UTF_8);
	}

	public static String formatSegments(Iterable<String> segments, Charset charset) {
		StringBuilder result = new StringBuilder();
		for (String segment : segments) {
			result.append(PATH_SEPARATOR).append(urlEncode(segment, charset, PATH_SAFE, false));
		}
		return result.toString();
	}

	public static List<Pair<String, String>> parseQuery(CharSequence s) {
		return parseQuery(s, StandardCharsets.UTF_8);
	}

	public static List<Pair<String, String>> parseQuery(CharSequence s, char... separators) {
		return parseQuery(s, StandardCharsets.UTF_8, separators);
	}

	public static List<Pair<String, String>> parseQuery(CharSequence s, Charset charset) {
		return parseQuery(s, charset, QP_SEP_A, QP_SEP_S);
	}

	public static List<Pair<String, String>> parseQuery(CharSequence s, Charset charset, char... separators) {
		final BitSet delimSet = new BitSet();
		for (final char separator : separators) {
			delimSet.set(separator);
		}
		return parseQuery(s, charset, delimSet);
	}

	@SuppressWarnings("DuplicatedCode")
	private static List<Pair<String, String>> parseQuery(CharSequence s, Charset charset, BitSet separators) {
		charset = charset != null ? charset : StandardCharsets.UTF_8;
		if (s == null || s.length() == 0) {
			return Collections.emptyList();
		}
		int start = 0;
		int end = s.length() - 1;
		for (int i = start; i < end; i++) {
			if (!separators.get(s.charAt(i))) {
				start = i;
				break;
			}
		}
		for (int i = end; i >= start; i--) {
			if (!separators.get(s.charAt(i))) {
				end = i;
				break;
			}
		}
		if (start == end) {
			return Collections.emptyList();
		}
		List<Pair<String, String>> list = new ArrayList<>();
		StringBuilder buf = new StringBuilder();
		int equalsIndex = -1;
		for (int i = start; i <= end; i++) {
			char ch = s.charAt(i);
			if (separators.get(ch)) {
				if (buf.length() > 0) {
					if (equalsIndex < 0) {
						list.add(Pair.of(decodeFormFields(buf.toString(), charset), ""));
					} else {
						list.add(Pair.of(decodeFormFields(buf.substring(0, equalsIndex), charset),
							equalsIndex >= buf.length() - 1 ? "" : decodeFormFields(buf.substring(equalsIndex + 1), charset)));
					}
					buf.setLength(0);
					equalsIndex = -1;
				}
			} else {
				if (equalsIndex < 0) {
					if (ch == '=') {
						equalsIndex = buf.length();
					}
				}
				buf.append(ch);
			}
		}

		if (buf.length() > 0) {
			if (equalsIndex < 0) {
				list.add(Pair.of(decodeFormFields(buf.toString(), charset), ""));
			} else {
				list.add(Pair.of(decodeFormFields(buf.substring(0, equalsIndex), charset),
					equalsIndex >= buf.length() - 1 ? "" : decodeFormFields(buf.substring(equalsIndex + 1), charset)));
			}
		}
		return list;
	}


	public static List<String> parsePathSegments(CharSequence s) {
		return parsePathSegments(s, StandardCharsets.UTF_8, PATH_SEPARATORS);
	}

	public static List<String> parsePathSegments(CharSequence s, Charset charset) {
		return parsePathSegments(s, charset, PATH_SEPARATORS);
	}

	@SuppressWarnings("DuplicatedCode")
	private static List<String> parsePathSegments(CharSequence s, Charset charset, BitSet separators) {
		charset = charset != null ? charset : StandardCharsets.UTF_8;
		if (s == null || s.length() == 0) {
			return Collections.emptyList();
		}
		int start = 0;
		int end = s.length() - 1;
		for (int i = start; i < end; i++) {
			if (!separators.get(s.charAt(i))) {
				start = i;
				break;
			}
		}
		for (int i = end; i >= start; i--) {
			if (!separators.get(s.charAt(i))) {
				end = i;
				break;
			}
		}
		if (start == end) {
			return Collections.emptyList();
		}
		List<String> list = new ArrayList<>();
		StringBuilder buf = new StringBuilder();
		for (int i = start; i <= end; i++) {
			char ch = s.charAt(i);
			if (separators.get(ch)) {
				if (buf.length() > 0) {
					list.add(urlDecode(buf.toString(), charset, false));
					buf.setLength(0);
				}
			} else {
				buf.append(ch);
			}
		}
		if (buf.length() > 0) {
			list.add(urlDecode(buf.toString(), charset, false));
		}
		return list;
	}

}
