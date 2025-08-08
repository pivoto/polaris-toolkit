package io.polaris.core.crypto.asymmetric;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.Decryptor;
import io.polaris.core.crypto.Encryptor;

/**
 * @author Qt
 * @since 1.8
 */
public class Asymmetric {
	private final String provider;
	private final String algorithm;
	private final PublicKey publicKey;
	private final PrivateKey privateKey;
	private Encryptor encryptor;
	private Decryptor decryptor;

	public Asymmetric(String provider, String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this.provider = provider;
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public static Asymmetric of(String provider, String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		return new Asymmetric(provider, algorithm, privateKey, publicKey);
	}

	public static Asymmetric of(String provider, String algorithm, KeyPair keyPair) {
		return of(provider, algorithm, keyPair.getPrivate(), keyPair.getPublic());
	}

	public static Asymmetric of(String provider, String algorithm) {
		return of(provider, algorithm, CryptoKeys.generateKeyPair(algorithm));
	}

	public static Asymmetric of(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		return of(null, algorithm, privateKey, publicKey);
	}

	public static Asymmetric of(String algorithm, KeyPair keyPair) {
		return of(null, algorithm, keyPair);
	}

	public static Asymmetric of(String algorithm) {
		return of(null, algorithm);
	}


	public static Asymmetric of(String provider, AsymmetricAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
		return of(provider, algorithm.code(), privateKey, publicKey);
	}

	public static Asymmetric of(String provider, AsymmetricAlgorithm algorithm, KeyPair keyPair) {
		return of(provider, algorithm.code(), keyPair.getPrivate(), keyPair.getPublic());
	}

	public static Asymmetric of(String provider, AsymmetricAlgorithm algorithm) {
		return of(provider, algorithm.code(), CryptoKeys.generateKeyPair(algorithm.code()));
	}

	public static Asymmetric of(AsymmetricAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
		return of(null, algorithm.code(), privateKey, publicKey);
	}

	public static Asymmetric of(AsymmetricAlgorithm algorithm, KeyPair keyPair) {
		return of(null, algorithm.code(), keyPair);
	}

	public static Asymmetric of(AsymmetricAlgorithm algorithm) {
		return of(null, algorithm.code());
	}


	public Decryptor getDecryptor() {
		if (decryptor == null) {
			decryptor = provider == null ? Ciphers.getDecryptor(algorithm, privateKey) : Ciphers.getDecryptor(provider, algorithm, privateKey);
		}
		return decryptor;
	}

	public Encryptor getEncryptor() {
		if (encryptor == null) {
			encryptor = provider == null ? Ciphers.getEncryptor(algorithm, publicKey) : Ciphers.getEncryptor(provider, algorithm, privateKey);
		}
		return encryptor;
	}


	public Asymmetric encryptUpdate(byte[] data) {
		getEncryptor().update(data);
		return this;
	}

	public byte[] encrypt(byte[] data) {
		return getEncryptor().encrypt(data);
	}

	public Asymmetric decryptUpdate(byte[] data) {
		getDecryptor().update(data);
		return this;
	}

	public byte[] decrypt(byte[] data) {
		return getDecryptor().decrypt(data);
	}

}
