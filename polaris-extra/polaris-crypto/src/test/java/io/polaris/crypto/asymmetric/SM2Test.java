package io.polaris.crypto.asymmetric;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import io.polaris.crypto.SmUtils;
import org.bouncycastle.crypto.CryptoException;
import org.junit.jupiter.api.Test;

class SM2Test {

	@Test
	void test01() throws GeneralSecurityException, CryptoException {
		SM2 sm2 = SmUtils.sm2();
		byte[] data = "测试一下1234".getBytes(StandardCharsets.UTF_8);
		byte[] encrypted = sm2.encrypt(data);
		byte[] decrypted = sm2.decrypt(encrypted);
		System.out.println(new String(decrypted, StandardCharsets.UTF_8));

		byte[] sign = sm2.sign(data);
		boolean verify = sm2.verify(data, sign);
		System.out.println(verify);
	}

	@Test
	void test02() {
	}
}
