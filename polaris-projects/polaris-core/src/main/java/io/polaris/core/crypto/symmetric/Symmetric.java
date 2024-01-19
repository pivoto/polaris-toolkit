package io.polaris.core.crypto.symmetric;

import java.security.Key;

import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.IDecryptor;
import io.polaris.core.crypto.IEncryptor;

/**
 * @author Qt
 * @since 1.8
 */
public class Symmetric {

	private final String algorithm;
	private final Key key;

	private IEncryptor encryptor;
	private IDecryptor decryptor;

	public Symmetric(String algorithm, Key key) {
		this.algorithm = algorithm;
		this.key = key;
	}

	public Symmetric(SymmetricAlgorithm algorithm, Key key) {
		this.algorithm = algorithm.code();
		this.key = key;
	}

	public Symmetric(String algorithm) {
		this.algorithm = algorithm;
		this.key = CryptoKeys.generateKey(this.algorithm);
	}

	public Symmetric(SymmetricAlgorithm algorithm) {
		this.algorithm = algorithm.code();
		this.key = CryptoKeys.generateKey(this.algorithm);
	}

	public IDecryptor getDecryptor() {
		if (decryptor == null) {
			decryptor = Ciphers.getDecryptor(algorithm, key);
		}
		return decryptor;
	}

	public IEncryptor getEncryptor() {
		if (encryptor == null) {
			encryptor = Ciphers.getEncryptor(algorithm, key);
		}
		return encryptor;
	}


	public Symmetric encryptUpdate(byte[] data) {
		getEncryptor().update(data);
		return this;
	}

	public byte[] encrypt(byte[] data) {
		return getEncryptor().encrypt(data);
	}

	public Symmetric decryptUpdate(byte[] data) {
		getDecryptor().update(data);
		return this;
	}

	public byte[] decrypt(byte[] data) {
		return getDecryptor().decrypt(data);
	}
}
