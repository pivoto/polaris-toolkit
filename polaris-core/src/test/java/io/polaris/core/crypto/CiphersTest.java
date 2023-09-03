package io.polaris.core.crypto;

import io.polaris.core.crypto.symmetric.SymmetricAlgorithm;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.util.Base64;

class CiphersTest {


	@Test
	void test01() throws GeneralSecurityException {
		byte[] encrypted, decrypted;
		SecretKey key = CryptoKeys.generateKey(SymmetricAlgorithm.AES.code(), 128);
		System.out.println("key: " + Base64.getEncoder().encodeToString(key.getEncoded()));

		encrypted = Ciphers.encrypt(SymmetricAlgorithm.AES.code(), key, "123456".getBytes());
		System.out.println("encrypted: " + Base64.getEncoder().encodeToString(encrypted));
		decrypted = Ciphers.decrypt(SymmetricAlgorithm.AES.code(), key, encrypted);
		System.out.println("decrypted: " + new String(decrypted));

		key = CryptoKeys.generateKey(SymmetricAlgorithm.AES.code(), key.getEncoded());
		decrypted = Ciphers.decrypt(SymmetricAlgorithm.AES.code(), key, encrypted);
		System.out.println("decrypted: " + new String(decrypted));


	}
}
