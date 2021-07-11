package io.awesome.dbv.toolkit;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Qt
 * @since 1.8
 * @version  1.0, Sep 13, 2016
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class IOKit {

	public static void closeQuietly(final Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (final IOException ioe) {
			// ignore
		}
	}

	public static InputStream getInputStream(final String path) throws FileNotFoundException {
		try {
			StackTraceElement[] traces = new Throwable().getStackTrace();
			StackTraceElement trace = traces.length > 1 ? traces[1] : traces[0];
			Class<?> clazz = Class.forName(trace.getClassName());
			return getInputStream(path, clazz);
		} catch (ClassNotFoundException e) {
			return getInputStream(path, IOKit.class);
		}
	}

	public static InputStream getInputStream(String path, final Class<?> clazz)
			throws FileNotFoundException {
		InputStream in = null;
		try {
			in = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			if (path.startsWith("/")) {
				path = path.substring(1);
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
				if (in == null) {
					in = clazz.getResourceAsStream(path);
				}
			} else {
				in = clazz.getResourceAsStream(path);
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


}
