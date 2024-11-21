package io.polaris.core.lang;

import java.math.BigInteger;

import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Nov 21, 2024
 */
public class BigIntegers {

	// region convert

	/**
	 * 创建{@link BigInteger}，支持16进制、10进制和8进制，如果传入空白串返回null<br>
	 * from Apache Common Lang
	 *
	 * @param str 数字字符串
	 * @return {@link BigInteger}
	 */
	public static BigInteger newBigInteger(String str) {
		str = Strings.trimToNull(str);
		if (null == str) {
			return null;
		}

		int pos = 0; // 数字字符串位置
		int radix = 10;
		boolean negate = false; // 负数与否
		if (str.startsWith("-")) {
			negate = true;
			pos = 1;
		}
		if (str.startsWith("0x", pos) || str.startsWith("0X", pos)) {
			// hex
			radix = 16;
			pos += 2;
		} else if (str.startsWith("#", pos)) {
			// alternative hex (allowed by Long/Integer)
			radix = 16;
			pos++;
		} else if (str.startsWith("0", pos) && str.length() > pos + 1) {
			// octal; so long as there are additional digits
			radix = 8;
			pos++;
		} // default is to treat as decimal

		if (pos > 0) {
			str = str.substring(pos);
		}
		final BigInteger value = new BigInteger(str, radix);
		return negate ? value.negate() : value;
	}

	/**
	 * 数字转{@link BigInteger}<br>
	 * null转换为0
	 *
	 * @param number 数字
	 * @return {@link BigInteger}
	 */
	public static BigInteger toBigInteger(Number number) {
		if (null == number) {
			return BigInteger.ZERO;
		}

		if (number instanceof BigInteger) {
			return (BigInteger) number;
		} else if (number instanceof Long) {
			return BigInteger.valueOf((Long) number);
		}

		return toBigInteger(number.longValue());
	}

	/**
	 * 数字转{@link BigInteger}<br>
	 * null或""或空白符转换为0
	 *
	 * @param number 数字字符串
	 * @return {@link BigInteger}
	 */
	public static BigInteger toBigInteger(String number) {
		return Strings.isBlank(number) ? BigInteger.ZERO : new BigInteger(number);
	}

	/**
	 * 以无符号字节数组的形式返回传入值。
	 *
	 * @param value 需要转换的值
	 * @return 无符号bytes
	 */
	public static byte[] toUnsignedByteArray(BigInteger value) {
		byte[] bytes = value.toByteArray();

		if (bytes[0] == 0) {
			byte[] tmp = new byte[bytes.length - 1];
			System.arraycopy(bytes, 1, tmp, 0, tmp.length);

			return tmp;
		}

		return bytes;
	}

	/**
	 * 以无符号字节数组的形式返回传入值。
	 *
	 * @param length bytes长度
	 * @param value  需要转换的值
	 * @return 无符号bytes
	 */
	public static byte[] toUnsignedByteArray(int length, BigInteger value) {
		byte[] bytes = value.toByteArray();
		if (bytes.length == length) {
			return bytes;
		}

		int start = bytes[0] == 0 ? 1 : 0;
		int count = bytes.length - start;

		if (count > length) {
			throw new IllegalArgumentException("standard length exceeded for value");
		}

		byte[] tmp = new byte[length];
		System.arraycopy(bytes, start, tmp, tmp.length - count, count);
		return tmp;
	}

	/**
	 * 无符号bytes转{@link BigInteger}
	 *
	 * @param buf buf 无符号bytes
	 * @return {@link BigInteger}
	 */
	public static BigInteger fromUnsignedByteArray(byte[] buf) {
		return new BigInteger(1, buf);
	}

	/**
	 * 无符号bytes转{@link BigInteger}
	 *
	 * @param buf    无符号bytes
	 * @param off    起始位置
	 * @param length 长度
	 * @return {@link BigInteger}
	 */
	public static BigInteger fromUnsignedByteArray(byte[] buf, int off, int length) {
		byte[] mag = buf;
		if (off != 0 || length != buf.length) {
			mag = new byte[length];
			System.arraycopy(buf, off, mag, 0, length);
		}
		return new BigInteger(1, mag);
	}

	// endregion

}
