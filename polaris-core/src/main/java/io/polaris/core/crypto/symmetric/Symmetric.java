package io.polaris.core.crypto.symmetric;

import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.IDecryptor;
import io.polaris.core.crypto.IEncryptor;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

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

	public Symmetric(String algorithm) throws NoSuchAlgorithmException {
		this.algorithm = algorithm;
		this.key = CryptoKeys.generateKey(this.algorithm);
	}

	public Symmetric(SymmetricAlgorithm algorithm) throws NoSuchAlgorithmException {
		this.algorithm = algorithm.code();
		this.key = CryptoKeys.generateKey(this.algorithm);
	}

	public IDecryptor getDecryptor() throws GeneralSecurityException {
		if (decryptor == null) {
			decryptor = Ciphers.getDecryptor(algorithm, key);
		}
		return decryptor;
	}

	public IEncryptor getEncryptor() throws GeneralSecurityException {
		if (encryptor == null) {
			encryptor = Ciphers.getEncryptor(algorithm, key);
		}
		return encryptor;
	}


	public Symmetric encryptUpdate(byte[] data) throws GeneralSecurityException {
		getEncryptor().update(data);
		return this;
	}

	public byte[] encrypt(byte[] data) throws GeneralSecurityException {
		return getEncryptor().encrypt(data);
	}

	public Symmetric decryptUpdate(byte[] data) throws GeneralSecurityException {
		getDecryptor().update(data);
		return this;
	}

	public byte[] decrypt(byte[] data) throws GeneralSecurityException {
		return getDecryptor().decrypt(data);
	}
}
