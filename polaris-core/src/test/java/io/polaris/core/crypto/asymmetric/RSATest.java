package io.polaris.core.crypto.asymmetric;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Base64;

import io.polaris.core.TestConsole;
import io.polaris.core.crypto.Ciphers;
import io.polaris.core.crypto.CryptoKeys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class RSATest {
	@BeforeAll
	static void beforeAll() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
	}


	@Test
	void test01() throws GeneralSecurityException, UnsupportedEncodingException {
		KeyPair keyPair = CryptoKeys.generateKeyPair(AsymmetricAlgorithm.RSA.code());
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		String data = "123456";
		{
			String encrypted = RSA.encrypt(publicKey, data);
			TestConsole.println("encrypted: {}", encrypted);
			byte[] decrypt = Ciphers.decrypt(AsymmetricAlgorithm.RSA_ECB_PKCS1.code(), privateKey, Base64.getDecoder().decode(encrypted));
			TestConsole.println("decrypt: {}", new String(decrypt));
			Assertions.assertEquals(data, new String(decrypt));

			byte[] encrypt = Ciphers.encrypt(AsymmetricAlgorithm.RSA_ECB_PKCS1.code(), publicKey, decrypt);
			TestConsole.println("encrypt: {}", Base64.getEncoder().encodeToString(encrypt));
			TestConsole.println("decrypt: {}", RSA.decrypt(privateKey, Base64.getEncoder().encodeToString(encrypt)));
			Assertions.assertEquals(data, RSA.decrypt(privateKey, Base64.getEncoder().encodeToString(encrypt)));
		}
		{
			String encrypted = RSA.encrypt(privateKey, data);
			TestConsole.println("encrypted: {}", encrypted);
			byte[] decrypt = Ciphers.decrypt(AsymmetricAlgorithm.RSA_ECB_PKCS1.code(), publicKey, Base64.getDecoder().decode(encrypted));
			TestConsole.println("decrypt: {}", new String(decrypt));
			Assertions.assertEquals(data, new String(decrypt));

			byte[] encrypt = Ciphers.encrypt(AsymmetricAlgorithm.RSA_ECB_PKCS1.code(), privateKey, decrypt);
			TestConsole.println("encrypt: {}", Base64.getEncoder().encodeToString(encrypt));
			TestConsole.println("decrypt: {}", RSA.decrypt(publicKey, Base64.getEncoder().encodeToString(encrypt)));
			Assertions.assertEquals(data, RSA.decrypt(publicKey, Base64.getEncoder().encodeToString(encrypt)));
		}

	}

	@Test
	void test02() throws GeneralSecurityException, UnsupportedEncodingException {
		String key = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANpps1ERytVE3J/aZNXLsb3A9907yIPwar4z/qngdEysSnz2P/8AU7RJyqjTf/gYTl87XXkvqV2GcXito2ZASQcCAwEAAQ==";
		String cipherText = "jmYXcvdLGT+YuAOx1G4ro7G3wrwxbd+mGaFul8cYG5wRJGzYkQ43TlxtWfoAS/lmyd5kmwGo+2WAFmN4fBdK5w==";
		String s = RSA.decryptByPublicKey(key, cipherText);
		TestConsole.println(s);

		PublicKey publicKey = CryptoKeys.toPublicKey(AsymmetricAlgorithm.RSA.code(), Base64.getDecoder().decode(key));
		byte[] decrypted = Ciphers.decrypt(AsymmetricAlgorithm.RSA_ECB_PKCS1.code(), publicKey, Base64.getDecoder().decode(cipherText));
		TestConsole.println(new String(decrypted));
		Assertions.assertEquals(s, new String(decrypted));
	}
}
