package io.polaris.core.env;

import java.util.Objects;
import java.util.StringJoiner;

import io.polaris.core.string.Strings;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
@Getter
@EqualsAndHashCode
public class Version implements Comparable<Version> {
	private static final Version CURRENT;
	private final int major;
	private final int minor;
	private final int patch;

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

	public Version(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public Version(String version) {
		String[] arr = version.replaceAll("-\\w+$", "").split("\\.");
		String[] vs = new String[]{"0", "0", "0"};
		System.arraycopy(arr, 0, vs, 0, arr.length);
		int major = 0, minor = 0, patch = 0;
		try {
			major = Integer.parseInt(vs[0]);
		} catch (NumberFormatException e) {
		}
		try {
			minor = Integer.parseInt(vs[1]);
		} catch (NumberFormatException e) {
		}
		try {
			patch = Integer.parseInt(vs[2]);
		} catch (NumberFormatException e) {
		}
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public static Version current() {
		return CURRENT;
	}

	public int asInt() {
		return (major & 0xFF) << 24 | (minor & 0xFF) << 16 | (patch & 0xFFFF);
	}

	@Override
	public String toString() {
		return new StringJoiner(".")
			.add(Objects.toString(major))
			.add(Objects.toString(minor))
			.add(Objects.toString(patch))
			.toString();
	}

	@Override
	public int compareTo(Version another) {
		return compare(this, another);
	}

	public static int compare(Version x, Version y) {
		if (x.major < y.major) {
			return -1;
		}
		if (x.major > y.major) {
			return 1;
		}
		if (x.minor < y.minor) {
			return -1;
		}
		if (x.minor > y.minor) {
			return 1;
		}
		if (x.patch < y.patch) {
			return -1;
		}
		if (x.patch > y.patch) {
			return 1;
		}
		return 0;
	}
}
