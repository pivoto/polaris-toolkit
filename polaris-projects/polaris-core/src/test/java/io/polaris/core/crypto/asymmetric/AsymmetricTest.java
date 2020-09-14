package io.polaris.core.crypto.asymmetric;

import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.crypto.IDecryptor;
import io.polaris.core.crypto.IEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;

class AsymmetricTest {
	@BeforeAll
	static void beforeAll() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(),1);
	}

	@Test
	public void test1() throws GeneralSecurityException {
		testAsymmetric(AsymmetricAlgorithm.RSA);
		testAsymmetric(AsymmetricAlgorithm.RSA_ECB_PKCS1);
		testAsymmetric(AsymmetricAlgorithm.RSA_ECB);
		testAsymmetric(AsymmetricAlgorithm.RSA_None);
	}

	@Test
	public void test2() throws GeneralSecurityException {
		//testAsymmetric(AsymmetricAlgorithm.DSA);
		testAsymmetric(AsymmetricAlgorithm.SM2);
	}

	private void testAsymmetric(AsymmetricAlgorithm algorithm) throws GeneralSecurityException {
		try {
			System.out.println("algorithm: " + algorithm);
			String data = "测试一下";
			KeyPair pair = CryptoKeys.generateKeyPair(algorithm.code());
			IEncryptor encryptor = Ciphers.getEncryptor(algorithm.code(), pair.getPublic());
			byte[] encrypted = encryptor.encrypt(data.getBytes(StandardCharsets.UTF_8));
			IDecryptor decryptor = Ciphers.getDecryptor(algorithm.code(), pair.getPrivate());
			byte[] decrypted = decryptor.decrypt(encrypted);
			System.out.println("decrypted: " + new String(decrypted, StandardCharsets.UTF_8));
			System.out.println();
			Assertions.assertEquals(data, new String(decrypted, StandardCharsets.UTF_8));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
