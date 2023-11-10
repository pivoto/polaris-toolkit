package io.polaris.core.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * @author Qt
 * @since 1.8
 */
public interface IDecryptor {

	IDecryptor update(byte[] data, int offset, int len) throws GeneralSecurityException;

	byte[] decrypt(byte[] data, int offset, int len) throws GeneralSecurityException;

	default byte[] decrypt(byte[] data) throws GeneralSecurityException {
		return decrypt(data, 0, data.length);
	}

	default IDecryptor update(byte[] data) throws GeneralSecurityException {
		update(data, 0, data.length);
		return this;
	}

	default IDecryptor update(InputStream in) throws IOException, GeneralSecurityException {
		int buffSize = 1024;
		byte[] buffer = new byte[buffSize];
		for (int read = in.read(buffer, 0, buffSize); read > -1; read = in.read(buffer, 0, buffSize)) {
			update(buffer, 0, read);
		}
		return this;
	}

}
