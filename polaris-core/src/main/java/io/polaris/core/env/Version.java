package io.polaris.core.env;

import java.util.Objects;
import java.util.StringJoiner;

import lombok.Getter;

/**
 * @author Qt
 * @since Apr 23, 2024
 */
@Getter
public class Version implements Comparable<Version> {
	private final int major;
	private final int minor;
	private final int patch;

	public Version(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public Version(String version) {
		String[] arr = version.replaceAll("[^.\\d]", "").split("\\.");
		String[] vs = new String[]{"0", "0", "0"};
		System.arraycopy(arr, 0, vs, 0, vs.length);
		int major = 0, minor = 0, patch = 0;
		try {
			major = Integer.parseInt(vs[0]);
		} catch (NumberFormatException ignored) {
		}
		try {
			minor = Integer.parseInt(vs[1]);
		} catch (NumberFormatException ignored) {
		}
		try {
			patch = Integer.parseInt(vs[2]);
		} catch (NumberFormatException ignored) {
		}
		this.major = major;
		this.minor = minor;
		this.patch = patch;
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

	public boolean after(Version another) {
		return compare(this, another) > 0;
	}

	public boolean afterOrEquals(Version another) {
		return compare(this, another) >= 0;
	}

	public boolean before(Version another) {
		return compare(this, another) < 0;
	}

	public boolean beforeOrEquals(Version another) {
		return compare(this, another) <= 0;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Version)) {
			return false;
		}
		Version version = (Version) o;
		return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.major, this.minor, this.patch);
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
