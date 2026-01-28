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
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
public class ClassPathResourceResolver {

	@Setter
	@Getter
	private PathMatcher pathMatcher = new AntPathMatcher();
	@Setter
	@Getter
	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	public URL[] getResourceArray(String locationPattern) throws IOException {
		List<URL> rs = getResources(locationPattern);
		return rs.toArray(new URL[0]);
	}

	@Nonnull
	public List<URL> getResources(String locationPattern) throws IOException {
		return ClassPathResources.getResources(classLoader, locationPattern, pathMatcher);
	}

	@Nonnull
	public Set<String> getResourceNames(String locationPattern) throws IOException {
		return ClassPathResources.getResourceNames(classLoader, locationPattern, pathMatcher);
	}


}
