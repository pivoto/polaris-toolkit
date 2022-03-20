package io.polaris.toolkit.spring.core.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * @author Qt
 * @version Oct 29, 2021
 * @since 1.8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IOUtils {

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
			}
		}
	}

	public static InputStream getInputStream(String path) throws FileNotFoundException {
		try {
			StackTraceElement[] traces = new Throwable().getStackTrace();
			StackTraceElement trace = traces.length > 1 ? traces[1] : traces[0];
			Class<?> caller = Class.forName(trace.getClassName());
			return getInputStream(path, caller);
		} catch (ClassNotFoundException e) {
			return getInputStream(path, IOUtils.class);
		}
	}

	@SuppressWarnings("resource")
	public static InputStream getInputStream(String path, Class<?> caller)
			throws FileNotFoundException {
		InputStream in = null;
		try {
			in = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			if (path.startsWith("/")) {
				path = path.substring(1);
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
				if (in == null) {
					in = caller.getResourceAsStream(path);
				}
			} else {
				in = caller.getResourceAsStream(path);
				if (in == null) {
					in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
				}
			}
			if (in == null) {
				in = ClassLoader.getSystemResourceAsStream(path);
			}
			if (in == null) {
				throw new FileNotFoundException(path);
			}
		}
		return in;
	}

	public static byte[] toBytes(InputStream input) throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			copy(input, output);
			return output.toByteArray();
		}
	}

	public static byte[] toBytes(InputStream input, int bufferSize) throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			copy(input, output, bufferSize);
			return output.toByteArray();
		}
	}

	public static String toString(InputStream input) throws IOException {
		return toString(input, Charset.defaultCharset());
	}

	public static String toString(InputStream input, int bufferSize) throws IOException {
		return toString(input, Charset.defaultCharset(), bufferSize);
	}

	public static String toString(InputStream input, Charset charset) throws IOException {
		InputStreamReader in = new InputStreamReader(input, charset);
		return toString(in);
	}

	public static String toString(InputStream input, Charset charset, int bufferSize) throws IOException {
		InputStreamReader in = new InputStreamReader(input, charset);
		return toString(in, bufferSize);
	}

	public static String toString(Reader input) throws IOException {
		try (StringWriter sw = new StringWriter()) {
			copy(input, sw);
			return sw.toString();
		}
	}

	public static String toString(Reader input, int bufferSize) throws IOException {
		try (StringWriter sw = new StringWriter()) {
			copy(input, sw, bufferSize);
			return sw.toString();
		}
	}


	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		return copy(input, output, 4096);
	}

	public static int copy(InputStream input, OutputStream output, int bufferSize)
			throws IOException {
		byte[] buffer = new byte[bufferSize];
		int count = 0;
		int n;
		while ((n = input.read(buffer)) != -1) {
			output.write(buffer, 0, n);
			count += n;
		}
		output.flush();
		return count;
	}

	public static int copy(Reader input, Writer output) throws IOException {
		return copy(input, output, 4096);
	}

	public static int copy(Reader input, Writer output, int bufferSize) throws IOException {
		char[] buffer = new char[bufferSize];
		int count = 0;
		int n;
		while ((n = input.read(buffer)) != -1) {
			output.write(buffer, 0, n);
			count += n;
		}
		output.flush();
		return count;
	}

	public static int copy(File src, File dest) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			int i = copy(in, out);
			out.flush();
			return i;
		} finally {
			close(out);
			close(in);
		}
	}
}
