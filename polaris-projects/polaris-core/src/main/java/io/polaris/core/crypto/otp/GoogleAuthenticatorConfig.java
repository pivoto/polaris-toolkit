package io.polaris.core.crypto.otp;

import java.util.concurrent.TimeUnit;

public class GoogleAuthenticatorConfig {
	private long timeStepSizeInMillis = TimeUnit.SECONDS.toMillis(30);
	private int windowSize = 3;
	private int codeDigits = 6;
	private int numberOfScratchCodes = 5;
	private int keyModulus = (int) Math.pow(10, codeDigits);
	private int secretBits = 160;
	private OtpHmacHashAlgorithm hmacHash = OtpHmacHashAlgorithm.HmacSHA1;

	/**
	 * Returns the key module.
	 *
	 * @return the key module.
	 */
	public int getKeyModulus() {
		return keyModulus;
	}


	/**
	 * Returns the number of digits in the generated code.
	 *
	 * @return the number of digits in the generated code.
	 */
	public int getCodeDigits() {
		return codeDigits;
	}

	/**
	 * Returns the number of scratch codes to generate.  We are using Google's default of providing 5 scratch codes.
	 *
	 * @return the number of scratch codes to generate.
	 */
	public int getNumberOfScratchCodes() {
		return numberOfScratchCodes;
	}

	/**
	 * Returns the time step size, in milliseconds, as specified by RFC 6238.
	 * The default value is 30.000.
	 *
	 * @return the time step size in milliseconds.
	 */
	public long getTimeStepSizeInMillis() {
		return timeStepSizeInMillis;
	}

	/**
	 * Returns an integer value representing the number of windows of size
	 * timeStepSizeInMillis that are checked during the validation process,
	 * to account for differences between the server and the client clocks.
	 * The bigger the window, the more tolerant the library code is about
	 * clock skews.
	 * <p>
	 * We are using Google's default behaviour of using a window size equal
	 * to 3.  The limit on the maximum window size, present in older
	 * versions of this library, has been removed.
	 *
	 * @return the window size.
	 * @see #timeStepSizeInMillis
	 */
	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * Returns the number of bits of the secret keys to generate.  The length
	 * should always be a multiple of 8.  The default value is 160 bits, and
	 * a value smaller than 128 is disallowed, as recommended by RFC 4226 §4.
	 *
	 * @return the secret size in bits.
	 */
	public int getSecretBits() {
		return secretBits;
	}

	/**
	 * Returns the cryptographic hash function used to calculate the HMAC (Hash-based
	 * Message Authentication Code). This implementation uses the SHA1 hash
	 * function by default.
	 * <p>
	 *
	 * @return the HMAC hash function.
	 */
	public OtpHmacHashAlgorithm getHmacHash() {
		return hmacHash;
	}

	public static GoogleAuthenticatorConfigBuilder builder() {
		return new GoogleAuthenticatorConfigBuilder();
	}

	public static class GoogleAuthenticatorConfigBuilder {
		private GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig();

		public GoogleAuthenticatorConfig build() {
			return config;
		}

		public GoogleAuthenticatorConfigBuilder codeDigits(int codeDigits) {
			if (codeDigits <= 0) {
				throw new IllegalArgumentException("Code digits must be positive.");
			}

			if (codeDigits < 6) {
				throw new IllegalArgumentException("The minimum number of digits is 6.");
			}

			if (codeDigits > 8) {
				throw new IllegalArgumentException("The maximum number of digits is 8.");
			}

			config.codeDigits = codeDigits;
			config.keyModulus = (int) Math.pow(10, codeDigits);
			return this;
		}

		public GoogleAuthenticatorConfigBuilder numberOfScratchCodes(int numberOfScratchCodes) {
			if (numberOfScratchCodes < 0) {
				throw new IllegalArgumentException("The number of scratch codes must not be negative");
			}

			if (numberOfScratchCodes > 1_000) {
				throw new IllegalArgumentException("The maximum number of scratch codes is 1000");
			}

			config.numberOfScratchCodes = numberOfScratchCodes;
			return this;
		}

		public GoogleAuthenticatorConfigBuilder timeStepSizeInMillis(long timeStepSizeInMillis) {
			if (timeStepSizeInMillis <= 0) {
				throw new IllegalArgumentException("Time step size must be positive.");
			}

			config.timeStepSizeInMillis = timeStepSizeInMillis;
			return this;
		}

		public GoogleAuthenticatorConfigBuilder windowSize(int windowSize) {
			if (windowSize <= 0) {
				throw new IllegalArgumentException("Window number must be positive.");
			}

			config.windowSize = windowSize;
			return this;
		}

		public GoogleAuthenticatorConfigBuilder secretBits(int secretBits) {
			if (secretBits < 128) {
				throw new IllegalArgumentException("Secret bits must be greater than or equal to 128.");
			}

			if (secretBits % 8 != 0) {
				throw new IllegalArgumentException("Secret bits must be a multiple of 8.");
			}

			config.secretBits = secretBits;
			return this;
		}

		public GoogleAuthenticatorConfigBuilder hmacHash(OtpHmacHashAlgorithm hmacHash) {
			if (hmacHash == null) {
				throw new IllegalArgumentException("HMAC Hash Function cannot be null.");
			}

			config.hmacHash = hmacHash;
			return this;
		}
	}
}
