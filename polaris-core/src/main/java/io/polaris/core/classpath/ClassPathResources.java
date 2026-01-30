package io.polaris.core.classpath;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import io.polaris.core.io.JarReaders;
import io.polaris.core.string.AntPathMatcher;
import io.polaris.core.string.PathMatcher;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
public class ClassPathResources {

	private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

	public static ClassPathResourceReader newReader(ClassLoader classLoader, PathMatcher pathMatcher) {
		ClassPathResourceReader resource = new ClassPathResourceReader();
		if (pathMatcher != null) {
			resource.setPathMatcher(pathMatcher);
		}
		if (classLoader != null) {
			resource.setClassLoader(classLoader);
		}
		return resource;
	}

	@Nonnull
	public static ClassPathResource[] getResources(String locationPattern) throws IOException {
		return getResources(Thread.currentThread().getContextClassLoader(), locationPattern, PATH_MATCHER);
	}

	@Nonnull
	public static ClassPathResource[] getResources(ClassLoader classLoader, String locationPattern) throws IOException {
		return getResources(classLoader, locationPattern, PATH_MATCHER);
	}

	@Nonnull
	public static ClassPathResource[] getResources(ClassLoader classLoader, String locationPattern, PathMatcher pathMatcher) throws IOException {
		List<URL> rs = new ArrayList<>();
		Set<String> resourceNames = getResourceNames(classLoader, locationPattern, pathMatcher);
		resourceNames.size();
		ClassPathResource[] resources = new ClassPathResource[resourceNames.size()];
		int i = 0;
		for (String resourceName : resourceNames) {
			resources[i++] = new ClassPathResource(classLoader, resourceName);
		}
		return resources;
	}

	@Nonnull
	public static Set<String> getResourceNames(String locationPattern) throws IOException {
		return getResourceNames(Thread.currentThread().getContextClassLoader(), locationPattern, PATH_MATCHER);
	}

	@Nonnull
	public static Set<String> getResourceNames(ClassLoader classLoader, String locationPattern) throws IOException {
		return getResourceNames(classLoader, locationPattern, PATH_MATCHER);
	}

	@Nonnull
	public static Set<String> getResourceNames(ClassLoader classLoader, String locationPattern, PathMatcher pathMatcher) throws IOException {
		String rootDirPath = determineRootDir(pathMatcher, locationPattern);
		String subPattern = locationPattern.substring(rootDirPath.length());
		rootDirPath = rootDirPath.replaceAll("^/+|/+$", "");

		Set<String> resourceNames = new LinkedHashSet<>();
		if (Strings.isBlank(subPattern) && Strings.isBlank(rootDirPath)) {
			return resourceNames;
		}

		if (Strings.isBlank(subPattern) && Strings.isNotBlank(rootDirPath)) {
			resourceNames.add(rootDirPath);
		} else if (Strings.isNotBlank(rootDirPath) || classLoader == null) {
			Set<String> scanPaths = new LinkedHashSet<>();
			Enumeration<URL> urls = classLoader == null ? ClassLoader.getSystemResources(rootDirPath) : classLoader.getResources(rootDirPath);
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String path = ClassPaths.cleanPath(url, rootDirPath);
				scan(pathMatcher, resourceNames, path, rootDirPath, subPattern);
			}
		} else {
			List<URL> urls = ClassPaths.getClassLoaderUrls(classLoader);
			for (URL url : urls) {
				String path = ClassPaths.cleanPath(url);
				scan(pathMatcher, resourceNames, path, rootDirPath, subPattern);
			}
		}
		return resourceNames;
	}

	private static void scan(PathMatcher pathMatcher, Set<String> resourceNames, String resourceDir, String rootDirPath, String subPattern) throws IOException {
		File file = new File(resourceDir);
		String rootPrefix = Strings.isBlank(rootDirPath) ? "" : rootDirPath + "/";
		if (file.isDirectory()) {
			File rootDir = new File(file, rootDirPath);
			scanDir(pathMatcher, resourceNames, rootPrefix, rootDir, "", subPattern);
		} else {
			if (resourceDir.endsWith(".jar") || resourceDir.endsWith(".zip")) {
				scanJar(pathMatcher, resourceNames, rootPrefix, resourceDir, subPattern);
			}
		}
	}

	private static void scanJar(PathMatcher pathMatcher, Set<String> resourceNames, String rootPrefix, @Nonnull String jarResource, String subPattern) throws IOException {
		JarReaders.read(jarResource, (path, inputStream) -> {
			if (path.startsWith(rootPrefix)) {
				String subPath = path.substring(rootPrefix.length());
				if (pathMatcher.match(subPattern, subPath)) {
					resourceNames.add(path);
				}
			}
		});
	}

	private static void scanDir(PathMatcher pathMatcher, Set<String> resourceNames, String rootPrefix, @Nonnull File dir, String parentPrefix, String subPattern) {
		if (!dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				scanDir(pathMatcher, resourceNames, rootPrefix, file, parentPrefix + file.getName() + "/", subPattern);
			} else {
				String name = file.getName();
				String path = parentPrefix + name;
				if (pathMatcher.match(subPattern, path)) {
					resourceNames.add(rootPrefix + path);
				}
			}
		}
	}

	@Nonnull
	static String determineRootDir(PathMatcher pathMatcher, String location) {
		int prefixEnd = 0;
		int rootDirEnd = location.length();
		while (rootDirEnd > prefixEnd && pathMatcher.isPattern(location.substring(prefixEnd, rootDirEnd))) {
			rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
		}
		return location.substring(0, rootDirEnd);
	}

	@Nonnull
	static String determineRootDir(String location) {
		return determineRootDir(PATH_MATCHER, location);
	}
}
