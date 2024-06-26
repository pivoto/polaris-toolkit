package io.polaris.core.crypto;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Qt
 * @since 1.8
 */
public interface IEncryptor {

	IEncryptor update(byte[] data, int offset, int len) ;

	byte[] encrypt(byte[] data, int offset, int len) ;

	default byte[] encrypt(byte[] data)  {
		return encrypt(data, 0, data.length);
	}

	default IEncryptor update(byte[] data)  {
		update(data, 0, data.length);
		return this;
	}

	default IEncryptor update(InputStream in) throws IOException {
		int buffSize = 1024;
		byte[] buffer = new byte[buffSize];
		for (int read = in.read(buffer, 0, buffSize); read > -1; read = in.read(buffer, 0, buffSize)) {
			update(buffer, 0, read);
		}
		return this;
	}
}
