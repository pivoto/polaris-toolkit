package io.polaris.core.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.Provider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * @author Qt
 * @since 1.8
 */
public class Ciphers {

	public static Cipher getCipher(String algorithm) {
		return CryptoKeys.getCipher(algorithm);
	}

	public static Cipher getCipher(String provider, String algorithm) {
		return CryptoKeys.getCipher(provider, algorithm);
	}

	public static Cipher getCipher(Provider provider, String algorithm) {
		return CryptoKeys.getCipher(provider, algorithm);
	}

	private static Cipher init(Cipher cipher, int decryptMode, Key key) {
		try {
			cipher.init(decryptMode, key);
			return cipher;
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Cipher doInitEncryptMode(Cipher cipher, Key key) {
		return init(cipher, Cipher.ENCRYPT_MODE, key);
	}

	public static Cipher doInitDecryptMode(Cipher cipher, Key key) {
		return init(cipher, Cipher.DECRYPT_MODE, key);
	}


	public static Cipher doUpdate(Cipher cipher, byte[] data) {
		cipher.update(data);
		return cipher;
	}

	public static Cipher doUpdate(Cipher cipher, byte[] data, int offset, int len) {
		cipher.update(data, offset, len);
		return cipher;
	}

	public static byte[] doFinal(Cipher cipher, byte[] data) {
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static byte[] doFinal(Cipher cipher, byte[] data, int offset, int len) {
		try {
			return cipher.doFinal(data, offset, len);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Cipher getEncryptCipher(String algorithm, Key key) {
		Cipher cipher = getCipher(algorithm);
		return doInitEncryptMode(cipher, key);
	}

	public static Cipher getEncryptCipher(String provider, String algorithm, Key key) {
		Cipher cipher = getCipher(provider, algorithm);
		return doInitEncryptMode(cipher, key);
	}

	public static Cipher getEncryptCipher(Provider provider, String algorithm, Key key) {
		Cipher cipher = getCipher(provider, algorithm);
		return doInitEncryptMode(cipher, key);
	}

	public static Cipher getDecryptCipher(String algorithm, Key key) {
		Cipher cipher = getCipher(algorithm);
		return doInitDecryptMode(cipher, key);
	}

	public static Cipher getDecryptCipher(String provider, String algorithm, Key key) {
		Cipher cipher = getCipher(provider, algorithm);
		return doInitDecryptMode(cipher, key);
	}

	public static Cipher getDecryptCipher(Provider provider, String algorithm, Key key) {
		Cipher cipher = getCipher(provider, algorithm);
		return doInitDecryptMode(cipher, key);
	}

	public static IEncryptor getEncryptor(String algorithm, Key key) {
		Cipher cipher = getEncryptCipher(algorithm, key);
		return getEncryptor(cipher);
	}

	public static IEncryptor getEncryptor(String provider, String algorithm, Key key) {
		Cipher cipher = getEncryptCipher(provider, algorithm, key);
		return getEncryptor(cipher);
	}

	public static IEncryptor getEncryptor(Provider provider, String algorithm, Key key) {
		Cipher cipher = getEncryptCipher(provider, algorithm, key);
		return getEncryptor(cipher);
	}

	private static IEncryptor getEncryptor(Cipher cipher) {
		return new IEncryptor() {
			@Override
			public IEncryptor update(byte[] data, int offset, int len) {
				doUpdate(cipher, data, offset, len);
				return this;
			}

			@Override
			public byte[] encrypt(byte[] data, int offset, int len) {
				return doFinal(cipher, data, offset, len);
			}
		};
	}

	public static IDecryptor getDecryptor(String algorithm, Key key) {
		Cipher cipher = getDecryptCipher(algorithm, key);
		return getDecryptor(cipher);
	}

	public static IDecryptor getDecryptor(String provider, String algorithm, Key key) {
		Cipher cipher = getDecryptCipher(provider, algorithm, key);
		return getDecryptor(cipher);
	}

	public static IDecryptor getDecryptor(Provider provider, String algorithm, Key key) {
		Cipher cipher = getDecryptCipher(provider, algorithm, key);
		return getDecryptor(cipher);
	}

	private static IDecryptor getDecryptor(Cipher cipher) {
		return new IDecryptor() {
			@Override
			public IDecryptor update(byte[] data, int offset, int len) {
				doUpdate(cipher, data, offset, len);
				return this;
			}

			@Override
			public byte[] decrypt(byte[] data, int offset, int len) {
				return doFinal(cipher, data, offset, len);
			}
		};
	}

	public static byte[] encrypt(String algorithm, Key key, byte[] data) {
		Cipher cipher = getEncryptCipher(algorithm, key);
		return doFinal(cipher, data);
	}

	public static byte[] encrypt(String provider, String algorithm, Key key, byte[] data) {
		Cipher cipher = getEncryptCipher(provider, algorithm, key);
		return doFinal(cipher, data);
	}

	public static byte[] encrypt(Provider provider, String algorithm, Key key, byte[] data) {
		Cipher cipher = getEncryptCipher(provider, algorithm, key);
		return doFinal(cipher, data);
	}

	public static byte[] encrypt(String algorithm, byte[] key, byte[] data) {
		return encrypt(algorithm, CryptoKeys.generateKey(algorithm, key), data);
	}

	public static byte[] encrypt(String provider, String algorithm, byte[] key, byte[] data) {
		return encrypt(provider, algorithm, CryptoKeys.generateKey(provider, algorithm, key), data);
	}

	public static byte[] encrypt(Provider provider, String algorithm, byte[] key, byte[] data) {
		return encrypt(provider, algorithm, CryptoKeys.generateKey(provider, algorithm, key), data);
	}

	public static byte[] decrypt(String algorithm, Key key, byte[] data) {
		Cipher cipher = getDecryptCipher(algorithm, key);
		return doFinal(cipher, data);
	}

	public static byte[] decrypt(String provider, String algorithm, Key key, byte[] data) {
		Cipher cipher = getDecryptCipher(provider, algorithm, key);
		return doFinal(cipher, data);
	}

	public static byte[] decrypt(Provider provider, String algorithm, Key key, byte[] data) {
		Cipher cipher = getDecryptCipher(provider, algorithm, key);
		return doFinal(cipher, data);
	}

	public static byte[] decrypt(String algorithm, byte[] key, byte[] data) {
		return decrypt(algorithm, CryptoKeys.generateKey(algorithm, key), data);
	}

	public static byte[] decrypt(String provider, String algorithm, byte[] key, byte[] data) {
		return decrypt(provider, algorithm, CryptoKeys.generateKey(provider, algorithm, key), data);
	}

	public static byte[] decrypt(Provider provider, String algorithm, byte[] key, byte[] data) {
		return decrypt(provider, algorithm, CryptoKeys.generateKey(provider, algorithm, key), data);
	}


	public static byte[] encryptByKeySeed(String algorithm, byte[] seed, byte[] data) {
		return encrypt(algorithm, CryptoKeys.generateKeyBySeed(algorithm, seed), data);
	}

	public static byte[] encryptByKeySeed(String provider, String algorithm, byte[] seed, byte[] data) {
		return encrypt(provider, algorithm, CryptoKeys.generateKeyBySeed(provider, algorithm, seed), data);
	}

	public static byte[] encryptByKeySeed(Provider provider, String algorithm, byte[] seed, byte[] data) {
		return encrypt(provider, algorithm, CryptoKeys.generateKeyBySeed(provider, algorithm, seed), data);
	}


	public static byte[] decryptByKeySeed(String algorithm, byte[] seed, byte[] data) {
		return decrypt(algorithm, CryptoKeys.generateKeyBySeed(algorithm, seed), data);
	}

	public static byte[] decryptByKeySeed(String provider, String algorithm, byte[] seed, byte[] data) {
		return decrypt(provider, algorithm, CryptoKeys.generateKeyBySeed(provider, algorithm, seed), data);
	}

	public static byte[] decryptByKeySeed(Provider provider, String algorithm, byte[] seed, byte[] data) {
		return decrypt(provider, algorithm, CryptoKeys.generateKeyBySeed(provider, algorithm, seed), data);
	}
}
