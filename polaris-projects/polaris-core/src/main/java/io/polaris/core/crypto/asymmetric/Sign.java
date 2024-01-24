package io.polaris.core.crypto.asymmetric;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.err.CryptoRuntimeException;

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

	public Sign(SignAlgorithm algorithm) {
		this(algorithm.code(), CryptoKeys.generateKeyPair(algorithm.code()));
	}

	public Sign(SignAlgorithm algorithm, KeyPair keyPair) {
		this(algorithm.code(), keyPair.getPrivate(), keyPair.getPublic());
	}

	public Sign(SignAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this(algorithm.code(), privateKey, publicKey);
	}

	public Sign(String algorithm) {
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

	public Sign signUpdate(byte[] data) {
		try {
			getSignSignature().update(data);
			return this;
		} catch (SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public byte[] sign(byte[] data) {
		return signUpdate(data).sign();
	}

	public byte[] sign() {
		try {
			return getSignSignature().sign();
		} catch (SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public Sign verifyUpdate(byte[] data) {
		try {
			getVerifySignature().update(data);
			return this;
		} catch (SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public boolean verify(byte[] data, byte[] signature) {
		return verifyUpdate(data).verify(signature);
	}

	public boolean verify(byte[] signature) {
		try {
			return getVerifySignature().verify(signature);
		} catch (SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}


	public Signature getSignSignature() {
		if (signSignature == null) {
			signSignature = Signatures.getInitializedSignature(algorithm, privateKey);
		}
		return signSignature;
	}

	public Signature getVerifySignature() {
		if (verifySignature == null) {
			verifySignature = Signatures.getInitializedSignature(algorithm, publicKey);
		}
		return verifySignature;
	}
}
