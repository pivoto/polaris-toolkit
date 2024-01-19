package io.polaris.core.crypto.digest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.MessageDigest;

/**
 * @author Qt
 * @since 1.8
 */
public class Digest {
	private final MessageDigest messageDigest;

	public Digest(String algorithm)  {
		this(Digests.getDigest(algorithm));
	}

	public Digest(MessageDigest digest) {
		this.messageDigest = digest;
	}

	public byte[] digest(byte[] data) {
		return Digests.digest(this.messageDigest, data);
	}

	public byte[] digest(ByteBuffer data) {
		return Digests.digest(this.messageDigest, data);
	}


	public byte[] digest(File data) throws IOException {
		return Digests.updateDigest(this.messageDigest, data).digest();
	}


	public byte[] digest(InputStream data) throws IOException {
		return Digests.updateDigest(this.messageDigest, data).digest();
	}


	public byte[] digest(Path data, OpenOption... options) throws IOException {
		return Digests.updateDigest(this.messageDigest, data, options).digest();
	}


	public byte[] digest(String data) {
		return Digests.updateDigest(this.messageDigest, data).digest();
	}


	@SuppressWarnings("unused")
	public MessageDigest getMessageDigest() {
		return this.messageDigest;
	}
}
