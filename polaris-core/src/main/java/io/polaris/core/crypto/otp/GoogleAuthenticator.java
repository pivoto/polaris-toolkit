package io.polaris.core.crypto.otp;

import io.polaris.core.codec.Base32;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This class implements the functionality described in RFC 6238 (TOTP: Time
 * based one-time password algorithm) and has been tested again Google's
 * implementation of such algorithm in its Google Authenticator application.
 * <p>
 * This class lets users create a new 16-bit base32-encoded secret key with
 * the validation code calculated at {@code time = 0} (the UNIX epoch) and the
 * URL of a Google-provided QR barcode to let an user load the generated
 * information into Google Authenticator.
 * <p>
 * The random number generator used by this class uses the default algorithm and
 * provider.  Users can override them by setting the following system properties
 * to the algorithm and provider name of their choice:
 * <ul>
 * <li>{@link #RNG_ALGORITHM_KEY}.</li>
 * <li>{@link #RNG_ALGORITHM_PROVIDER_KEY}.</li>
 * </ul>
 * <p>
 * This class does not store in any way either the generated keys nor the keys
 * passed during the authorization process.
 * <p>
 * Java Server side class for Google Authenticator's TOTP generator was inspired
 * by an author's blog post.
 *
 * @author Enrico M. Crisostomo
 * @author Warren Strange
 * @see <a href="http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html"></a>
 * @see <a href="http://code.google.com/p/google-authenticator"></a>
 * @see <a href="http://tools.ietf.org/id/draft-mraihi-totp-timebased-06.txt"></a>
 */
public final class GoogleAuthenticator implements IGoogleAuthenticator {
	private static final ILogger log = ILoggers.of(GoogleAuthenticator.class);
	public static final String RNG_ALGORITHM_KEY = "googleauth.rng.algorithm";
	public static final String RNG_ALGORITHM_PROVIDER_KEY = "googleauth.rng.algorithmProvider";
	private static final String DEFAULT_RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";
	private static final String DEFAULT_RANDOM_NUMBER_ALGORITHM_PROVIDER = "SUN";

	/**
	 * Number of digits of a scratch code represented as a decimal integer.
	 */
	private static final int SCRATCH_CODE_LENGTH = 8;
	/**
	 * Modulus used to truncate the scratch code.
	 */
	public static final int SCRATCH_CODE_MODULUS = (int) Math.pow(10, SCRATCH_CODE_LENGTH);
	/**
	 * Magic number representing an invalid scratch code.
	 */
	private static final int SCRATCH_CODE_INVALID = -1;
	/**
	 * Length in bytes of each scratch code. We're using Google's default of
	 * using 4 bytes per scratch code.
	 */
	private static final int BYTES_PER_SCRATCH_CODE = 4;
	/**
	 * The configuration used by the current instance.
	 */
	private final GoogleAuthenticatorConfig config;

	/**
	 * The internal SecureRandom instance used by this class.  Since Java 7
	 * {@link Random} instances are required to be thread-safe, no synchronisation is
	 * required in the methods of this class using this instance.  Thread-safety
	 * of this class was a de-facto standard in previous versions of Java so
	 * that it is expected to work correctly in previous versions of the Java
	 * platform as well.
	 */
	private ReseedingSecureRandom secureRandom;

	public GoogleAuthenticator() {
		config = new GoogleAuthenticatorConfig();
		this.secureRandom = new ReseedingSecureRandom(
			getRandomNumberAlgorithm(),
			getRandomNumberAlgorithmProvider());
	}

	public GoogleAuthenticator(GoogleAuthenticatorConfig config) {
		if (config == null) {
			throw new IllegalArgumentException("Configuration cannot be null.");
		}
		this.config = config;
		this.secureRandom = new ReseedingSecureRandom(
			getRandomNumberAlgorithm(),
			getRandomNumberAlgorithmProvider()
		);
	}

	public GoogleAuthenticator(final String randomNumberAlgorithm, final String randomNumberAlgorithmProvider) {
		this(new GoogleAuthenticatorConfig(), randomNumberAlgorithm, randomNumberAlgorithmProvider);
	}

	public GoogleAuthenticator(GoogleAuthenticatorConfig config, final String randomNumberAlgorithm, final String randomNumberAlgorithmProvider) {
		if (config == null) {
			throw new IllegalArgumentException("Configuration cannot be null.");
		}

		this.config = config;

		if (randomNumberAlgorithm == null && randomNumberAlgorithmProvider == null) {
			this.secureRandom = new ReseedingSecureRandom();
		} else if (randomNumberAlgorithm == null) {
			throw new IllegalArgumentException("RandomNumberAlgorithm must not be null. If the RandomNumberAlgorithm is null, the RandomNumberAlgorithmProvider must also be null.");
		} else if (randomNumberAlgorithmProvider == null) {
			this.secureRandom = new ReseedingSecureRandom(randomNumberAlgorithm);
		}
	}

	private String getRandomNumberAlgorithm() {
		return System.getProperty(RNG_ALGORITHM_KEY, DEFAULT_RANDOM_NUMBER_ALGORITHM);
	}

	private String getRandomNumberAlgorithmProvider() {
		return System.getProperty(RNG_ALGORITHM_PROVIDER_KEY, DEFAULT_RANDOM_NUMBER_ALGORITHM_PROVIDER);
	}

	/**
	 * Calculates the verification code of the provided key at the specified
	 * instant of time using the algorithm specified in RFC 6238.
	 *
	 * @param key the secret key in binary format.
	 * @param tm  the instant of time.
	 * @return the validation code for the provided key at the specified instant
	 * of time.
	 */
	int calculateCode(byte[] key, long tm) {
		// Allocating an array of bytes to represent the specified instant of time.
		byte[] data = new byte[8];
		long value = tm;
		// Converting the instant of time from the long representation to a big-endian array of bytes (RFC4226, 5.2. Description).
		for (int i = 8; i-- > 0; value >>>= 8) {
			data[i] = (byte) value;
		}
		// Building the secret key specification for the HmacSHA1 algorithm.
		SecretKeySpec signKey = new SecretKeySpec(key, config.getHmacHash().getHmacAlgorithm());
		try {
			// Getting an HmacSHA1/HmacSHA256 algorithm implementation from the JCE.
			Mac mac = Mac.getInstance(config.getHmacHash().getHmacAlgorithm());
			// Initializing the MAC algorithm.
			mac.init(signKey);
			// Processing the instant of time and getting the encrypted data.
			byte[] hash = mac.doFinal(data);
			// Building the validation code performing dynamic truncation (RFC4226, 5.3. Generating an HOTP value)
			int offset = hash[hash.length - 1] & 0xF;
			// We are using a long because Java hasn't got an unsigned integer type and we need 32 unsigned bits).
			long truncatedHash = 0;

			for (int i = 0; i < 4; ++i) {
				truncatedHash <<= 8;
				// Java bytes are signed but we need an unsigned integer: cleaning off all but the LSB.
				truncatedHash |= (hash[offset + i] & 0xFF);
			}
			// Clean bits higher than the 32nd (inclusive) and calculate the module with the maximum validation code value.
			truncatedHash &= 0x7FFFFFFF;
			truncatedHash %= config.getKeyModulus();
			// Returning the validation code to the caller.
			return (int) truncatedHash;
		} catch (NoSuchAlgorithmException | InvalidKeyException ex) {
			// Logging the exception.
			log.error(ex.getMessage(), ex);
			// We're not disclosing internal error details to our clients.
			throw new GoogleAuthenticatorException("The operation cannot be performed now.");
		}
	}

	private long getTimeWindowFromTime(long time) {
		return time / this.config.getTimeStepSizeInMillis();
	}

	/**
	 * This method implements the algorithm specified in RFC 6238 to check if a
	 * validation code is valid in a given instant of time for the given secret
	 * key.
	 *
	 * @param secret    the Base32 encoded secret key.
	 * @param code      the code to validate.
	 * @param timestamp the instant of time to use during the validation process.
	 * @param window    the window size to use during the validation process.
	 * @return <code>true</code> if the validation code is valid,
	 * <code>false</code> otherwise.
	 */
	private boolean checkCode(String secret, long code, long timestamp, int window) {
		byte[] decodedKey = decodeSecret(secret);

		// convert unix time into a 30 second "window" as specified by the
		// TOTP specification. Using Google's default interval of 30 seconds.
		final long timeWindow = getTimeWindowFromTime(timestamp);

		// Calculating the verification code of the given key in each of the
		// time intervals and returning true if the provided code is equal to
		// one of them.
		for (int i = -((window - 1) / 2); i <= window / 2; ++i) {
			// Calculating the verification code for the current time interval.
			long hash = calculateCode(decodedKey, timeWindow + i);

			// Checking if the provided code is equal to the calculated one.
			if (hash == code) {
				// The verification code is valid.
				return true;
			}
		}

		// The verification code is invalid.
		return false;
	}

	private byte[] decodeSecret(String secret) {
		return Base32.decode(secret);
	}

	@Override
	public GoogleAuthenticatorKey createCredentials() {
		// Allocating a buffer sufficiently large to hold the bytes required by
		// the secret key.
		int bufferSize = config.getSecretBits() / 8;
		byte[] buffer = new byte[bufferSize];

		secureRandom.nextBytes(buffer);

		// Extracting the bytes making up the secret key.
		byte[] secretKey = Arrays.copyOf(buffer, bufferSize);
		String generatedKey = calculateSecretKey(secretKey);

		// Generating the verification code at time = 0.
		int validationCode = calculateValidationCode(secretKey);

		// Calculate scratch codes
		List<Integer> scratchCodes = calculateScratchCodes();

		return new GoogleAuthenticatorKey
			.Builder(generatedKey)
			.setConfig(config)
			.setVerificationCode(validationCode)
			.setScratchCodes(scratchCodes)
			.build();
	}

	@Override
	public GoogleAuthenticatorKey createCredentials(String userName) {
		// Further validation will be performed by the configured provider.
		if (userName == null) {
			throw new IllegalArgumentException("User name cannot be null.");
		}
		GoogleAuthenticatorKey key = createCredentials();
		return key;
	}

	private List<Integer> calculateScratchCodes() {
		final List<Integer> scratchCodes = new ArrayList<>();
		for (int i = 0; i < config.getNumberOfScratchCodes(); ++i) {
			scratchCodes.add(generateScratchCode());
		}
		return scratchCodes;
	}

	/**
	 * This method calculates a scratch code from a random byte buffer of
	 * suitable size <code>#BYTES_PER_SCRATCH_CODE</code>.
	 *
	 * @param scratchCodeBuffer a random byte buffer whose minimum size is
	 *                          <code>#BYTES_PER_SCRATCH_CODE</code>.
	 * @return the scratch code.
	 */
	private int calculateScratchCode(byte[] scratchCodeBuffer) {
		if (scratchCodeBuffer.length < BYTES_PER_SCRATCH_CODE) {
			throw new IllegalArgumentException(
				String.format(
					"The provided random byte buffer is too small: %d.",
					scratchCodeBuffer.length));
		}

		int scratchCode = 0;

		for (int i = 0; i < BYTES_PER_SCRATCH_CODE; ++i) {
			scratchCode = (scratchCode << 8) + (scratchCodeBuffer[i] & 0xff);
		}

		scratchCode = (scratchCode & 0x7FFFFFFF) % SCRATCH_CODE_MODULUS;

		// Accept the scratch code only if it has exactly
		// SCRATCH_CODE_LENGTH digits.
		if (validateScratchCode(scratchCode)) {
			return scratchCode;
		} else {
			return SCRATCH_CODE_INVALID;
		}
	}

	boolean validateScratchCode(int scratchCode) {
		return (scratchCode >= SCRATCH_CODE_MODULUS / 10);
	}

	/**
	 * This method creates a new random byte buffer from which a new scratch
	 * code is generated. This function is invoked if a scratch code generated
	 * from the main buffer is invalid because it does not satisfy the scratch
	 * code restrictions.
	 *
	 * @return A valid scratch code.
	 */
	private int generateScratchCode() {
		while (true) {
			byte[] scratchCodeBuffer = new byte[BYTES_PER_SCRATCH_CODE];
			secureRandom.nextBytes(scratchCodeBuffer);

			int scratchCode = calculateScratchCode(scratchCodeBuffer);

			if (scratchCode != SCRATCH_CODE_INVALID) {
				return scratchCode;
			}
		}
	}

	/**
	 * This method calculates the validation code at time 0.
	 *
	 * @param secretKey The secret key to use.
	 * @return the validation code at time 0.
	 */
	private int calculateValidationCode(byte[] secretKey) {
		return calculateCode(secretKey, 0);
	}


	public int getTotpPassword(String secret) {
		return getTotpPassword(secret, System.currentTimeMillis());
	}

	public int getTotpPassword(String secret, long time) {
		return calculateCode(decodeSecret(secret), getTimeWindowFromTime(time));
	}


	/**
	 * This method calculates the secret key given a random byte buffer.
	 *
	 * @param secretKey a random byte buffer.
	 * @return the secret key.
	 */
	private String calculateSecretKey(byte[] secretKey) {
		return Base32.encodeToString(secretKey);
	}

	@Override
	public boolean authorize(String secret, int verificationCode) {
		return authorize(secret, verificationCode, System.currentTimeMillis());
	}

	@Override
	public boolean authorize(String secret, int verificationCode, long time) {
		// Checking user input and failing if the secret key was not provided.
		if (secret == null) {
			throw new IllegalArgumentException("Secret cannot be null.");
		}

		// Checking if the verification code is between the legal bounds.
		if (verificationCode <= 0 || verificationCode >= this.config.getKeyModulus()) {
			return false;
		}

		// Checking the validation code using the current UNIX time.
		return checkCode(
			secret,
			verificationCode,
			time,
			this.config.getWindowSize());
	}

}
