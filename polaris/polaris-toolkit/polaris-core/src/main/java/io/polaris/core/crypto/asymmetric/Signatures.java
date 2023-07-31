package io.polaris.core.crypto.asymmetric;

import io.polaris.core.crypto.CryptoKeys;

import java.security.*;

/**
 * @author Qt
 * @since 1.8
 */
public class Signatures {

	public static Signature getSignature(String algorithm) throws NoSuchAlgorithmException {
		return CryptoKeys.getSignature(algorithm);
	}

	public static Signature getInitializedSignature(String algorithm, PrivateKey key) throws GeneralSecurityException {
		Signature signature = getSignature(algorithm);
		signature.initSign(key);
		return signature;
	}

	public static Signature getInitializedSignature(String algorithm, PublicKey key) throws GeneralSecurityException {
		Signature signature = getSignature(algorithm);
		signature.initVerify(key);
		return signature;
	}

	public static Signature doUpdate(Signature signature, byte[] data) throws GeneralSecurityException {
		signature.update(data);
		return signature;
	}

	public static byte[] doSign(Signature signature, byte[] data) throws GeneralSecurityException {
		signature.update(data);
		return signature.sign();
	}

	public static boolean doVerify(Signature signature, byte[] data, byte[] sign) throws GeneralSecurityException {
		signature.update(data);
		return signature.verify(sign);
	}

	public static byte[] sign(String algorithm, PrivateKey key, byte[] data) throws GeneralSecurityException {
		Signature signature = getSignature(algorithm);
		signature.initSign(key);
		signature.update(data);
		return signature.sign();
	}

	public static boolean verify(String algorithm, PublicKey key, byte[] data, byte[] sign)
		throws GeneralSecurityException {
		Signature signature = getSignature(algorithm);
		signature.initVerify(key);
		signature.update(data);
		return signature.verify(sign);
	}
}
