package io.polaris.core.classpath;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.consts.StdConsts;
import io.polaris.core.consts.SystemProperty;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
public class ClassPaths {
	private static final Logger log = Loggers.of(ClassPaths.class);

	public static String cleanPath(final URL url) {
		String path = url.toString();
		try {
			path = URLDecoder.decode(path, StdConsts.UTF_8);
		} catch (UnsupportedEncodingException ignored) {}

		if (path.startsWith("zip:")) {
			path = path.substring("zip:".length());
		}
		if (path.startsWith("jar:")) {
			path = path.substring("jar:".length());
		}
		if (path.startsWith("file:")) {
			path = path.substring("file:".length());
		}
		if (path.endsWith("!/")) {
			path = path.substring(0, path.length() - 2);
		} else if (path.endsWith("!")) {
			path = path.substring(0, path.length() - 1);
		} else if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	public static String cleanPath(final URL url, String entry) {
		if (Strings.isBlank(entry)) {
			return cleanPath(url);
		}
		String file = url.toString();
		try {
			file = URLDecoder.decode(file, StdConsts.UTF_8);
		} catch (UnsupportedEncodingException ignored) {}

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
			if (file.endsWith("!/" + entry)) {
				file = file.substring(0, file.length() - entry.length() - 2);
				match = true;
			} else if (file.endsWith("!" + entry) || file.endsWith("/" + entry)) {
				file = file.substring(0, file.length() - entry.length() - 1);
				match = true;
			}
			if (!match) {
				break;
			}
		}
		return file;
	}

	public static Collection<URL> distinctUrls(Collection<URL> urls) {
		Map<String, URL> distinct = new LinkedHashMap<>(urls.size());
		for (URL url : urls) {
			distinct.put(url.toString(), url);
		}
		return distinct.values();
	}

	public static List<File> getClassLoaderFiles(@Nonnull ClassLoader classloader) {
		List<URL> urls = getClassLoaderUrls(classloader);
		List<File> files = new ArrayList<>();
		for (URL url : urls) {
			File file = toFile(url);
			if (file != null) {
				files.add(file);
			}
		}
		return files;
	}

	public static List<URL> getClassLoaderUrls(@Nonnull ClassLoader classloader) {
		List<URL> urls = new ArrayList<>();
		Deque<ClassLoader> loaders = new ArrayDeque<>();
		{
			ClassLoader loader = classloader;
			while (loader != null) {
				loaders.addFirst(loader);
				loader = loader.getParent();
			}
		}
		// 从所有继承体系下的类加载器获取
		{
			for (ClassLoader loader : loaders) {
				if (loader instanceof URLClassLoader) {
					urls.addAll(Arrays.asList(((URLClassLoader) loader).getURLs()));
				}
			}
		}
		// 尝试通过类加载器中的资源名间接获取
		{
			PrivateClassloader privateClassloader = new PrivateClassloader(classloader);
			Set<String> resourcePrefix = new HashSet<>();
			resourcePrefix.add(StdConsts.META_INF);
			Package[] packages = privateClassloader.getPackages();
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
					Enumeration<URL> resources = classloader.getResources(prefix);
					while (resources.hasMoreElements()) {
						URL url = resources.nextElement();
						String path = cleanPath(url, prefix);
						try {
							urls.add(new File(path).toURI().toURL());
						} catch (MalformedURLException e) {
							urls.add(new URL("file", null, new File(path).getAbsolutePath()));
						}
					}
				} catch (IOException ignored) {
				}
			}
		}
		// 尝试通过类路径环境变量获取
		parseJavaClassPath(urls);
		return new ArrayList<>(distinctUrls(urls));
	}

	private static void parseJavaClassPath(List<URL> urls) {
		String[] arr = Strings.delimitedToArray(SystemProperty.JAVA_CLASS_PATH.value(), SystemProperty.PATH_SEPARATOR.value());
		for (String entry : arr) {
			try {
				try {
					urls.add(new File(entry).toURI().toURL());
				} catch (SecurityException e) { // File.toURI checks to see if the file is a directory
					urls.add(new URL("file", null, new File(entry).getAbsolutePath()));
				}
			} catch (MalformedURLException e) {
				log.warn(e, "malformed classpath entry: " + entry);
			}
		}
	}


	@Nullable
	private static File toFile(URL url) {
		if (url.getProtocol().equals("file")) {
			try {
				Path path = Paths.get(url.toURI());
				return path.toFile();
			} catch (Exception e) {
				try {
					return new File(url.toURI()); // Accepts escaped characters like %20.
				} catch (URISyntaxException e1) { // URL.toURI() doesn't escape chars.
					return new File(url.getPath()); // Accepts non-escaped chars like space.
				}
			}
		}
		return null;
	}


	private static class PrivateClassloader extends ClassLoader {
		public PrivateClassloader(ClassLoader parent) {
			super(parent);
		}

		@Override
		protected Package[] getPackages() {
			return super.getPackages();
		}
	}
}
