package io.polaris.core.codec;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Qt
 * @since 1.8
 */
public interface Encoder {
	int getEncodedLength(int inputLength);

	int getMaxDecodedLength(int inputLength);

	int encode(byte[] data, int off, int length, OutputStream out) throws IOException;

	int decode(byte[] data, int off, int length, OutputStream out) throws IOException;

	int decode(String data, OutputStream out) throws IOException;
}
