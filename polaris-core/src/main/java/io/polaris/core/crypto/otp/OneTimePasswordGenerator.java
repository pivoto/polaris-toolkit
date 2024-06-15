package io.polaris.core.crypto.otp;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import io.polaris.core.codec.Base32;
import io.polaris.core.crypto.CryptoRuntimeException;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("ALL")
public class OneTimePasswordGenerator {
	private final Mac prototypeMac;
	private final int length;
	private final int modDivisor;

	public OneTimePasswordGenerator() {
		this(6);
	}

	public OneTimePasswordGenerator(int length) {
		this(length, OtpHmacHashAlgorithm.HmacSHA1);
	}

	public OneTimePasswordGenerator(int length, OtpHmacHashAlgorithm algorithm) {
		try {
			this.prototypeMac = Mac.getInstance(algorithm.getHmacAlgorithm());
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
		this.length = length;
		this.modDivisor = (int) Math.pow(10, length);
	}

	private Mac getMac() {
		try {
			// Cloning is generally cheaper than `Mac.getInstance`, but isn't GUARANTEED to be supported.
			return (Mac) this.prototypeMac.clone();
		} catch (CloneNotSupportedException e) {
			try {
				return Mac.getInstance(this.prototypeMac.getAlgorithm());
			} catch (final NoSuchAlgorithmException ex) {
				// This should be impossible
				throw new IllegalStateException(ex);
			}
		}
	}

	public byte[] generateKey() {
		return generateKey(20);
	}

	public String generateKeyString() {
		return generateKeyString(20);
	}

	public byte[] generateKey(int size) {
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			byte[] buffer = new byte[size];
			secureRandom.nextBytes(buffer);
			return buffer;
		} catch (NoSuchAlgorithmException e) {
			// This should be impossible
			throw new IllegalStateException(e);
		}
	}

	public String generateKeyString(int size) {
		return Base32.encodeToString(generateKey(size));
	}

	public int generate(String key, long data)  {
		return generate(Base32.decode(key), data);
	}

	public int generate(byte[] key, long data)  {
		return generate(new SecretKeySpec(key, getAlgorithm()), data);
	}

	public int generate(Key key, long data)  {
		Mac mac = getMac();
		ByteBuffer buffer = ByteBuffer.allocate(mac.getMacLength());
		buffer.putLong(0, data);
		try {
			byte[] array = buffer.array();
			mac.init(key);
			mac.update(array, 0, 8);
			mac.doFinal(array, 0);
			final int offset = buffer.get(buffer.capacity() - 1) & 0x0f;
			return (buffer.getInt(offset) & 0x7fffffff) % this.modDivisor;
		} catch (ShortBufferException e) {
			// This should be impossible
			throw new CryptoRuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new CryptoRuntimeException(e);
		}
	}


	public String generateString(String key, long data)  {
		return generateString(Base32.decode(key), data);
	}

	public String generateString(byte[] key, long data)  {
		return generateString(new SecretKeySpec(key, getAlgorithm()), data);
	}

	public String generateString(Key key, long data)  {
		String pwd = Integer.toString(generate(key, data));
		StringBuilder sb = new StringBuilder(this.length);
		int offset = (this.length - pwd.length());
		for (int i = 0; i < offset; i++) {
			sb.append('0');
		}
		sb.append(pwd);
		return sb.toString();
	}

	public int getLength() {
		return this.length;
	}

	public String getAlgorithm() {
		return this.prototypeMac.getAlgorithm();
	}
}
