package io.polaris.toolkit.core.crypto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

class RSATest {

	@Test
	@DisplayName("测试加解密")
	void test() throws GeneralSecurityException, UnsupportedEncodingException {
		String password = "123456";
		String[] arr = RSA.genKeyPairStr(512);
		System.out.println("privateKey:" + arr[0]);
		System.out.println("publicKey:" + arr[1]);
		String encrypt = RSA.encrypt(RSA.toPrivateKey(arr[0]), password);
		System.out.println("password:" + encrypt);
		System.out.println("plain:" + RSA.decrypt(RSA.toPublicKey(arr[1]), encrypt));
	}
}
