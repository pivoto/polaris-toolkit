package io.polaris.core.crypto.symmetric;

import io.polaris.core.crypto.Ciphers;

/**
 * @author Qt
 * @since 1.8
 */
public class DESede {

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
