package io.polaris.core.compiler;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;
import javax.tools.JavaFileObject;

import io.polaris.core.classpath.ClassPaths;
import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.SystemProperty;
import io.polaris.core.env.Config;
import io.polaris.core.io.IO;
import io.polaris.core.lang.JavaAccessControllers;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 */
public class MemoryClassLoader extends URLClassLoader {
	private static final Logger log = Loggers.of(MemoryClassLoader.class);
	private static final String classBytesCacheDir;
	private static final boolean classBytesCacheEnabled;
	private final Map<String, MemoryJavaFileObject> classes = new ConcurrentHashMap<>();
	private final String classPath;
	private final Set<String> classPaths = new LinkedHashSet<>();

	private static final class Holder {
		private static final Map<ClassLoader, MemoryClassLoader> CACHE = new ConcurrentHashMap<>();

		public static MemoryClassLoader get(ClassLoader classLoader) {
			classLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
			return CACHE.computeIfAbsent(classLoader, loader ->
				JavaAccessControllers.get(() -> new MemoryClassLoader(loader))
			);
		}
	}

	static {
		String tmpdir = Config.INSTANCE.bytecode().generator().tmpdir();
		if (Strings.isNotBlank(tmpdir)) {
			File dir = new File(tmpdir.trim());
			if (!dir.exists()) {
				dir.mkdirs();
			}
			classBytesCacheDir = dir.getAbsolutePath();
			classBytesCacheEnabled = dir.exists();
		} else {
			classBytesCacheDir = null;
			classBytesCacheEnabled = false;
		}
	}

	public MemoryClassLoader() {
		super(new URL[0], Thread.currentThread().getContextClassLoader());
		this.classPath = parseClassPath();
	}

	public MemoryClassLoader(final ClassLoader parentClassLoader) {
		super(new URL[0], parentClassLoader);
		this.classPath = parseClassPath();
	}

	public static MemoryClassLoader getInstance() {
		return Holder.get(Thread.currentThread().getContextClassLoader());
	}

	public static MemoryClassLoader getInstance(ClassLoader parentClassLoader) {
		return Holder.get(parentClassLoader);
	}

	public Collection<JavaFileObject> files() {
		return Collections.unmodifiableCollection(classes.values());
	}

	static void writeMemoryClassBytesCacheFile(String className, byte[] bytes) {
		if (classBytesCacheEnabled) {
			try {
				File file = new File(classBytesCacheDir + "/" + className.replace(".", "/") + ".class");
				if (log.isDebugEnabled()) {
					log.debug("缓存动态生成类的字节码：{}", file.getAbsolutePath());
				}
				IO.writeBytes(file, bytes);
			} catch (Throwable e) {
				log.error("", e);
			}
		}
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

	public void addIfAbsent(String className, byte[] classBytes) {
		addIfAbsent(className, new MemoryJavaFileObject(className, classBytes));
	}

	public void add(String className, byte[] classBytes) {
		add(className, new MemoryJavaFileObject(className, classBytes));
	}

	public void addIfAbsent(final String className, MemoryJavaFileObject file) {
		//classes.putIfAbsent(className, file);
		MemoryJavaFileObject old = classes.putIfAbsent(className, file);
		if (old == null) {
			writeMemoryClassBytesCacheFile(className, file.getByteCode());
		}
	}

	public void add(final String className, MemoryJavaFileObject file) {
		classes.put(className, file);
		writeMemoryClassBytesCacheFile(className, file.getByteCode());
	}

	@Override
	protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		return super.loadClass(name, resolve);
	}


	@Nullable
	public byte[] getMemoryClassBytes(String name) {
		String className = name;
		if (name.endsWith(JavaFileObject.Kind.CLASS.extension)) {
			className = name.substring(0, name.length() - JavaFileObject.Kind.CLASS.extension.length())
				.replace('/', '.');

		}
		MemoryJavaFileObject file = classes.get(className);
		if (file == null) {
			return null;
		}
		return file.getByteCode();
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
		{
			ClassLoader parent = getParent();
			Deque<ClassLoader> loaders = new ArrayDeque<>();
			{
				ClassLoader loader = parent;
				while (loader != null) {
					loaders.addFirst(loader);
					loader = loader.getParent();
				}
			}
			for (ClassLoader loader : loaders) {
				if (loader instanceof URLClassLoader && !ClassLoader.getSystemClassLoader().equals(loader)) {
					for (URL url : ((URLClassLoader) loader).getURLs()) {
						String path = url.getFile();
						classPaths.add(path); // path
					}
				}
			}
		}

		{
			Set<String> resourcePrefix = new HashSet<>();
			resourcePrefix.add(StdConsts.META_INF);
			Package[] packages = getPackages();
			if (packages != null) {
				for (Package aPackage : packages) {
					String name = aPackage.getName();
					int i = name.indexOf(".");
					if (i > 0) {
						name = name.substring(0, i);
					}
					resourcePrefix.add(name);
				}
			}

			for (String prefix : resourcePrefix) {
				try {
					Enumeration<URL> resources = getResources(prefix);
					while (resources.hasMoreElements()) {
						URL url = resources.nextElement();
						String file = ClassPaths.cleanPath(url, prefix);
						classPaths.add(file); // path
					}
				} catch (IOException ignored) {
				}
			}
		}

		String sysPath = SystemProperty.JAVA_CLASS_PATH.value();
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
