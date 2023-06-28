package io.polaris.core.script;

import io.polaris.core.io.IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author Qt
 * @since 1.8
 */
public class ScriptContent {
	public static final String FILE_PREFIX = "file:";
	public static final String CLASSPATH_PREFIX = "classpath:";

	public static String getContent(String path) throws IOException {
		try (InputStream in = getInputStream(path)) {
			return IO.toString(in, Charset.defaultCharset());
		}
	}

	public static InputStream getInputStream(String path) throws FileNotFoundException {
		InputStream in = null;
		boolean isClasspath = path.startsWith(CLASSPATH_PREFIX);
		String resource;
		if (isClasspath) {
			resource = path.substring(CLASSPATH_PREFIX.length());
		} else {
			if (path.startsWith(FILE_PREFIX)) {
				resource = path.substring(FILE_PREFIX.length());
			} else {
				resource = path;
			}
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (isClasspath) {
			in = classLoader.getResourceAsStream(path);
		}
		if (in == null) {
			try {
				in = new FileInputStream(path);
			} catch (FileNotFoundException e) {
				in = classLoader.getResourceAsStream(path);
				if (in == null) {
					in = ClassLoader.getSystemResourceAsStream(path);
				}
				if (in == null) {
					throw new FileNotFoundException(path);
				}
			}
		}
		return in;
	}
}
