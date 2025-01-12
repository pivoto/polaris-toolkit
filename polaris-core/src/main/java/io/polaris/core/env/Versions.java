package io.polaris.core.env;

import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Jan 13, 2025
 */
public class Versions {
	private static final Version CURRENT;

	static {
		String version = Version.class.getPackage().getImplementationVersion();
		if (Strings.isBlank(version)) {
			version = InternalProperties.INSTANCE.getProperty("version");
		}
		if (Strings.isBlank(version)) {
			version = "0.0.0";
		}
		CURRENT = new Version(version);
	}

	public static Version current() {
		return CURRENT;
	}

	public static Version of(String version) {
		return new Version(version);
	}

	public static Version of(int major, int minor, int patch) {
		return new Version(major, minor, patch);
	}

}
