package io.polaris.core.crypto.asymmetric;

import java.security.KeyPair;

import io.polaris.core.crypto.symmetric.AES;
import io.polaris.core.crypto.symmetric.DES;
import io.polaris.core.crypto.CryptoRuntimeException;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DSATest {

	@Test
	void test01() throws Exception {
		KeyPair keyPair = DSA.getKeyPair();
		String data = "123456";
		byte[] bytes = data.getBytes();

		{
			byte[] sign = DSA.sign(keyPair.getPrivate(), bytes);
			Assertions.assertTrue(DSA.verify(keyPair.getPublic(), bytes, sign));
			Assertions.assertFalse(DSA.verify(keyPair.getPublic(), "12345".getBytes(), sign));
		}

		{
			// java.security.InvalidKeyException
			Assertions.assertThrows(CryptoRuntimeException.class, ()->{
				String key = Randoms.randomString(8);
				byte[] encrypt = AES.encrypt(bytes, key.getBytes());
				byte[] decrypt = AES.decrypt(encrypt, key.getBytes());
				Assertions.assertArrayEquals(bytes, decrypt);
			});
		}
		{
			String key = Randoms.randomString(16);
			byte[] encrypt = AES.encrypt(bytes, key.getBytes());
			byte[] decrypt = AES.decrypt(encrypt, key.getBytes());
			Assertions.assertArrayEquals(bytes, decrypt);
		}

		{
			String key = Randoms.randomString(8);
			byte[] encrypt = AES.encryptByKeySeed(bytes, key.getBytes());
			byte[] decrypt = AES.decryptByKeySeed(encrypt, key.getBytes());
			Assertions.assertArrayEquals(bytes, decrypt);
		}

		{
			// java.security.InvalidKeyException: Wrong key size
			Assertions.assertThrows(CryptoRuntimeException.class, ()->{
				String key = Randoms.randomString(4);
				byte[] encrypt = DES.encrypt(bytes, key.getBytes());
				byte[] decrypt = DES.decrypt(encrypt, key.getBytes());
				Assertions.assertArrayEquals(bytes, decrypt);
			});
		}
		{
			String key = Randoms.randomString(4);
			byte[] encrypt = DES.encryptByKeySeed(bytes, key.getBytes());
			byte[] decrypt = DES.decryptByKeySeed(encrypt, key.getBytes());
			Assertions.assertArrayEquals(bytes, decrypt);
		}
		{
			String key = Randoms.randomString(8);
			byte[] encrypt = DES.encrypt(bytes, key.getBytes());
			byte[] decrypt = DES.decrypt(encrypt, key.getBytes());
			Assertions.assertArrayEquals(bytes, decrypt);
		}

	}
}
