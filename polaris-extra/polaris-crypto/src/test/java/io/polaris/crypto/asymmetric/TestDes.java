package io.polaris.crypto.asymmetric;

import javax.crypto.SecretKey;

import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.CryptoRuntimeException;
import io.polaris.core.crypto.symmetric.AES;
import io.polaris.core.crypto.symmetric.DES;
import io.polaris.core.io.Consoles;
import io.polaris.core.random.Randoms;
import io.polaris.core.string.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Jan 01, 2025
 */
public class TestDes {
	String data = "12345678";
	byte[] bytes = data.getBytes();

	@Test
	void test01() throws Exception {
		{
			// java.security.InvalidKeyException: Wrong key size
			Assertions.assertThrows(CryptoRuntimeException.class, ()->{
				String key = Randoms.randomString(4);
				byte[] encrypt = DES.encrypt(bytes, key.getBytes());
				Consoles.log(Hex.formatBytes(encrypt));
				byte[] decrypt = DES.decrypt(encrypt, key.getBytes());
				Assertions.assertArrayEquals(bytes, decrypt);
			});
		}
		{
			String key = Randoms.randomString(4);
			byte[] encrypt = DES.encryptByKeySeed(bytes, key.getBytes());
			Consoles.log(Hex.formatBytes(encrypt));
			byte[] decrypt = DES.decryptByKeySeed(encrypt, key.getBytes());
			Assertions.assertArrayEquals(bytes, decrypt);
		}
		{
			String key = Randoms.randomString(8);
			byte[] encrypt = DES.encrypt(bytes, key.getBytes());
			Consoles.log(Hex.formatBytes(encrypt));
			byte[] decrypt = DES.decrypt(encrypt, key.getBytes());
			Assertions.assertArrayEquals(bytes, decrypt);
		}
	}

	@Test
	void test02() throws Exception {
		String key = Randoms.randomString(111);
		byte[] encrypt = DES.encrypt(bytes, key.getBytes());
		Consoles.log(Hex.formatBytes(encrypt));
		byte[] decrypt = DES.decrypt(encrypt, key.getBytes());
		Assertions.assertArrayEquals(bytes, decrypt);
	}

}
