package io.polaris.core.crypto.asymmetric;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.Security;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;

import io.polaris.core.TestConsole;
import io.polaris.core.crypto.CryptoKeys;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SignaturesTest {
	@BeforeAll
	static void beforeAll() {
		Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
	}


	@Test
	public void test01() throws GeneralSecurityException {

		signAndVerify(SignAlgorithm.NONEwithRSA);
		signAndVerify(SignAlgorithm.MD2withRSA);
		signAndVerify(SignAlgorithm.MD5withRSA);

		signAndVerify(SignAlgorithm.SHA1withRSA);
		signAndVerify(SignAlgorithm.SHA256withRSA);
		signAndVerify(SignAlgorithm.SHA384withRSA);
		signAndVerify(SignAlgorithm.SHA512withRSA);

		signAndVerify(SignAlgorithm.NONEwithDSA);
		signAndVerify(SignAlgorithm.SHA1withDSA);

		signAndVerify(SignAlgorithm.NONEwithECDSA);
		signAndVerify(SignAlgorithm.SHA1withECDSA);
		signAndVerify(SignAlgorithm.SHA1withECDSA);
		signAndVerify(SignAlgorithm.SHA256withECDSA);
		signAndVerify(SignAlgorithm.SHA384withECDSA);
		signAndVerify(SignAlgorithm.SHA512withECDSA);

	}


	@Test
	public void test02() throws GeneralSecurityException {
		signAndVerify(SignAlgorithm.SHA256withRSA_PSS);
		signAndVerify(SignAlgorithm.SHA384withRSA_PSS);
		signAndVerify(SignAlgorithm.SHA512withRSA_PSS);
	}

	private void signAndVerify(SignAlgorithm signAlgorithm) throws GeneralSecurityException {
		TestConsole.println(Strings.repeat('-', 80));
		TestConsole.println("signAlgorithm: " + signAlgorithm);
		byte[] data = "一段测试文字".getBytes(StandardCharsets.UTF_8);
		if (SignAlgorithm.NONEwithDSA == signAlgorithm) {
			// 有长度限制：20
			data = Arrays.copyOfRange(Arrays.copyOf(data, 20), 0, 20);
		}

		TestConsole.println("data: " + data.length + " : " + new String(data, StandardCharsets.UTF_8));

		KeyPair keyPair = CryptoKeys.generateKeyPair(signAlgorithm.code(), 2048);
		Signature signature = Signatures.getInitializedSignature(signAlgorithm.code(), keyPair.getPrivate());
		byte[] signed = Signatures.doSign(signature, data);
		TestConsole.println("signed: " + Base64.getEncoder().encodeToString(signed));

		signature = Signatures.getInitializedSignature(signAlgorithm.code(), keyPair.getPublic());
		boolean verify = Signatures.doVerify(signature, data, signed);
		TestConsole.println("verify: " + verify);
		TestConsole.println();
		Assertions.assertTrue(verify);
	}
}
