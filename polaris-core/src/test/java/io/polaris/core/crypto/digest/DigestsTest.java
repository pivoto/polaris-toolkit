package io.polaris.core.crypto.digest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.Security;
import java.util.Base64;

class DigestsTest {

	@BeforeAll
	static void beforeAll() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
	}

	@Test
	void test01() throws Exception {
		MessageDigest digest = Digests.getDigest(DigestAlgorithm.SM3.code());
		byte[] rs = Digests.digest(digest, "测试".getBytes());
		System.out.println(Base64.getEncoder().encodeToString(rs));
	}
}
