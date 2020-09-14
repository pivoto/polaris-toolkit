package io.polaris.core.crypto.asymmetric;

import io.polaris.core.crypto.CryptoKeys;

import java.security.*;

/**
 * @author Qt
 * @since 1.8
 */
public class Sign {
	private final String algorithm;
	private final PublicKey publicKey;
	private final PrivateKey privateKey;

	private Signature signSignature;
	private Signature verifySignature;

	public Sign(SignAlgorithm algorithm) throws GeneralSecurityException {
		this(algorithm.code(), CryptoKeys.generateKeyPair(algorithm.code()));
	}

	public Sign(SignAlgorithm algorithm, KeyPair keyPair) {
		this(algorithm.code(), keyPair.getPrivate(), keyPair.getPublic());
	}

	public Sign(SignAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this(algorithm.code(), privateKey, publicKey);
	}

	public Sign(String algorithm) throws GeneralSecurityException {
		this(algorithm, CryptoKeys.generateKeyPair(algorithm));
	}

	public Sign(String algorithm, KeyPair keyPair) {
		this(algorithm, keyPair.getPrivate(), keyPair.getPublic());
	}

	public Sign(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public Sign signUpdate(byte[] data) throws GeneralSecurityException {
		getSignSignature().update(data);
		return this;
	}

	public byte[] sign(byte[] data) throws GeneralSecurityException {
		return signUpdate(data).sign();
	}

	public byte[] sign() throws GeneralSecurityException {
		return getSignSignature().sign();
	}

	public Sign verifyUpdate(byte[] data) throws GeneralSecurityException {
		getVerifySignature().update(data);
		return this;
	}

	public boolean verify(byte[] data, byte[] signature) throws GeneralSecurityException {
		return verifyUpdate(data).verify(signature);
	}

	public boolean verify(byte[] signature) throws GeneralSecurityException {
		return getVerifySignature().verify(signature);
	}


	public Signature getSignSignature() throws GeneralSecurityException {
		if (signSignature == null) {
			signSignature = Signatures.getInitializedSignature(algorithm, privateKey);
		}
		return signSignature;
	}

	public Signature getVerifySignature() throws GeneralSecurityException {
		if (verifySignature == null) {
			verifySignature = Signatures.getInitializedSignature(algorithm, publicKey);
		}
		return verifySignature;
	}
}
