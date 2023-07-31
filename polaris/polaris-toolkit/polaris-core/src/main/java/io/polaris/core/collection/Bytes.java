package io.polaris.core.collection;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author Qt
 * @since 1.8
 */
public class Bytes {

	public static byte[] copy(byte[] array, int length) {
		return Arrays.copyOf(array, length);
	}

	public static byte[] copy(byte[] array, int from, int to) {
		return Arrays.copyOfRange(array, from, to);
	}

	public static byte[] concat(@Nonnull byte[] byteArray, byte[]... byteArrays) {
		if (byteArrays.length == 0) {
			return byteArray;
		}
		int length = 0;
		for (byte[] bytes : byteArrays) {
			length += bytes.length;
		}
		byte[] result = new byte[length];
		int pos = 0;
		for (byte[] bytes : byteArrays) {
			System.arraycopy(bytes, 0, result, pos, bytes.length);
			pos += bytes.length;
		}
		return result;
	}

	public static boolean contains(byte[] array, byte target) {
		for (byte value : array) {
			if (value == target) {
				return true;
			}
		}
		return false;
	}

	public static int indexOf(byte[] array, byte target) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == target) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(byte[] array, byte[] target) {
		if (target.length == 0) {
			return 0;
		}

		outer:
		for (int i = 0; i < array.length - target.length + 1; i++) {
			for (int j = 0; j < target.length; j++) {
				if (array[i + j] != target[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}

	public static int lastIndexOf(byte[] array, byte target) {
		for (int i = array.length - 1; i >= 0; i--) {
			if (array[i] == target) {
				return i;
			}
		}
		return -1;
	}

}
