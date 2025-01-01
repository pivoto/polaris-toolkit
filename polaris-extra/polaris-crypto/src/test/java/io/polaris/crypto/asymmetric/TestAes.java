package io.polaris.crypto.asymmetric;

import javax.crypto.SecretKey;

import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.CryptoRuntimeException;
import io.polaris.core.crypto.symmetric.AES;
import io.polaris.core.io.Consoles;
import io.polaris.core.random.Randoms;
import io.polaris.core.string.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Jan 01, 2025
 */
public class TestAes {
	String data = "12345678";
	byte[] bytes = data.getBytes();

	@Test
	void test01() throws Exception {

		{
			// java.security.InvalidKeyException
			Assertions.assertThrows(CryptoRuntimeException.class, () -> {
				String key = Randoms.randomString(8);
				byte[] encrypt = AES.encrypt(bytes, key.getBytes());
				Consoles.log(Hex.formatBytes(encrypt));
				byte[] decrypt = AES.decrypt(encrypt, key.getBytes());
				Assertions.assertArrayEquals(bytes, decrypt);
			});
		}
	}

	@Test
	void test02() throws Exception {
		{
			String key = Randoms.randomString(16);
			byte[] encrypt = AES.encrypt(bytes, key.getBytes());
			Consoles.log(Hex.formatBytes(encrypt));
			byte[] decrypt = AES.decrypt(encrypt, key.getBytes());
			Assertions.assertArrayEquals(bytes, decrypt);
		}
		{
			String key = Randoms.randomString(16);
			byte[] encrypt = Ciphers.encrypt("AES/ECB/PKCS7Padding", key.getBytes(), bytes);
			Consoles.log(Hex.formatBytes(encrypt));
			byte[] decrypt = Ciphers.decrypt("AES/ECB/PKCS7Padding", key.getBytes(), encrypt);
			Assertions.assertArrayEquals(bytes, decrypt);
		}

	}

	@Test
	void test03() throws Exception {
		{
			String key = Randoms.randomString(16);
			Consoles.log("key", Hex.formatBytes(CryptoKeys.generateKeyBySeed("SunJCE", "AES/ECB/PKCS5Padding", key.getBytes()).getEncoded()));
			Consoles.log("key", Hex.formatBytes(CryptoKeys.generateKeyBySeed("SunJCE", "AES/ECB/PKCS5Padding", key.getBytes()).getEncoded()));
			SecretKey secretKey = CryptoKeys.generateKeyBySeed("SunJCE", "AES/ECB/PKCS5Padding", key.getBytes());
			byte[] encrypt = Ciphers.encrypt("SunJCE", "AES/ECB/PKCS5Padding", secretKey, bytes);
			Consoles.log(Hex.formatBytes(encrypt));
			byte[] decrypt = Ciphers.decrypt("SunJCE", "AES/ECB/PKCS5Padding", secretKey, encrypt);
			Assertions.assertArrayEquals(bytes, decrypt);
		}

	}

	@Test
	void test04() throws Exception {
		{
			String key = Randoms.randomString(8);
			Consoles.log("key", Hex.formatBytes(CryptoKeys.generateKeyBySeed("AES/ECB/PKCS5Padding", key.getBytes()).getEncoded()));
			Consoles.log("key", Hex.formatBytes(CryptoKeys.generateKeyBySeed("AES/ECB/PKCS5Padding", key.getBytes()).getEncoded()));
			byte[] encrypt = Ciphers.encryptByKeySeed("AES/ECB/PKCS5Padding", key.getBytes(), bytes);
			Consoles.log(Hex.formatBytes(encrypt));
			byte[] decrypt = Ciphers.decryptByKeySeed("AES/ECB/PKCS5Padding", key.getBytes(), encrypt);
			Assertions.assertArrayEquals(bytes, decrypt);
		}
	}

}
