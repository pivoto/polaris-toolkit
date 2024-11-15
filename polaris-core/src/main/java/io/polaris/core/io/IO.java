package io.polaris.core.io;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.CodeSource;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("All")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IO {


	public static void close(AutoCloseable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Throwable ignore) {
			}
		}
	}

	@Nullable
	public static String getCodeSourceLocation(@Nonnull Class c) {
		CodeSource codeSource = c.getProtectionDomain().getCodeSource();
		if (codeSource == null) {
			return null;
		}
		URL url = codeSource.getLocation();
		if (Objects.equals(url.getProtocol(), "file")) {
			return url.getFile();
		}
		return null;
	}

	public static BufferedInputStream getInputStream(File file) throws IOException {
		return IO.toBuffered(IO.toStream(file));
	}

	public static ByteArrayInputStream toStream(byte[] content) {
		return new ByteArrayInputStream(content);
	}

	public static FileInputStream toStream(File file) throws IOException {
		return new FileInputStream(file);
	}

	public static BufferedInputStream toBuffered(InputStream in) {
		return (in instanceof BufferedInputStream) ? (BufferedInputStream) in : new BufferedInputStream(in);
	}

	public static BufferedInputStream toBuffered(InputStream in, int bufferSize) {
		return (in instanceof BufferedInputStream) ? (BufferedInputStream) in : new BufferedInputStream(in, bufferSize);
	}


	public static InputStream getInputStream(String path) throws FileNotFoundException {
		try {
			StackTraceElement[] traces = new Throwable().getStackTrace();
			StackTraceElement trace = traces.length > 1 ? traces[1] : traces[0];
			Class<?> caller = Class.forName(trace.getClassName());
			return getInputStream(path, caller);
		} catch (ClassNotFoundException e) {
			return getInputStream(path, IO.class);
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

	public static BufferedReader getReader(@Nonnull InputStream in, Charset charset) {
		InputStreamReader reader;
		if (null == charset) {
			reader = new InputStreamReader(in);
		} else {
			reader = new InputStreamReader(in, charset);
		}
		return new BufferedReader(reader);
	}

	public static BufferedReader getReader(@Nonnull Reader reader) {
		return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
	}

	public static BufferedReader toBuffered(Reader reader) {
		return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
	}

	public static BufferedReader toBuffered(Reader reader, int bufferSize) {
		return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader, bufferSize);
	}

	public static BufferedOutputStream getOutputStream(@Nonnull File file) throws IOException {
		return IO.toBuffered(new FileOutputStream(file));
	}

	public static BufferedOutputStream toBuffered(@Nonnull OutputStream out) {
		return (out instanceof BufferedOutputStream) ? (BufferedOutputStream) out : new BufferedOutputStream(out);
	}

	public static BufferedOutputStream toBuffered(OutputStream out, int bufferSize) {
		return (out instanceof BufferedOutputStream) ? (BufferedOutputStream) out : new BufferedOutputStream(out, bufferSize);
	}

	public static OutputStreamWriter getWriter(OutputStream out, Charset charset) {
		if (null == charset) {
			return new OutputStreamWriter(out);
		} else {
			return new OutputStreamWriter(out, charset);
		}
	}

	public static BufferedWriter toBuffered(Writer writer) {
		return (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer);
	}

	public static BufferedWriter toBuffered(Writer writer, int bufferSize) {
		return (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer, bufferSize);
	}


	public static byte[] toBytes(File file) throws IOException {
		return toBytes(toStream(file));
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

	public static String toString(InputStream input, String charset) throws IOException {
		InputStreamReader in = new InputStreamReader(input, charset);
		return toString(in);
	}

	public static String toString(InputStream input, Charset charset) throws IOException {
		InputStreamReader in = new InputStreamReader(input, charset);
		return toString(in);
	}

	public static String toString(InputStream input, String charset, int bufferSize) throws IOException {
		InputStreamReader in = new InputStreamReader(input, charset);
		return toString(in, bufferSize);
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
			mkdirParent(dest);
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


	public static void writeBytes(File file, byte[] bytes) throws IOException {
		mkdirParent(file);
		try (BufferedOutputStream out = IO.getOutputStream(file);) {
			out.write(bytes);
		}
	}

	public static void writeBytes(OutputStream out, byte[]... bytesArray) throws IOException {
		for (byte[] bytes : bytesArray) {
			out.write(bytes);
		}
	}

	public static void writeString(File file, Charset charset, String content) throws IOException {
		mkdirParent(file);
		try (BufferedOutputStream out = IO.getOutputStream(file);) {
			out.write(content.getBytes(charset));
		}
	}

	public static void writeString(OutputStream out, Charset charset, String... contents) throws IOException {
		OutputStreamWriter writer = getWriter(out, charset);
		for (String content : contents) {
			writer.write(content);
		}
		writer.flush();
	}

	public static void mkdir(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static void mkdirParent(File file) {
		File dir = file.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
}
