package io.polaris.core.string;

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

	private static String format(long num, int bits, int width) {
		int len = (bits / width + (bits % width > 0 ? 1 : 0));
		long mask = (-1 << width) ^ -1;
		char[] chs = new char[len];
		for (int i = len - 1; i >= 0; i--) {
			chs[i] = DIGITS_UPPER[(int) (num & mask)];
			num >>>= width;
		}
		return new String(chs);
	}

	private static long parse(String text, int bits, int width) {
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

	public static String format32Radix(long num, int bits) {
		return format(num, bits, 5);
	}

	public static String format32Radix(long num) {
		return format32Radix(num, 64);
	}

	public static String format32Radix(int num) {
		return format32Radix(num, 32);
	}

	public static String format32Radix(short num) {
		return format32Radix(num, 16);
	}

	public static String format32Radix(byte num) {
		return format32Radix(num, 8);
	}

	public static long parse32Radix(String text, int bits) {
		return parse(text, bits, 5);
	}

	public static long parse32Radix(String text) {
		return parse32Radix(text, 64);
	}

	public static int parse32RadixAsInt(String text) {
		return (int) parse32Radix(text, 32);
	}

	public static short parse32RadixAsShort(String text) {
		return (short) parse32Radix(text, 16);
	}

	public static byte parse32RadixAsByte(String text) {
		return (byte) parse32Radix(text, 8);
	}

	public static String formatHex(long num, int bits) {
		return format(num, bits, 4);
	}

	public static String formatHex(long num) {
		return formatHex(num, 64);
	}

	public static String formatHex(int num) {
		return formatHex(num, 32);
	}

	public static String formatHex(short num) {
		return formatHex(num, 16);
	}

	public static String formatHex(byte num) {
		return formatHex(num, 8);
	}

	public static long parseHex(String text, int bits) {
		return parse(text, bits, 4);
	}

	public static long parseHex(String text) {
		return parseHex(text, 64);
	}

	public static int parseHexAsInt(String text) {
		return (int) parseHex(text, 32);
	}

	public static short parseHexAsShort(String text) {
		return (short) parseHex(text, 16);
	}

	public static byte parseHexAsByte(String text) {
		return (byte) parseHex(text, 8);
	}

	public static String formatBin(long num, int bits) {
		return format(num, bits, 1);
	}

	public static String formatBin(long num) {
		return formatBin(num, 64);
	}

	public static String formatBin(int num) {
		return formatBin(num, 32);
	}

	public static String formatBin(short num) {
		return formatBin(num, 16);
	}

	public static String formatBin(byte num) {
		return formatBin(num, 8);
	}

	public static long parseBin(String text, int bits) {
		return parse(text, bits, 1);
	}

	public static long parseBin(String text) {
		return parseBin(text, 64);
	}

	public static int parseBinAsInt(String text) {
		return (int) parseBin(text, 32);
	}

	public static short parseBinAsShort(String text) {
		return (short) parseBin(text, 16);
	}

	public static byte parseBinAsByte(String text) {
		return (byte) parseBin(text, 8);
	}

	public static String formatOct(long num, int bits) {
		return format(num, bits, 3);
	}

	public static String formatOct(long num) {
		return formatOct(num, 64);
	}

	public static String formatOct(int num) {
		return formatOct(num, 32);
	}

	public static String formatOct(short num) {
		return formatOct(num, 16);
	}

	public static String formatOct(byte num) {
		return formatOct(num, 8);
	}

	public static long parseOct(String text, int bits) {
		return parse(text, bits, 3);
	}

	public static long parseOct(String text) {
		return parseOct(text, 64);
	}

	public static int parseOctAsInt(String text) {
		return (int) parseOct(text, 32);
	}

	public static short parseOctAsShort(String text) {
		return (short) parseOct(text, 16);
	}

	public static byte parseOctAsByte(String text) {
		return (byte) parseOct(text, 8);
	}

	public static String formatBytes(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(formatHex(b, 8));
		}
		return sb.toString();
	}

	public static byte[] parseBytes(String text) {
		int len = text.length();
		int size = len / 2 + len % 2;
		byte[] bytes = new byte[size];
		for (int i = len, j = size - 1; i > 0; i = i - 2, j--) {
			String str = text.substring(Integer.max(0, i - 2), i);
			bytes[j] = (byte) parseHex(str, 8);
		}
		return bytes;
	}
}
