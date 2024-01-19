package io.polaris.core.crypto.asymmetric;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.IDecryptor;
import io.polaris.core.crypto.IEncryptor;

/**
 * @author Qt
 * @since 1.8
 */
public class Asymmetric {
	private final String algorithm;
	private final PublicKey publicKey;
	private final PrivateKey privateKey;
	private IEncryptor encryptor;
	private IDecryptor decryptor;

	public Asymmetric(AsymmetricAlgorithm algorithm) {
		this(algorithm.code(), CryptoKeys.generateKeyPair(algorithm.code()));
	}

	public Asymmetric(AsymmetricAlgorithm algorithm, KeyPair keyPair) {
		this(algorithm.code(), keyPair.getPrivate(), keyPair.getPublic());
	}

	public Asymmetric(AsymmetricAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this(algorithm.code(), privateKey, publicKey);
	}

	public Asymmetric(String algorithm) {
		this(algorithm, CryptoKeys.generateKeyPair(algorithm));
	}

	public Asymmetric(String algorithm, KeyPair keyPair) {
		this(algorithm, keyPair.getPrivate(), keyPair.getPublic());
	}

	public Asymmetric(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public IDecryptor getDecryptor() {
		if (decryptor == null) {
			decryptor = Ciphers.getDecryptor(algorithm, privateKey);
		}
		return decryptor;
	}

	public IEncryptor getEncryptor() {
		if (encryptor == null) {
			encryptor = Ciphers.getEncryptor(algorithm, publicKey);
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
