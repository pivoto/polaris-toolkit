package io.polaris.core.consts;

import java.math.BigDecimal;

/**
 * @author Qt
 * @since Mar 14, 2026
 */
public enum JavaVersion {

	JAVA_0(new BigDecimal("0.9")),
	JAVA_1(new BigDecimal("1.1")),
	JAVA_2(new BigDecimal("1.2")),
	JAVA_3(new BigDecimal("1.3")),
	JAVA_4(new BigDecimal("1.4")),
	JAVA_5(new BigDecimal("1.5")),
	JAVA_6(new BigDecimal("1.6")),
	JAVA_7(new BigDecimal("1.7")),
	JAVA_8(new BigDecimal("1.8")),
	JAVA_9(new BigDecimal("9")),
	JAVA_10(new BigDecimal("10")),
	JAVA_11(new BigDecimal("11")),
	JAVA_12(new BigDecimal("12")),
	JAVA_13(new BigDecimal("13")),
	JAVA_14(new BigDecimal("14")),
	JAVA_15(new BigDecimal("15")),
	JAVA_16(new BigDecimal("16")),
	JAVA_17(new BigDecimal("17")),
	JAVA_18(new BigDecimal("18")),
	JAVA_19(new BigDecimal("19")),
	JAVA_20(new BigDecimal("20")),
	JAVA_21(new BigDecimal("21")),
	JAVA_22(new BigDecimal("22")),
	JAVA_23(new BigDecimal("23")),
	JAVA_24(new BigDecimal("24")),
	JAVA_25(new BigDecimal("25")),
	UNKNOWN(new BigDecimal("99")),
	;

	private static final BigDecimal NEWER = new BigDecimal("26");
	public static final JavaVersion CURRENT = get();
	private final BigDecimal value;

	JavaVersion(final BigDecimal value) {
		this.value = value;
	}

	public boolean isEqualThan(JavaVersion version) {
		return compareTo(version) == 0;
	}

	public boolean isNewerThan(JavaVersion version) {
		return compareTo(version) > 0;
	}

	public boolean isOlderThan(JavaVersion version) {
		return compareTo(version) < 0;
	}

	public boolean isEqualOrNewerThan(JavaVersion version) {
		return compareTo(version) >= 0;
	}

	public boolean isEqualOrOlderThan(JavaVersion version) {
		return compareTo(version) <= 0;
	}

	public static JavaVersion get() {
		return get(System.getProperty("java.specification.version"));
	}

	public static JavaVersion get(final String versionStr) {
		if (versionStr == null) {
			return UNKNOWN;
		}
		BigDecimal version = null;
		try {
			version = new BigDecimal(versionStr);
		} catch (Exception ignored) {
		}
		if (version == null) {
			return UNKNOWN;
		}
		if (version.compareTo(JAVA_1.value) < 0) {
			return JAVA_0;
		}
		if (version.compareTo(JAVA_2.value) < 0) {
			return JAVA_1;
		}
		if (version.compareTo(JAVA_3.value) < 0) {
			return JAVA_2;
		}
		if (version.compareTo(JAVA_4.value) < 0) {
			return JAVA_3;
		}
		if (version.compareTo(JAVA_5.value) < 0) {
			return JAVA_4;
		}
		if (version.compareTo(JAVA_6.value) < 0) {
			return JAVA_5;
		}
		if (version.compareTo(JAVA_7.value) < 0) {
			return JAVA_6;
		}
		if (version.compareTo(JAVA_8.value) < 0) {
			return JAVA_7;
		}
		if (version.compareTo(JAVA_9.value) < 0) {
			return JAVA_8;
		}
		if (version.compareTo(JAVA_10.value) < 0) {
			return JAVA_9;
		}
		if (version.compareTo(JAVA_11.value) < 0) {
			return JAVA_10;
		}
		if (version.compareTo(JAVA_12.value) < 0) {
			return JAVA_11;
		}
		if (version.compareTo(JAVA_13.value) < 0) {
			return JAVA_12;
		}
		if (version.compareTo(JAVA_14.value) < 0) {
			return JAVA_13;
		}
		if (version.compareTo(JAVA_15.value) < 0) {
			return JAVA_14;
		}
		if (version.compareTo(JAVA_16.value) < 0) {
			return JAVA_15;
		}
		if (version.compareTo(JAVA_17.value) < 0) {
			return JAVA_16;
		}
		if (version.compareTo(JAVA_18.value) < 0) {
			return JAVA_17;
		}
		if (version.compareTo(JAVA_19.value) < 0) {
			return JAVA_18;
		}
		if (version.compareTo(JAVA_20.value) < 0) {
			return JAVA_19;
		}
		if (version.compareTo(JAVA_21.value) < 0) {
			return JAVA_20;
		}
		if (version.compareTo(JAVA_22.value) < 0) {
			return JAVA_21;
		}
		if (version.compareTo(JAVA_23.value) < 0) {
			return JAVA_22;
		}
		if (version.compareTo(JAVA_24.value) < 0) {
			return JAVA_23;
		}
		if (version.compareTo(JAVA_25.value) < 0) {
			return JAVA_24;
		}
		if (version.compareTo(NEWER) < 0) {
			return JAVA_25;
		}
		return UNKNOWN;
	}
}
