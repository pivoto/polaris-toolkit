package io.polaris.core.crypto.digest;

import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Mac;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HmacsTest {
	@BeforeAll
	static void beforeAll() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
	}

	@Test
	void test_SM4CMAC() {
		// SM4CMAC 长度限制 128 bit
		byte[] key = new byte[16];
		Arrays.fill(key, (byte) 88);
		TestConsole.println("key-length: {}", key.length);
		Mac mac = Hmacs.getInitializedMac(HmacAlgorithm.SM4CMAC.code(), key);
		byte[] hmac = Hmacs.hmac(mac, "测试".getBytes());
		TestConsole.println("hmac: {}", Base64.getEncoder().encodeToString(hmac));
		Assertions.assertEquals("gmDtOkITAVDgrnG4hjK2Jg==", Base64.getEncoder().encodeToString(hmac));
	}


}
