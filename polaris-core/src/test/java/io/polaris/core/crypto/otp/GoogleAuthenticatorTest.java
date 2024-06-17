package io.polaris.core.crypto.otp;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Supplier;

import io.polaris.core.TestConsole;
import io.polaris.core.codec.Base32;
import io.polaris.core.codec.Base64;
import io.polaris.core.string.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GoogleAuthenticatorTest {

	public static void main(String[] args) {
		String KEY = "TFWP24CNYGMA5QSPQ7CBODYEKIL32ZEM";
		GoogleAuthenticator gAuth = new GoogleAuthenticator();

		Supplier<String> generator = () -> {
			long now = System.currentTimeMillis();
			int pre = gAuth.getTotpPassword(KEY, now - 30000);
			int curr = gAuth.getTotpPassword(KEY, now);
			int post = gAuth.getTotpPassword(KEY, now + 30000);
			return Arrays.asList(pre, curr, post).toString();
		};
		System.out.printf("passwd: %s\n", generator.get());
		Scanner scanner = new Scanner(System.in);
		System.out.print("Input> ");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			if ("exit".equalsIgnoreCase(line)) {
				break;
			}
			try {
				int code = Integer.parseInt(line);
				boolean authorized = gAuth.authorize(KEY, code);
				System.out.printf("authorized: %s, passwd: %s\n", authorized, generator.get());
			} catch (NumberFormatException e) {
				System.err.println(e.getClass().getName() + "> " + e.getMessage());
			}
			System.out.print("Input> ");
		}
	}

	@Test
	void testGen() {
		GoogleAuthenticatorConfig config = GoogleAuthenticatorConfig.builder()
			.numberOfScratchCodes(10)
			.build();

		GoogleAuthenticator gAuth = new GoogleAuthenticator(config);
		GoogleAuthenticatorKey key = gAuth.createCredentials("Apollo");
		TestConsole.println("key: {}", key.getKey());
		TestConsole.println("scratchCodes: {}", key.getScratchCodes());
		TestConsole.println("verificationCode: {}", key.getVerificationCode());
		TestConsole.println("config: {}", key.getConfig());

		String url = GoogleAuthenticatorQRGenerator.getOtpAuthUrl("Apollo", "apollo@163.com", key);
		TestConsole.println("url: {}", url);
		TestConsole.println("url: {}", GoogleAuthenticatorQRGenerator.getOtpAuthQrUrl("Apollo", "apollo@163.com", key));

		int passwd = gAuth.getTotpPassword(key.getKey());
		TestConsole.println("passwd: {}", passwd);
		Assertions.assertTrue(gAuth.authorize(key.getKey(), passwd));
		Assertions.assertEquals(10, key.getScratchCodes().size());

	}

	@Test
	void testVerify() throws InvalidKeyException {
		String KEY = "W3C5B3WKR4AUKFVWYU2WNMYB756OAKWY";
		TestConsole.println(Base64.encodeToString(Base32.decode(KEY)));
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		TestConsole.println(gAuth.getTotpPassword(KEY, 1567631536000L - 30000));
		TestConsole.println(gAuth.getTotpPassword(KEY, 1567631536000L));
		TestConsole.println(gAuth.getTotpPassword(KEY, 1567631536000L + 30000));
		Assertions.assertEquals(38525, gAuth.getTotpPassword(KEY, 1567631536000L - 30000));
		Assertions.assertEquals(82371, gAuth.getTotpPassword(KEY, 1567631536000L));
		Assertions.assertEquals(830845, gAuth.getTotpPassword(KEY, 1567631536000L + 30000));

		TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();
		Assertions.assertEquals(38525, totp.generate(KEY, Instant.ofEpochMilli(1567631536000L - 30000)));
		Assertions.assertEquals(82371, totp.generate(KEY, Instant.ofEpochMilli(1567631536000L)));
		Assertions.assertEquals(830845, totp.generate(KEY, Instant.ofEpochMilli(1567631536000L + 30000)));
	}


	@Test
	void test03() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(1567631536000L);
		buffer.position(0);
		TestConsole.println(buffer.position());
		TestConsole.println(Hex.formatHex(buffer.getInt()));
		TestConsole.println(buffer.position());
		TestConsole.println(Hex.formatHex(buffer.getInt()));
		TestConsole.println(Hex.formatHex((int) (1567631536000L >>> 32)));
		TestConsole.println(Hex.formatHex((int) 1567631536000L));
	}
}
