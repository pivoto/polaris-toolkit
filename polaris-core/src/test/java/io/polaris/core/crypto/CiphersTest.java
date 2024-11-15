package io.polaris.core.crypto;

import io.polaris.core.crypto.symmetric.SymmetricAlgorithm;
import io.polaris.core.io.Consoles;
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
		String msg1 = "key: " + Base64.getEncoder().encodeToString(key.getEncoded());
		Consoles.println(msg1);

		encrypted = Ciphers.encrypt(SymmetricAlgorithm.AES.code(), key, data);
		String msg = "encrypted: " + Base64.getEncoder().encodeToString(encrypted);
		Consoles.println(msg);
		decrypted = Ciphers.decrypt(SymmetricAlgorithm.AES.code(), key, encrypted);
		Consoles.println("decrypted: " + new String(decrypted));
		Assertions.assertArrayEquals(data, decrypted);

		key = CryptoKeys.generateKey(SymmetricAlgorithm.AES.code(), key.getEncoded());
		decrypted = Ciphers.decrypt(SymmetricAlgorithm.AES.code(), key, encrypted);
		Consoles.println("decrypted: " + new String(decrypted));
		Assertions.assertArrayEquals(data, decrypted);


	}
}
