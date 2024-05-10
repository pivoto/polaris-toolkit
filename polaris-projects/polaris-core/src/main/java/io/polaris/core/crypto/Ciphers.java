package io.polaris.core.crypto;

import java.security.InvalidKeyException;
import java.security.Key;

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

	public static Cipher getEncryptCipher(String algorithm, Key key) {
		try {
			Cipher cipher = getCipher(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher;
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static Cipher getDecryptCipher(String algorithm, Key key) {
		try {
			Cipher cipher = getCipher(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher;
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static IEncryptor getEncryptor(String algorithm, Key key) {
		Cipher cipher = getEncryptCipher(algorithm, key);
		return getEncryptor(cipher);
	}

	public static IEncryptor getEncryptor(Cipher cipher) {
		return new IEncryptor() {
			@Override
			public IEncryptor update(byte[] data, int offset, int len) {
				cipher.update(data, offset, len);
				return this;
			}

			@Override
			public byte[] encrypt(byte[] data, int offset, int len) {
				try {
					return cipher.doFinal(data, offset, len);
				} catch (IllegalBlockSizeException | BadPaddingException e) {
					throw new CryptoRuntimeException(e);
				}
			}
		};
	}

	public static IDecryptor getDecryptor(String algorithm, Key key) {
		Cipher cipher = getDecryptCipher(algorithm, key);
		return getDecryptor(cipher);
	}

	public static IDecryptor getDecryptor(Cipher cipher) {
		return new IDecryptor() {
			@Override
			public IDecryptor update(byte[] data, int offset, int len) {
				cipher.update(data, offset, len);
				return this;
			}

			@Override
			public byte[] decrypt(byte[] data, int offset, int len)  {
				try {
					return cipher.doFinal(data, offset, len);
				} catch (IllegalBlockSizeException | BadPaddingException e) {
					throw new CryptoRuntimeException(e);
				}
			}
		};
	}

	public static Cipher doUpdate(Cipher cipher, byte[] data) {
		cipher.update(data);
		return cipher;
	}

	public static byte[] doFinal(Cipher cipher, byte[] data) {
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static byte[] encrypt(String algorithm, Key key, byte[] data) {
		Cipher cipher = getEncryptCipher(algorithm, key);
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static byte[] encrypt(String algorithm, byte[] key, byte[] data) {
		return encrypt(algorithm, CryptoKeys.generateKey(algorithm, key), data);
	}


	public static byte[] encryptByKeySeed(String algorithm, byte[] seed, byte[] data) {
		return encrypt(algorithm, CryptoKeys.generateKeyBySeed(algorithm, seed), data);
	}

	public static byte[] decrypt(String algorithm, Key key, byte[] data) {
		Cipher cipher = getDecryptCipher(algorithm, key);
		try {
			return cipher.doFinal(data);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new CryptoRuntimeException(e);
		}
	}

	public static byte[] decrypt(String algorithm, byte[] key, byte[] data) {
		return decrypt(algorithm, CryptoKeys.generateKey(algorithm, key), data);
	}

	public static byte[] decryptByKeySeed(String algorithm, byte[] seed, byte[] data) {
		return decrypt(algorithm, CryptoKeys.generateKeyBySeed(algorithm, seed), data);
	}
}
