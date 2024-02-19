package io.polaris.core.crypto;

import io.polaris.core.TestConsole;
import io.polaris.core.crypto.symmetric.SymmetricAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.util.Base64;

class CiphersTest {


	@Test
	void test01() throws GeneralSecurityException {
		byte[] data = "123456".getBytes();
		byte[] encrypted, decrypted;
		SecretKey key = CryptoKeys.generateKey(SymmetricAlgorithm.AES.code(), 128);
		TestConsole.println("key: " + Base64.getEncoder().encodeToString(key.getEncoded()));

		encrypted = Ciphers.encrypt(SymmetricAlgorithm.AES.code(), key, data);
		TestConsole.println("encrypted: " + Base64.getEncoder().encodeToString(encrypted));
		decrypted = Ciphers.decrypt(SymmetricAlgorithm.AES.code(), key, encrypted);
		TestConsole.println("decrypted: " + new String(decrypted));
		Assertions.assertArrayEquals(data, decrypted);

		key = CryptoKeys.generateKey(SymmetricAlgorithm.AES.code(), key.getEncoded());
		decrypted = Ciphers.decrypt(SymmetricAlgorithm.AES.code(), key, encrypted);
		TestConsole.println("decrypted: " + new String(decrypted));
		Assertions.assertArrayEquals(data, decrypted);


	}
}
