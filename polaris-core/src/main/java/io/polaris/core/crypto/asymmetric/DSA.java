package io.polaris.core.crypto.asymmetric;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;

import io.polaris.core.crypto.CryptoKeys;

/**
 * @author Qt
 * @since 1.8
 */
public class DSA {

	public static KeyPair getKeyPair() {
		return CryptoKeys.generateKeyPair(AsymmetricAlgorithm.DSA.code());
	}

	public static KeyPair getKeyPair(String provider) {
		return CryptoKeys.generateKeyPair(provider, AsymmetricAlgorithm.DSA.code());
	}

	public static KeyPair getKeyPair(Provider provider) {
		return CryptoKeys.generateKeyPair(provider, AsymmetricAlgorithm.DSA.code());
	}

	public static byte[] sign(PrivateKey key, byte[] data) {
		return Signatures.sign(AsymmetricAlgorithm.DSA.code(), key, data);
	}

	public static boolean verify(PublicKey key, byte[] data, byte[] sign) {
		return Signatures.verify(AsymmetricAlgorithm.DSA.code(), key, data, sign);
	}


}
