package io.polaris.core.crypto.digest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

class HmacsTest {
	@BeforeAll
	static void beforeAll() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
	}

	@Test
	void test01() throws NoSuchAlgorithmException, InvalidKeyException {
		byte[] key = new byte[16]; // SM4CMAC 长度限制
		Arrays.fill(key, (byte) 88);
		System.out.println(key.length);
		Mac mac = Hmacs.getInitializedMac(HmacAlgorithm.SM4CMAC.code(), key);
		byte[] hmac = Hmacs.hmac(mac, "测试".getBytes());
		System.out.println(Base64.getEncoder().encodeToString(hmac));
	}
}
