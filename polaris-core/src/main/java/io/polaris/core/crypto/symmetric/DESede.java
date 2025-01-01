package io.polaris.core.crypto.symmetric;

import java.util.Arrays;

import io.polaris.core.crypto.Ciphers;

/**
 * @author Qt
 * @since 1.8
 */
public class DESede {
	public static byte[] paddingKey(byte[] key) {
		return paddingKey(key, (byte) key.length);
	}

	public static byte[] paddingKey(byte[] key, byte pad) {
		// DESede Key length： 192 bits. javax.crypto.spec.DESedeKeySpec.DES_KEY_LEN
		if (key.length >= 24) {
			return key;
		}
		int newLength = 24;
		byte[] newKey = new byte[newLength];
		System.arraycopy(key, 0, newKey, 0, key.length);
		Arrays.fill(newKey, key.length, newLength, pad);
		return newKey;
	}

	public static byte[] encrypt(byte[] data, byte[] key) {
		return Ciphers.encrypt(SymmetricAlgorithm.DESede.code(), key, data);
	}

	public static byte[] encryptByKeySeed(byte[] data, byte[] key) {
		return Ciphers.encryptByKeySeed(SymmetricAlgorithm.DESede.code(), key, data);
	}

	public static byte[] decrypt(byte[] data, byte[] key) {
		return Ciphers.decrypt(SymmetricAlgorithm.DESede.code(), key, data);
	}

	public static byte[] decryptByKeySeed(byte[] data, byte[] key) {
		return Ciphers.decryptByKeySeed(SymmetricAlgorithm.DESede.code(), key, data);
	}
}
