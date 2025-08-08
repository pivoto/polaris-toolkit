package io.polaris.core.crypto.symmetric;

import java.security.Key;

import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.Decryptor;
import io.polaris.core.crypto.IEncryptor;

/**
 * @author Qt
 * @since 1.8
 */
public class Symmetric {

	private final String provider;
	private final String algorithm;
	private final Key key;

	private IEncryptor encryptor;
	private Decryptor decryptor;

	public Symmetric(String provider, String algorithm, Key key) {
		this.provider = provider;
		this.algorithm = algorithm;
		if (key == null) {
			key = provider == null ? CryptoKeys.generateKey(algorithm) : CryptoKeys.generateKey(provider, algorithm);
		}
		this.key = key;
	}

	public static Symmetric of(String provider, String algorithm, Key key) {
		return new Symmetric(provider, algorithm, key);
	}

	public static Symmetric of(String provider, String algorithm) {
		return of(provider, algorithm, CryptoKeys.generateKey(provider, algorithm));
	}

	public static Symmetric of(String algorithm, Key key) {
		return of(null, algorithm, key);
	}

	public static Symmetric of(String algorithm) {
		return of(null, algorithm, CryptoKeys.generateKey(algorithm));
	}

	public static Symmetric of(String provider, SymmetricAlgorithm algorithm, Key key) {
		return new Symmetric(provider, algorithm.code(), key);
	}

	public static Symmetric of(String provider, SymmetricAlgorithm algorithm) {
		return of(provider, algorithm, CryptoKeys.generateKey(provider, algorithm.code()));
	}

	public static Symmetric of(SymmetricAlgorithm algorithm, Key key) {
		return of(null, algorithm.code(), key);
	}

	public static Symmetric of(SymmetricAlgorithm algorithm) {
		return of(null, algorithm, CryptoKeys.generateKey(algorithm.code()));
	}


	public Decryptor getDecryptor() {
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
