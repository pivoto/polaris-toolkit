package io.polaris.core.crypto.symmetric;

import java.util.Arrays;

import io.polaris.core.crypto.Ciphers;

/**
 * @author Qt
 * @since 1.8
 */
public class AES {

	public static byte[] paddingKey(byte[] key) {
		return paddingKey(key, (byte) key.length);
	}

	/** 填充字节以使密钥数组符合AES的长度要求 */
	public static byte[] paddingKey(byte[] key, byte pad) {
		// AES Key length： 128/192/256 bits.
		if (key.length == 16 || key.length == 24 || key.length == 32) {
			return key;
		}
		int newLength = 32;
		if (key.length < 16) {
			newLength = 16;
		} else if (key.length < 24) {
			newLength = 24;
		}
		byte[] newKey = new byte[newLength];
		System.arraycopy(key, 0, newKey, 0, Integer.min(key.length, newLength));
		if (newLength > key.length) {
			Arrays.fill(newKey, key.length, newLength, pad);
		}
		return newKey;
	}

	public static byte[] encrypt(byte[] data, byte[] key) {
		return Ciphers.encrypt(SymmetricAlgorithm.AES.code(), key, data);
	}

	public static byte[] encryptByKeySeed(byte[] data, byte[] key) {
		return Ciphers.encryptByKeySeed(SymmetricAlgorithm.AES.code(), key, data);
	}

	public static byte[] decrypt(byte[] data, byte[] key) {
		return Ciphers.decrypt(SymmetricAlgorithm.AES.code(), key, data);
	}

	public static byte[] decryptByKeySeed(byte[] data, byte[] key) {
		return Ciphers.decryptByKeySeed(SymmetricAlgorithm.AES.code(), key, data);
	}
}
