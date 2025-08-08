package io.polaris.core.crypto.otp;

/**
 * Google Authenticator library interface.
 *
 * @see <a href="https://github.com/wstrange/GoogleAuth">GoogleAuth</a>
 */
public interface Authenticator {
	/**
	 * This method generates a new set of credentials including:
	 * <ol>
	 * <li>Secret key.</li>
	 * <li>Validation code.</li>
	 * <li>A list of scratch codes.</li>
	 * </ol>
	 * <p>
	 * The user must register this secret on their device.
	 *
	 * @return secret key
	 */
	AuthenticatorKey createCredentials();

	/**
	 * This method generates a new set of credentials invoking the
	 * <code>#createCredentials</code> method with no arguments. The generated
	 * credentials are then saved using the configured
	 * <code>#ICredentialRepository</code> service.
	 * <p>
	 * The user must register this secret on their device.
	 *
	 * @param userName the user name.
	 * @return secret key
	 */
	AuthenticatorKey createCredentials(String userName);

	/**
	 * This method generates the current TOTP password.
	 *
	 * @param secret the encoded secret key.
	 * @return the current TOTP password.
	 */
	int getTotpPassword(String secret);

	/**
	 * This method generates the TOTP password at the specified time.
	 *
	 * @param secret The encoded secret key.
	 * @param time   The time to use to calculate the password.
	 * @return the TOTP password at the specified time.
	 */
	int getTotpPassword(String secret, long time);



	/**
	 * Checks a verification code against a secret key using the current time.
	 *
	 * @param secret           the encoded secret key.
	 * @param verificationCode the verification code.
	 * @return <code>true</code> if the validation code is valid,
	 * <code>false</code> otherwise.
	 * @throws AuthenticatorException if a failure occurs during the
	 *                                      calculation of the validation code.
	 *                                      The only failures that should occur
	 *                                      are related with the cryptographic
	 *                                      functions provided by the JCE.
	 * @see #authorize(String, int, long)
	 */
	boolean authorize(String secret, int verificationCode);

	/**
	 * Checks a verification code against a secret key using the specified time.
	 * The algorithm also checks in a time window whose size determined by the
	 * {@code windowSize} property of this class.
	 * <p>
	 * The default value of 30 seconds recommended by RFC 6238 is used for the
	 * interval size.
	 *
	 * @param secret           The encoded secret key.
	 * @param verificationCode The verification code.
	 * @param time             The time to use to calculate the TOTP password..
	 * @return {@code true} if the validation code is valid, {@code false}
	 * otherwise.
	 * @throws AuthenticatorException if a failure occurs during the
	 *                                      calculation of the validation code.
	 *                                      The only failures that should occur
	 *                                      are related with the cryptographic
	 *                                      functions provided by the JCE.
	 */
	boolean authorize(String secret, int verificationCode, long time);


}
