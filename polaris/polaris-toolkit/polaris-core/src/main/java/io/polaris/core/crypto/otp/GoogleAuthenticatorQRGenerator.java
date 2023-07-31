package io.polaris.core.crypto.otp;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.net.URIBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class GoogleAuthenticatorQRGenerator {
	private static final String TOTP_URI_FORMAT =
		"https://api.qrserver.com/v1/create-qr-code/?data=%s&size=200x200&ecc=M&margin=10";

	private static String internalURLEncode(String s) {
		try {
			return URLEncoder.encode(s, StdConsts.UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("UTF-8 encoding is not supported by URLEncoder.", e);
		}
	}

	private static String formatLabel(String issuer, String accountName) {
		if (accountName == null || accountName.trim().length() == 0) {
			throw new IllegalArgumentException("Account name must not be empty.");
		}
		StringBuilder sb = new StringBuilder();
		if (issuer != null) {
			if (issuer.contains(":")) {
				throw new IllegalArgumentException("Issuer cannot contain the \':\' character.");
			}
			sb.append(issuer);
			sb.append(":");
		}
		sb.append(accountName);
		return sb.toString();
	}


	public static String getOtpAuthQrUrl(String issuer, String accountName, GoogleAuthenticatorKey credentials) {
		return String.format(TOTP_URI_FORMAT,
			internalURLEncode(getOtpAuthUrl(issuer, accountName, credentials)));
	}

	public static String getOtpAuthUrl(String issuer, String accountName, GoogleAuthenticatorKey credentials) {
		String secret = credentials.getKey();
		final GoogleAuthenticatorConfig config = credentials.getConfig();
		String algorithm = config.getHmacHash().getHashAlgorithm();
		int digits = config.getCodeDigits();
		int period = (int) (config.getTimeStepSizeInMillis() / 1000);
		return getOtpAuthUrl(issuer, accountName, secret, algorithm, digits, period);
	}


	public static String getOtpAuthQrUrl(String issuer, String accountName, String secret, String algorithm, int digits, int period) {
		return String.format(TOTP_URI_FORMAT,
			internalURLEncode(getOtpAuthUrl(issuer, accountName, secret, algorithm, digits, period)));
	}

	private static String getOtpAuthUrl(String issuer, String accountName, String secret, String algorithm, int digits, int period) {
		URIBuilder uri = new URIBuilder()
			.setScheme("otpauth")
			.setHost("totp")
			.setPath("/" + formatLabel(issuer, accountName))
			.setParameter("secret", secret);

		if (issuer != null) {
			if (issuer.contains(":")) {
				throw new IllegalArgumentException("Issuer cannot contain the \':\' character.");
			}
			uri.setParameter("issuer", issuer);
		}

		uri.setParameter("algorithm", algorithm);
		uri.setParameter("digits", String.valueOf(digits));
		uri.setParameter("period", String.valueOf(period));
		return uri.toString();
	}

}
