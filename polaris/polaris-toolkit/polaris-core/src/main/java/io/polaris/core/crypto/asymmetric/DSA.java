package io.polaris.core.crypto.asymmetric;

import io.polaris.core.crypto.CryptoKeys;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Qt
 * @since 1.8
 */
public class DSA {

	public static KeyPair getKeyPair() throws Exception {
		return CryptoKeys.generateKeyPair(AsymmetricAlgorithm.DSA.code());
	}

	public static byte[] sign(PrivateKey key, byte[] data) throws GeneralSecurityException {
		return Signatures.sign(AsymmetricAlgorithm.DSA.code(), key, data);
	}

	public static boolean verify(PublicKey key, byte[] data, byte[] sign) throws GeneralSecurityException {
		return Signatures.verify(AsymmetricAlgorithm.DSA.code(), key, data, sign);
	}


}
