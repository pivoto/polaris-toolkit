package io.polaris.core.classloader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.function.BiFunction;

import io.polaris.core.io.IO;

/**
 * @author Qt
 * @since Oct 08, 2024
 */
public class DynamicURLClassLoader extends URLClassLoader {

	private final BiFunction<DynamicURLClassLoader, String, Class<?>> extension;

	public DynamicURLClassLoader(ClassLoader parent, URL[] urls) {
		this(parent, urls, null);
	}

	public DynamicURLClassLoader(ClassLoader parent, URL[] urls, BiFunction<DynamicURLClassLoader, String, Class<?>> extension) {
		super(urls, parent);
		this.extension = extension;
	}

	public DynamicURLClassLoader(URL[] urls) {
		this(urls, null);
	}

	public DynamicURLClassLoader(URL[] urls, BiFunction<DynamicURLClassLoader, String, Class<?>> extension) {
		super(urls);
		this.extension = extension;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> loadedClass = null;
			loadedClass = findLoadedClass(name);
			if (loadedClass == null) {
				if (name.startsWith("java.")) {
					try {
						loadedClass = Class.forName(name, false, getParent());
					} catch (Throwable ignored) {
					}
				}
			}
			if (loadedClass == null) {
				try {
					loadedClass = readClass(name);
				} catch (Throwable ignored) {
				}
			}
			if (loadedClass == null) {
				try {
					if (extension != null) {
						loadedClass = extension.apply(this, name);
					}
				} catch (Throwable ignored) {
				}
			}
			if (loadedClass == null) {
				loadedClass = Class.forName(name, false, getParent());
			}
			if (resolve) {
				resolveClass(loadedClass);
			}
			return loadedClass;
		}
	}

	private Class<?> readClass(final String name) {
		try {
			Enumeration<URL> es = this.findResources(name.replace('.', '/') + ".class");
			while (es.hasMoreElements()) {
				URL url = es.nextElement();
				InputStream in = url.openStream();
				if (in != null) {
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						byte[] buf = new byte[4096];
						for (int i = in.read(buf); i != -1; i = in.read(buf)) {
							bos.write(buf, 0, i);
						}
						byte[] bytes = bos.toByteArray();
						Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
						return clazz;
					} finally {
						IO.close(in);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

}
