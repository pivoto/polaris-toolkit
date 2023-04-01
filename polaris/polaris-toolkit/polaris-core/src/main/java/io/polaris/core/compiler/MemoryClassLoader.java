package io.polaris.core.compiler;


import io.polaris.core.lang.Strings;

import javax.tools.JavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8
 */
public class MemoryClassLoader extends URLClassLoader {
	private final Map<String, MemoryJavaFileObject> classes = new HashMap<>();
	private final String classPath;
	private final Set<String> classPaths = new LinkedHashSet<>();

	public MemoryClassLoader() {
		super(new URL[0], Thread.currentThread().getContextClassLoader());
		this.classPath = parseClassPath();
	}

	public MemoryClassLoader(final ClassLoader parentClassLoader) {
		super(new URL[0], parentClassLoader);
		this.classPath = parseClassPath();
	}

	public Collection<JavaFileObject> files() {
		return Collections.unmodifiableCollection(classes.values());
	}

	@Override
	protected Class<?> findClass(final String className) throws ClassNotFoundException {
		MemoryJavaFileObject file = classes.get(className);
		if (file != null) {
			byte[] bytes = file.getByteCode();
			if (bytes != null) {
				return defineClass(className, bytes, 0, bytes.length);
			}
		}
		try {
			return getClass().getClassLoader().loadClass(className);
		} catch (ClassNotFoundException nf) {
			return super.findClass(className);
		}
	}

	public void add(final String className, MemoryJavaFileObject file) {
		classes.put(className, file);
	}

	@Override
	protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
	}

	@Override
	public InputStream getResourceAsStream(final String name) {
		if (name.endsWith(JavaFileObject.Kind.CLASS.extension)) {
			String className = name.substring(0, name.length() - JavaFileObject.Kind.CLASS.extension.length())
				.replace('/', '.');
			MemoryJavaFileObject file = classes.get(className);
			if (file != null) {
				byte[] bytes = file.getByteCode();
				if (bytes != null) {
					return new ByteArrayInputStream(bytes);
				}
			}
		}
		return super.getResourceAsStream(name);
	}

	private String parseClassPath() {
		StringBuilder classpath = new StringBuilder();
		Set<String> classPaths = this.classPaths;
		ClassLoader parent = getParent();
		if (parent != null && parent instanceof URLClassLoader
			&& (!"sun.misc.Launcher$AppClassLoader".equals(parent.getClass().getName()))) {
			for (URL url : ((URLClassLoader) parent).getURLs()) {
				String path = url.getFile();
				classPaths.add(path); // path
			}
		}

		Package[] packages = getPackages();
		if (packages != null) {
			Set<String> resourcePrefix = new HashSet<>();
			for (Package aPackage : packages) {
				String name = aPackage.getName();
				int i = name.indexOf(".");
				if (i > 0) {
					name = name.substring(0, i);
				}
				resourcePrefix.add(name);
			}

			for (String prefix : resourcePrefix) {
				try {
					Enumeration<URL> resources = getResources(prefix);
					while (resources.hasMoreElements()) {
						URL url = resources.nextElement();
						String file = url.toString();
						while (true) {
							boolean match = false;
							file = file.replaceFirst("/+$", "");
							if (file.startsWith("zip:")) {
								file = file.substring("zip:".length());
								match = true;
							}
							if (file.startsWith("jar:")) {
								file = file.substring("jar:".length());
								match = true;
							}
							if (file.startsWith("file:")) {
								file = file.substring("file:".length());
								match = true;
							}
							if (file.endsWith("!/" + prefix)) {
								file = file.substring(0, file.length() - prefix.length() - 2);
								match = true;
							} else if (file.endsWith("/" + prefix)) {
								file = file.substring(0, file.length() - prefix.length() - 1);
								match = true;
							}
							if (!match) {
								break;
							}
						}
						classPaths.add(file); // path
					}
				} catch (IOException e) {
				}
			}
		}

		String sysPath = System.getProperty("java.class.path");
		if (sysPath != null) {
			String[] arr = sysPath.split(File.pathSeparator);
			for (String path : arr) {
				if (Strings.isNotBlank(path)) {
					classPaths.add(path); // path
				}
			}
		}

		for (String path : classPaths) {
			classpath.append(path).append(File.pathSeparator);
		}
		return classpath.toString();
	}


	public Set<String> getClassPaths() {
		return Collections.unmodifiableSet(classPaths);
	}

	public String getClassPath() {
		return classPath;
	}

	public String getClassPath(String... paths) {
		StringBuilder classpath = new StringBuilder();
		for (String path : paths) {
			classpath.append(path).append(File.pathSeparator);
		}
		classpath.append(this.classPath);
		return classpath.toString();
	}
}
