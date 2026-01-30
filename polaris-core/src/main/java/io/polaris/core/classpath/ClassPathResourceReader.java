package io.polaris.core.classpath;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Nonnull;

import io.polaris.core.string.AntPathMatcher;
import io.polaris.core.string.PathMatcher;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qt
 * @since Jan 26, 2026
 */
public class ClassPathResourceReader {

	@Setter
	@Getter
	private PathMatcher pathMatcher = new AntPathMatcher();
	@Setter
	@Getter
	private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();


	@Nonnull
	public ClassPathResource[] getResources(String locationPattern) throws IOException {
		return ClassPathResources.getResources(classLoader, locationPattern, pathMatcher);
	}

	@Nonnull
	public Set<String> getResourceNames(String locationPattern) throws IOException {
		return ClassPathResources.getResourceNames(classLoader, locationPattern, pathMatcher);
	}


}
