package io.polaris.core.crypto.digest;

import javax.crypto.Mac;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Qt
 * @since 1.8
 */
public class Hmac {
	private final Mac mac;

	public Hmac(Mac mac) {
		this.mac = mac;
	}

	public Hmac(String algorithm, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		this(Hmacs.getInitializedMac(algorithm, key));
	}


	public Hmac update(byte[] bytes) throws IllegalStateException {
		mac.update(bytes);
		return this;
	}

	public Hmac update(ByteBuffer byteBuffer) {
		mac.update(byteBuffer);
		return this;
	}


	public Hmac update(InputStream data) throws IOException {
		Hmacs.updateHmac(mac, data);
		return this;
	}

	public byte[] doFinal() throws IllegalStateException {
		return mac.doFinal();
	}

	public byte[] doFinal(byte[] bytes) throws IllegalStateException {
		return mac.doFinal(bytes);
	}

	public byte[] doFinal(ByteBuffer byteBuffer) throws IllegalStateException {
		return update(byteBuffer).doFinal();
	}

	public Mac getMac() {
		return mac;
	}
}
