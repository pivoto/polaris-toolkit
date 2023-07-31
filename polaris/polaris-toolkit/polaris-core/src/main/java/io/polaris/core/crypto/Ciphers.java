package io.polaris.core.crypto;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * @author Qt
 * @since 1.8
 */
public class Ciphers {

	public static Cipher getCipher(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
		return CryptoKeys.getCipher(algorithm);
	}

	public static Cipher getEncryptCipher(String algorithm, Key key) throws GeneralSecurityException {
		Cipher cipher = getCipher(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher;
	}

	public static Cipher getDecryptCipher(String algorithm, Key key) throws GeneralSecurityException {
		Cipher cipher = getCipher(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher;
	}

	public static IEncryptor getEncryptor(String algorithm, Key key) throws GeneralSecurityException {
		Cipher cipher = getEncryptCipher(algorithm, key);
		return getEncryptor(cipher);
	}

	public static IEncryptor getEncryptor(Cipher cipher) {
		return new IEncryptor() {
			@Override
			public IEncryptor update(byte[] data, int offset, int len) throws GeneralSecurityException {
				cipher.update(data, offset, len);
				return this;
			}

			@Override
			public byte[] encrypt(byte[] data, int offset, int len) throws GeneralSecurityException {
				return cipher.doFinal(data, offset, len);
			}
		};
	}

	public static IDecryptor getDecryptor(String algorithm, Key key) throws GeneralSecurityException {
		Cipher cipher = getDecryptCipher(algorithm, key);
		return getDecryptor(cipher);
	}

	public static IDecryptor getDecryptor(Cipher cipher) {
		return new IDecryptor() {
			@Override
			public IDecryptor update(byte[] data, int offset, int len) throws GeneralSecurityException {
				cipher.update(data, offset, len);
				return this;
			}

			@Override
			public byte[] decrypt(byte[] data, int offset, int len) throws GeneralSecurityException {
				return cipher.doFinal(data, offset, len);
			}
		};
	}

	public static Cipher doUpdate(Cipher cipher, byte[] data) {
		cipher.update(data);
		return cipher;
	}

	public static byte[] doFinal(Cipher cipher, byte[] data) throws GeneralSecurityException {
		return cipher.doFinal(data);
	}

	public static byte[] encrypt(String algorithm, Key key, byte[] data) throws GeneralSecurityException {
		Cipher cipher = getEncryptCipher(algorithm, key);
		return cipher.doFinal(data);
	}

	public static byte[] encrypt(String algorithm, byte[] key, byte[] data) throws GeneralSecurityException {
		return encrypt(algorithm, CryptoKeys.generateKey(algorithm, key), data);
	}


	public static byte[] encryptByKeySeed(String algorithm, byte[] seed, byte[] data) throws GeneralSecurityException {
		return encrypt(algorithm, CryptoKeys.generateKeyBySeed(algorithm, seed), data);
	}

	public static byte[] decrypt(String algorithm, Key key, byte[] data) throws GeneralSecurityException {
		Cipher cipher = getDecryptCipher(algorithm, key);
		return cipher.doFinal(data);
	}

	public static byte[] decrypt(String algorithm, byte[] key, byte[] data) throws GeneralSecurityException {
		return decrypt(algorithm, CryptoKeys.generateKey(algorithm, key), data);
	}

	public static byte[] decryptByKeySeed(String algorithm, byte[] seed, byte[] data) throws GeneralSecurityException {
		return decrypt(algorithm, CryptoKeys.generateKeyBySeed(algorithm, seed), data);
	}
}
