package io.polaris.core.crypto.asymmetric;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.CryptoRuntimeException;

/**
 * @author Qt
 * @since 1.8
 */
public class Sign {
	private final String provider;
	private final String algorithm;
	private final PublicKey publicKey;
	private final PrivateKey privateKey;

	private Signature signSignature;
	private Signature verifySignature;

	public Sign(String provider, String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		this.provider = provider;
		this.algorithm = algorithm;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	public static Sign of(String provider, String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		return new Sign(provider, algorithm, privateKey, publicKey);
	}

	public static Sign of(String provider, String algorithm, KeyPair keyPair) {
		return of(provider, algorithm, keyPair.getPrivate(), keyPair.getPublic());
	}

	public static Sign of(String provider, String algorithm) {
		return of(provider, algorithm, CryptoKeys.generateKeyPair(provider, algorithm));
	}

	public static Sign of(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
		return of(null, algorithm, privateKey, publicKey);
	}

	public static Sign of(String algorithm, KeyPair keyPair) {
		return of(null, algorithm, keyPair.getPrivate(), keyPair.getPublic());
	}

	public static Sign of(String algorithm) {
		return of(null, algorithm);
	}

	public static Sign of(String provider, SignAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
		return of(provider, algorithm.code(), privateKey, publicKey);
	}

	public static Sign of(String provider, SignAlgorithm algorithm, KeyPair keyPair) {
		return of(provider, algorithm, keyPair.getPrivate(), keyPair.getPublic());
	}

	public static Sign of(String provider, SignAlgorithm algorithm) {
		return of(provider, algorithm, CryptoKeys.generateKeyPair(provider, algorithm.code()));
	}

	public static Sign of(SignAlgorithm algorithm, PrivateKey privateKey, PublicKey publicKey) {
		return of(null, algorithm, privateKey, publicKey);
	}

	public static Sign of(SignAlgorithm algorithm, KeyPair keyPair) {
		return of(null, algorithm, keyPair.getPrivate(), keyPair.getPublic());
	}

	public static Sign of(SignAlgorithm algorithm) {
		return of(null, algorithm);
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
