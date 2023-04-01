package io.polaris.core.guid;

/**
 * @author Qt
 * @since 1.8
 */
public class Hex {

	private static final char[] DIGITS_UPPER = new char[36];

	static {
		for (int i = 0; i < 10; i++) {
			DIGITS_UPPER[i] = (char) ('0' + i);
		}
		for (int i = 0; i < 26; i++) {
			DIGITS_UPPER[i + 10] = (char) ('A' + i);
		}
	}

	private static String to(long num, int bits, int width) {
		int len = (bits / width + (bits % width > 0 ? 1 : 0));
		long mask = (-1 << width) ^ -1;
		char[] chs = new char[len];
		for (int i = len - 1; i >= 0; i--) {
			chs[i] = DIGITS_UPPER[(int) (num & mask)];
			num >>>= width;
		}
		return new String(chs);
	}

	private static long from(String text, int bits, int width) {
		int len = (bits / width + (bits % width > 0 ? 1 : 0));
		long mask = (-1 << width) ^ -1;
		long num = 0;
		for (int i = Integer.max(text.length() - len, 0); i < text.length(); i++) {
			char c = text.charAt(i);
			int x = 0;
			if (c >= '0' && c <= '9') {
				x = c - '0';
			} else if (c >= 'A' && c <= 'Z') {
				x = c - 'A' + 10;
			} else if (c >= 'a' && c <= 'z') {
				x = c - 'a' + 10;
			}
			num <<= width;
			num |= (long) (x & mask);
		}
		return num;
	}

	public static String to32Radix(long num, int bits) {
		return to(num, bits, 5);
	}

	public static long from32Radix(String text, int bits) {
		return from(text, bits, 5);
	}

	public static String toHex(long num, int bits) {
		return to(num, bits, 4);
	}

	public static long fromHex(String text, int bits) {
		return from(text, bits, 4);
	}

	public static String toBin(long num, int bits) {
		return to(num, bits, 1);
	}

	public static long fromBin(String text, int bits) {
		return from(text, bits, 1);
	}

	public static String toOct(long num, int bits) {
		return to(num, bits, 3);
	}

	public static long fromOct(String text, int bits) {
		return from(text, bits, 3);
	}

}
