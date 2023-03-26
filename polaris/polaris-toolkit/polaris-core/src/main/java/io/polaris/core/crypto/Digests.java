package io.polaris.core.crypto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Qt
 * @since 1.8
 */
public class Digests {
	private static final int STREAM_BUFFER_LENGTH = 1024;

	public static MessageDigest getDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException var2) {
			throw new IllegalArgumentException(var2);
		}
	}

	public static byte[] digest(MessageDigest messageDigest, byte[] data) {
		return messageDigest.digest(data);
	}

	public static byte[] digest(MessageDigest messageDigest, ByteBuffer data) {
		messageDigest.update(data);
		return messageDigest.digest();
	}

	public static byte[] digest(MessageDigest messageDigest, File data) throws IOException {
		return updateDigest(messageDigest, data).digest();
	}

	public static MessageDigest updateDigest(MessageDigest digest, File data) throws IOException {
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(data));
		Throwable var3 = null;

		MessageDigest var4;
		try {
			var4 = updateDigest(digest, (InputStream) inputStream);
		} catch (Throwable var13) {
			var3 = var13;
			throw var13;
		} finally {
			if (inputStream != null) {
				if (var3 != null) {
					try {
						inputStream.close();
					} catch (Throwable var12) {
						var3.addSuppressed(var12);
					}
				} else {
					inputStream.close();
				}
			}

		}

		return var4;
	}

	public static MessageDigest updateDigest(MessageDigest digest, InputStream inputStream) throws IOException {
		byte[] buffer = new byte[STREAM_BUFFER_LENGTH];

		for (int read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH); read > -1; read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH)) {
			digest.update(buffer, 0, read);
		}

		return digest;
	}

	public static byte[] digest(MessageDigest messageDigest, Path data, OpenOption... options) throws IOException {
		return updateDigest(messageDigest, data, options).digest();
	}

	public static MessageDigest updateDigest(MessageDigest digest, Path path, OpenOption... options) throws IOException {
		BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(path, options));
		Throwable var4 = null;

		MessageDigest var5;
		try {
			var5 = updateDigest(digest, (InputStream) inputStream);
		} catch (Throwable var14) {
			var4 = var14;
			throw var14;
		} finally {
			if (inputStream != null) {
				if (var4 != null) {
					try {
						inputStream.close();
					} catch (Throwable var13) {
						var4.addSuppressed(var13);
					}
				} else {
					inputStream.close();
				}
			}

		}

		return var5;
	}

	public static byte[] digest(MessageDigest messageDigest, RandomAccessFile data) throws IOException {
		return updateDigest(messageDigest, data).digest();
	}

	public static MessageDigest updateDigest(MessageDigest digest, RandomAccessFile data) throws IOException {
		return updateDigest(digest, data.getChannel());
	}

	private static MessageDigest updateDigest(MessageDigest digest, FileChannel data) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(STREAM_BUFFER_LENGTH);

		while (data.read(buffer) > 0) {
			buffer.flip();
			digest.update(buffer);
			buffer.clear();
		}

		return digest;
	}

	public static boolean isAvailable(String messageDigestAlgorithm) {
		return getDigest(messageDigestAlgorithm, (MessageDigest) null) != null;
	}

	public static MessageDigest getDigest(String algorithm, MessageDigest defaultMessageDigest) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (Exception var3) {
			return defaultMessageDigest;
		}
	}

	public static byte[] md2(InputStream data) throws IOException {
		return digest(getMd2Digest(), data);
	}

	public static byte[] digest(MessageDigest messageDigest, InputStream data) throws IOException {
		return updateDigest(messageDigest, data).digest();
	}

	public static MessageDigest getMd2Digest() {
		return getDigest("MD2");
	}

	public static byte[] md2(String data) {
		return md2(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] md2(byte[] data) {
		return getMd2Digest().digest(data);
	}

	public static byte[] md5(InputStream data) throws IOException {
		return digest(getMd5Digest(), data);
	}

	public static MessageDigest getMd5Digest() {
		return getDigest("MD5");
	}

	public static byte[] md5(String data) {
		return md5(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] md5(byte[] data) {
		return getMd5Digest().digest(data);
	}

	public static byte[] sha1(InputStream data) throws IOException {
		return digest(getSha1Digest(), data);
	}

	public static MessageDigest getSha1Digest() {
		return getDigest("SHA-1");
	}

	public static byte[] sha1(String data) {
		return sha1(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha1(byte[] data) {
		return getSha1Digest().digest(data);
	}

	public static byte[] sha256(InputStream data) throws IOException {
		return digest(getSha256Digest(), data);
	}

	public static MessageDigest getSha256Digest() {
		return getDigest("SHA-256");
	}

	public static byte[] sha256(String data) {
		return sha256(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha256(byte[] data) {
		return getSha256Digest().digest(data);
	}

	public static byte[] sha3_224(InputStream data) throws IOException {
		return digest(getSha3_224Digest(), data);
	}

	public static MessageDigest getSha3_224Digest() {
		return getDigest("SHA3-224");
	}

	public static byte[] sha3_224(String data) {
		return sha3_224(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha3_224(byte[] data) {
		return getSha3_224Digest().digest(data);
	}

	public static byte[] sha3_256(InputStream data) throws IOException {
		return digest(getSha3_256Digest(), data);
	}

	public static MessageDigest getSha3_256Digest() {
		return getDigest("SHA3-256");
	}

	public static byte[] sha3_256(String data) {
		return sha3_256(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha3_256(byte[] data) {
		return getSha3_256Digest().digest(data);
	}

	public static byte[] sha3_384(InputStream data) throws IOException {
		return digest(getSha3_384Digest(), data);
	}

	public static MessageDigest getSha3_384Digest() {
		return getDigest("SHA3-384");
	}

	public static byte[] sha3_384(String data) {
		return sha3_384(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha3_384(byte[] data) {
		return getSha3_384Digest().digest(data);
	}

	public static byte[] sha3_512(InputStream data) throws IOException {
		return digest(getSha3_512Digest(), data);
	}

	public static MessageDigest getSha3_512Digest() {
		return getDigest("SHA3-512");
	}

	public static byte[] sha3_512(String data) {
		return sha3_512(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha3_512(byte[] data) {
		return getSha3_512Digest().digest(data);
	}

	public static byte[] sha384(InputStream data) throws IOException {
		return digest(getSha384Digest(), data);
	}

	public static MessageDigest getSha384Digest() {
		return getDigest("SHA-384");
	}

	public static byte[] sha384(String data) {
		return sha384(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha384(byte[] data) {
		return getSha384Digest().digest(data);
	}

	public static byte[] sha512(InputStream data) throws IOException {
		return digest(getSha512Digest(), data);
	}

	public static MessageDigest getSha512Digest() {
		return getDigest("SHA-512");
	}

	public static byte[] sha512(String data) {
		return sha512(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha512(byte[] data) {
		return getSha512Digest().digest(data);
	}

	public static byte[] sha512_224(InputStream data) throws IOException {
		return digest(getSha512_224Digest(), data);
	}

	public static MessageDigest getSha512_224Digest() {
		return getDigest("SHA-512/224");
	}

	public static byte[] sha512_224(String data) {
		return sha512_224(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha512_224(byte[] data) {
		return getSha512_224Digest().digest(data);
	}

	public static byte[] sha512_256(InputStream data) throws IOException {
		return digest(getSha512_256Digest(), data);
	}

	public static MessageDigest getSha512_256Digest() {
		return getDigest("SHA-512/256");
	}

	public static byte[] sha512_256(String data) {
		return sha512_256(data.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] sha512_256(byte[] data) {
		return getSha512_256Digest().digest(data);
	}

	public static MessageDigest updateDigest(MessageDigest messageDigest, String valueToDigest) {
		messageDigest.update(valueToDigest.getBytes(StandardCharsets.UTF_8));
		return messageDigest;
	}


}
