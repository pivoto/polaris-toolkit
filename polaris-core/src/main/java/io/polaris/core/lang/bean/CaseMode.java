package io.polaris.core.lang.bean;

/**
 * @author Qt
 * @since Nov 15, 2024
 */
public enum CaseMode {
	INSENSITIVE(1),
	CAMEL(1 << 1),
	;

	private final int value;

	CaseMode(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}

	public boolean is(int mode) {
		return (value & mode) != 0;
	}

	public boolean is(CaseModeOption mode) {
		return (value & mode.getMode()) != 0;
	}
}
