package io.polaris.core.crypto.digest;

import javax.annotation.Nonnull;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Qt
 * @since 1.8
 */
public class Hmacs {
	private static final int STREAM_BUFFER_LENGTH = 1024;

	@Nonnull
	public static Mac getMac(String algorithm) throws NoSuchAlgorithmException {
		return Mac.getInstance(algorithm);
	}

	@Nonnull
	public static Mac getInitializedMac(String algorithm, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = getMac(algorithm);
		SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
		mac.init(keySpec);
		return mac;
	}

	public static byte[] hmac(Mac mac, byte[] data) {
		return mac.doFinal(data);
	}

	public static byte[] hmac(Mac mac, ByteBuffer data) {
		mac.update(data);
		return mac.doFinal();
	}

	public static byte[] hmac(Mac mac, InputStream in) throws IOException {
		byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
		for (int read = in.read(buffer, 0, STREAM_BUFFER_LENGTH); read > -1; read = in.read(buffer, 0, STREAM_BUFFER_LENGTH)) {
			mac.update(buffer, 0, read);
		}
		return mac.doFinal();
	}

	public static Mac updateHmac(Mac mac, byte[] data) {
		mac.update(data);
		return mac;
	}

	public static Mac updateHmac(Mac mac, ByteBuffer data) {
		mac.update(data);
		return mac;
	}

	public static Mac updateHmac(Mac mac, InputStream in) throws IOException {
		byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
		for (int read = in.read(buffer, 0, STREAM_BUFFER_LENGTH); read > -1; read = in.read(buffer, 0, STREAM_BUFFER_LENGTH)) {
			mac.update(buffer, 0, read);
		}
		return mac;
	}

	public static Mac getHmacMd5(byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		return getInitializedMac(HmacAlgorithm.HmacMD5.code(), key);
	}

	public static Mac getHmacSha1(byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		return getInitializedMac(HmacAlgorithm.HmacSHA1.code(), key);
	}

	public static Mac getHmacSha256(byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		return getInitializedMac(HmacAlgorithm.HmacSHA256.code(), key);
	}

	public static Mac getHmacSha384(byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		return getInitializedMac(HmacAlgorithm.HmacSHA384.code(), key);
	}

	public static Mac getHmacSha512(byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		return getInitializedMac(HmacAlgorithm.HmacSHA512.code(), key);
	}


	public static byte[] hmacMd5(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacMd5(key), data);
	}


	public static byte[] hmacSha1(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacSha1(key), data);
	}

	public static byte[] hmacSha256(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacSha256(key), data);
	}

	public static byte[] hmacSha384(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacSha384(key), data);
	}

	public static byte[] hmacSha512(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacSha512(key), data);
	}


	public static byte[] hmacMd5(byte[] key, ByteBuffer data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacMd5(key), data);
	}


	public static byte[] hmacSha1(byte[] key, ByteBuffer data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacSha1(key), data);
	}

	public static byte[] hmacSha256(byte[] key, ByteBuffer data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacSha256(key), data);
	}

	public static byte[] hmacSha384(byte[] key, ByteBuffer data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacSha384(key), data);
	}

	public static byte[] hmacSha512(byte[] key, ByteBuffer data) throws NoSuchAlgorithmException, InvalidKeyException {
		return hmac(getHmacSha512(key), data);
	}


	public static byte[] hmacMd5(byte[] key, InputStream data) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		return hmac(getHmacMd5(key), data);
	}


	public static byte[] hmacSha1(byte[] key, InputStream data) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		return hmac(getHmacSha1(key), data);
	}

	public static byte[] hmacSha256(byte[] key, InputStream data) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		return hmac(getHmacSha256(key), data);
	}

	public static byte[] hmacSha384(byte[] key, InputStream data) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		return hmac(getHmacSha384(key), data);
	}

	public static byte[] hmacSha512(byte[] key, InputStream data) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		return hmac(getHmacSha512(key), data);
	}
}
