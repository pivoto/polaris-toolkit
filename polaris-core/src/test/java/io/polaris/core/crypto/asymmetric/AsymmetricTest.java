package io.polaris.core.crypto.asymmetric;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;

import io.polaris.core.TestConsole;
import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.IDecryptor;
import io.polaris.core.crypto.IEncryptor;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AsymmetricTest {
	@BeforeAll
	static void beforeAll() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
	}

	@Test
	public void test1() throws GeneralSecurityException {
		for (AsymmetricAlgorithm algorithm : AsymmetricAlgorithm.values()) {
			if (algorithm == AsymmetricAlgorithm.DSA) {
				continue;
			}
			testAsymmetric(algorithm);
		}
	}

	private void testAsymmetric(AsymmetricAlgorithm algorithm) {
		String data = "测试一下";
		TestConsole.println(Strings.repeat('-', 80));
		TestConsole.println("algorithm: {}", algorithm);
		TestConsole.println("data: {}", data);
		KeyPair pair = CryptoKeys.generateKeyPair(algorithm.code());
		IEncryptor encryptor = Ciphers.getEncryptor(algorithm.code(), pair.getPublic());
		byte[] encrypted = encryptor.encrypt(data.getBytes(StandardCharsets.UTF_8));
		TestConsole.println("encrypted: {}", new String(encrypted, StandardCharsets.UTF_8));

		IDecryptor decryptor = Ciphers.getDecryptor(algorithm.code(), pair.getPrivate());
		byte[] decrypted = decryptor.decrypt(encrypted);
		TestConsole.println("decrypted: {}", new String(decrypted, StandardCharsets.UTF_8));

		Assertions.assertEquals(data, new String(decrypted, StandardCharsets.UTF_8));
		Assertions.assertArrayEquals(data.getBytes(StandardCharsets.UTF_8), decrypted);
	}
}
