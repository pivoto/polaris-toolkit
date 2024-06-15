package io.polaris.core.crypto.digest;

import java.security.MessageDigest;
import java.security.Security;
import java.util.Base64;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DigestsTest {

	@BeforeAll
	static void beforeAll() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
	}

	@Test
	void test01() throws Exception {
		MessageDigest digest = Digests.getDigest(DigestAlgorithm.SM3.code());
		byte[] rs = Digests.digest(digest, "测试".getBytes());
		TestConsole.println("digest: {}", Base64.getEncoder().encodeToString(rs));
		Assertions.assertEquals("b8+IajEV6zsY0tuhtEE/7VBnwVTgMCdtinjKp3O0Tqs=", Base64.getEncoder().encodeToString(rs));
	}
}
