package io.polaris.core.crypto.asymmetric;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.CryptoRuntimeException;

/**
 * @author Qt
 * @since 1.8
 */
public class Signatures {

	public static Signature getSignature(String algorithm) {
		return CryptoKeys.getSignature(algorithm);
	}

	public static Signature getSignature(String provider, String algorithm) {
		return CryptoKeys.getSignature(provider, algorithm);
	}

	public static Signature getSignature(Provider provider, String algorithm) {
		return CryptoKeys.getSignature(provider, algorithm);
	}

	public static Signature getInitializedSignature(String algorithm, PrivateKey key) {
		Signature signature = getSignature(algorithm);
		try {
			signature.initSign(key);
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
		return signature;
	}

	public static Signature getInitializedSignature(String provider, String algorithm, PrivateKey key) {
		Signature signature = getSignature(provider, algorithm);
		try {
			signature.initSign(key);
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
		return signature;
	}

	public static Signature getInitializedSignature(Provider provider, String algorithm, PrivateKey key) {
		Signature signature = getSignature(provider, algorithm);
		try {
			signature.initSign(key);
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
		return signature;
	}

	public static Signature getInitializedSignature(String algorithm, PublicKey key) {
		Signature signature = getSignature(algorithm);
		try {
			signature.initVerify(key);
			return signature;
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Signature getInitializedSignature(String provider, String algorithm, PublicKey key) {
		Signature signature = getSignature(provider, algorithm);
		try {
			signature.initVerify(key);
			return signature;
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Signature getInitializedSignature(Provider provider, String algorithm, PublicKey key) {
		Signature signature = getSignature(provider, algorithm);
		try {
			signature.initVerify(key);
			return signature;
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Signature doUpdate(Signature signature, byte[] data) {
		try {
			signature.update(data);
			return signature;
		} catch (SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static byte[] doSign(Signature signature, byte[] data) {
		try {
			signature.update(data);
			return signature.sign();
		} catch (SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static boolean doVerify(Signature signature, byte[] data, byte[] sign) {
		try {
			signature.update(data);
			return signature.verify(sign);
		} catch (SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static byte[] sign(String algorithm, PrivateKey key, byte[] data) {
		Signature signature = getSignature(algorithm);
		try {
			signature.initSign(key);
			signature.update(data);
			return signature.sign();
		} catch (InvalidKeyException | SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static byte[] sign(String provider, String algorithm, PrivateKey key, byte[] data) {
		Signature signature = getSignature(provider, algorithm);
		try {
			signature.initSign(key);
			signature.update(data);
			return signature.sign();
		} catch (InvalidKeyException | SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static byte[] sign(Provider provider, String algorithm, PrivateKey key, byte[] data) {
		Signature signature = getSignature(provider, algorithm);
		try {
			signature.initSign(key);
			signature.update(data);
			return signature.sign();
		} catch (InvalidKeyException | SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static boolean verify(String algorithm, PublicKey key, byte[] data, byte[] sign) {
		Signature signature = getSignature(algorithm);
		try {
			signature.initVerify(key);
			signature.update(data);
			return signature.verify(sign);
		} catch (InvalidKeyException | SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static boolean verify(String provider, String algorithm, PublicKey key, byte[] data, byte[] sign) {
		Signature signature = getSignature(provider, algorithm);
		try {
			signature.initVerify(key);
			signature.update(data);
			return signature.verify(sign);
		} catch (InvalidKeyException | SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static boolean verify(Provider provider, String algorithm, PublicKey key, byte[] data, byte[] sign) {
		Signature signature = getSignature(provider, algorithm);
		try {
			signature.initVerify(key);
			signature.update(data);
			return signature.verify(sign);
		} catch (InvalidKeyException | SignatureException e) {
			throw new CryptoRuntimeException(e);
		}
	}
}
