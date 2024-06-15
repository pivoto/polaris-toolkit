package io.polaris.core.codec;

import java.security.AccessController;
import java.security.PrivilegedAction;

import io.polaris.core.consts.SystemKeys;

/**
 * @author Qt
 * @since 1.8
 */
class CodecStrings {
	static String LINE_SEPARATOR;

	static {
		try {
			LINE_SEPARATOR = AccessController.doPrivileged(new PrivilegedAction<String>() {
				public String run() {
					return System.getProperty(SystemKeys.LINE_SEPARATOR);
				}
			});
		} catch (Exception e) {
			try {
				LINE_SEPARATOR = String.format("%n");
			} catch (Exception ef) {
				LINE_SEPARATOR = "\n";   // we're desperate use this...
			}
		}
	}

	public static byte[] toByteArray(char[] chars) {
		byte[] bytes = new byte[chars.length];
		for (int i = 0; i != bytes.length; i++) {
			bytes[i] = (byte) chars[i];
		}
		return bytes;
	}


	public static byte[] toByteArray(String string) {
		byte[] bytes = new byte[string.length()];
		for (int i = 0; i != bytes.length; i++) {
			char ch = string.charAt(i);
			bytes[i] = (byte) ch;
		}
		return bytes;
	}

	public static int toByteArray(String s, byte[] buf, int off) {
		int count = s.length();
		for (int i = 0; i < count; ++i) {
			char c = s.charAt(i);
			buf[off + i] = (byte) c;
		}
		return count;
	}

	public static String fromByteArray(byte[] bytes) {
		return new String(asCharArray(bytes));
	}

	public static char[] asCharArray(byte[] bytes) {
		char[] chars = new char[bytes.length];
		for (int i = 0; i != chars.length; i++) {
			chars[i] = (char) (bytes[i] & 0xff);
		}
		return chars;
	}


}
