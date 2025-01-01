package io.polaris.core.crypto.symmetric;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Base64;

class SymmetricTest {


	@BeforeAll
	static void beforeAll() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
	}

	@Test
	void test01() throws Exception {
		testAlgorithm(SymmetricAlgorithm.SM4);
	}

	@Test
	void test02() throws Exception {
		testAlgorithm(SymmetricAlgorithm.SM4);
		testAlgorithm(SymmetricAlgorithm.AES);
		testAlgorithm(SymmetricAlgorithm.ARCFOUR);
		testAlgorithm(SymmetricAlgorithm.Blowfish);
		testAlgorithm(SymmetricAlgorithm.DES);
		testAlgorithm(SymmetricAlgorithm.DESede);
		testAlgorithm(SymmetricAlgorithm.RC2);
	}

	private static void testAlgorithm(SymmetricAlgorithm algorithm) throws GeneralSecurityException {
		System.out.println(algorithm);
		Symmetric symmetric = Symmetric.of(algorithm.code());
		byte[] data = "测试".getBytes();
		byte[] rs = symmetric.encrypt(data);
		System.out.println(Base64.getEncoder().encodeToString(rs));
		byte[] decrypted = symmetric.decrypt(rs);
		System.out.println(new String(decrypted));
		System.out.println();
		Assertions.assertArrayEquals(data, decrypted);
	}
}
