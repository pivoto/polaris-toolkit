package io.polaris.maven.plugin;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author Qt
 * @since 1.8,  May 08, 2024
 */
public class DuplicateFile {
	/** src properties files to duplicate. */
	@Parameter
	public String src;
	/** dest properties files to duplicate. */
	@Parameter
	public String dest;
}
