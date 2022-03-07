package io.polaris.toolkit.core.crypto;


import io.polaris.toolkit.core.crypto.RSA;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

class RSATest {

	@Test
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
