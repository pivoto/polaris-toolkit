package io.polaris.core.crypto.otp;

import io.polaris.core.codec.Base32;
import io.polaris.core.codec.Base64;
import io.polaris.core.string.Hex;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Supplier;

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
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		GoogleAuthenticatorKey key = gAuth.createCredentials("Apollo");
		System.out.printf("key: %s\n", key.getKey());
		System.out.printf("scratchCodes: %s\n", key.getScratchCodes());
		System.out.printf("verificationCode: %s\n", key.getVerificationCode());
		System.out.printf("config: %s\n", key.getConfig());

		String url = GoogleAuthenticatorQRGenerator.getOtpAuthUrl("Apollo", "apollo@163.com", key);
		System.out.printf("url: %s\n", url);
		System.out.printf("url: %s\n", GoogleAuthenticatorQRGenerator.getOtpAuthQrUrl("Apollo", "apollo@163.com", key));

		int passwd = gAuth.getTotpPassword(key.getKey());
		System.out.printf("passwd: %s\n", passwd);
	}

	@Test
	void testVerify() throws InvalidKeyException {
		String KEY = "W3C5B3WKR4AUKFVWYU2WNMYB756OAKWY";
		System.out.println(Base64.encodeToString(Base32.decode(KEY)));
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		System.out.println(gAuth.getTotpPassword(KEY, 1567631536000L-30000));
		System.out.println(gAuth.getTotpPassword(KEY, 1567631536000L));
		System.out.println(gAuth.getTotpPassword(KEY, 1567631536000L+30000));

		TimeBasedOneTimePasswordGenerator totp = new TimeBasedOneTimePasswordGenerator();
		System.out.println(totp.generate(KEY, Instant.ofEpochMilli(1567631536000L-30000)));
		System.out.println(totp.generate(KEY, Instant.ofEpochMilli(1567631536000L)));
		System.out.println(totp.generate(KEY, Instant.ofEpochMilli(1567631536000L+30000)));
	}


	@Test
	void test03() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(1567631536000L);
		buffer.position(0);
		System.out.println(buffer.position());
		System.out.println(Hex.formatHex(buffer.getInt()));
		System.out.println(buffer.position());
		System.out.println(Hex.formatHex(buffer.getInt()));
		System.out.println(Hex.formatHex((int)(1567631536000L>>>32)));
		System.out.println(Hex.formatHex((int)1567631536000L));
	}
}
