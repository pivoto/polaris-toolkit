package io.polaris.core.crypto.asymmetric;

import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.IDecryptor;
import io.polaris.core.crypto.IEncryptor;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

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

	public Asymmetric(AsymmetricAlgorithm algorithm) throws GeneralSecurityException {
		this(algorithm.code(), CryptoKeys.generateKeyPair(algorithm.code()));
	}

	public Asymmetric(AsymmetricAlgorithm algorithm, KeyPair keyPair) {
		this(algorithm.code(), keyPair.getPrivate(), keyPair.getPublic());
	}

	public Asymmetric(AsymmetricAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this(algorithm.code(), privateKey, publicKey);
	}

	public Asymmetric(String algorithm) throws GeneralSecurityException {
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

	public IDecryptor getDecryptor() throws GeneralSecurityException {
		if (decryptor == null) {
			decryptor = Ciphers.getDecryptor(algorithm, privateKey);
		}
		return decryptor;
	}

	public IEncryptor getEncryptor() throws GeneralSecurityException {
		if (encryptor == null) {
			encryptor = Ciphers.getEncryptor(algorithm, publicKey);
		}
		return encryptor;
	}


	public Asymmetric encryptUpdate(byte[] data) throws GeneralSecurityException {
		getEncryptor().update(data);
		return this;
	}

	public byte[] encrypt(byte[] data) throws GeneralSecurityException {
		return getEncryptor().encrypt(data);
	}

	public Asymmetric decryptUpdate(byte[] data) throws GeneralSecurityException {
		getDecryptor().update(data);
		return this;
	}

	public byte[] decrypt(byte[] data) throws GeneralSecurityException {
		return getDecryptor().decrypt(data);
	}

}
