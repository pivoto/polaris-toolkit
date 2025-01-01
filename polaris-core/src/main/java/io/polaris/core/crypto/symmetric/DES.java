package io.polaris.core.crypto.symmetric;

import java.util.Arrays;

import io.polaris.core.crypto.Ciphers;

/**
 * @author Qt
 * @since 1.8
 */
public class DES {

	public static byte[] paddingKey(byte[] key) {
		return paddingKey(key, (byte) key.length);
	}

	public static byte[] paddingKey(byte[] key, byte pad) {
		// DES Key length： 64 bits. javax.crypto.spec.DESKeySpec.DES_KEY_LEN
		if (key.length >= 8) {
			return key;
		}
		int newLength = 8;
		byte[] newKey = new byte[newLength];
		System.arraycopy(key, 0, newKey, 0, key.length);
		Arrays.fill(newKey, key.length, newLength, pad);
		return newKey;
	}

	public static byte[] encrypt(byte[] data, byte[] key) {
		return Ciphers.encrypt(SymmetricAlgorithm.DES.code(), key, data);
	}

	public static byte[] encryptByKeySeed(byte[] data, byte[] key) {
		return Ciphers.encryptByKeySeed(SymmetricAlgorithm.DES.code(), key, data);
	}

	public static byte[] decrypt(byte[] data, byte[] key) {
		return Ciphers.decrypt(SymmetricAlgorithm.DES.code(), key, data);
	}

	public static byte[] decryptByKeySeed(byte[] data, byte[] key) {
		return Ciphers.decryptByKeySeed(SymmetricAlgorithm.DES.code(), key, data);
	}
}
