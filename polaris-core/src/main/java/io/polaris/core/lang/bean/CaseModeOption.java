package io.polaris.core.lang.bean;

import java.util.Objects;

import io.polaris.core.string.Hex;

/**
 * @author Qt
 * @since Nov 15, 2024
 */
public class CaseModeOption {
	private static final CaseModeOption[] VALUES;

	static {
		int len = (1 << CaseMode.values().length);
		VALUES = new CaseModeOption[len];
		for (int i = 0; i < len; i++) {
			VALUES[i] = new CaseModeOption(i);
		}
	}


	private final int mode;

	private CaseModeOption(int mode) {
		this.mode = mode;
	}

	public static CaseModeOption empty() {
		return VALUES[0];
	}

	public static CaseModeOption all() {
		return VALUES[VALUES.length - 1];
	}

	public static CaseModeOption of(boolean caseInsensitive, boolean caseCamel) {
		int mode = 0;
		if (caseInsensitive) {
			mode |= CaseMode.INSENSITIVE.value();
		}
		if (caseCamel) {
			mode |= CaseMode.CAMEL.value();
		}
		return CaseModeOption.of(mode);
	}

	public static CaseModeOption of(CaseMode... caseModes) {
		int mode = 0;
		for (CaseMode caseMode : caseModes) {
			mode |= caseMode.value();
		}
		return of(mode);
	}

	public static CaseModeOption of(int mode) {
		if (mode < 0 || mode >= VALUES.length) {
			mode = mode & (VALUES.length - 1);
		}
		return VALUES[mode];
	}

	public CaseModeOption plus(CaseModeOption option) {
		return of(mode | option.mode);
	}

	public CaseModeOption plus(CaseMode caseMode) {
		return of(mode | caseMode.value());
	}

	public CaseModeOption minus(CaseModeOption option) {
		return of(mode & (~option.mode));
	}

	public CaseModeOption minus(CaseMode caseMode) {
		return of(mode & (~caseMode.value()));
	}

	public int getMode() {
		return mode;
	}

	public boolean is(CaseMode caseMode) {
		return caseMode.is(mode);
	}

	@Override
	public String toString() {
		return "CaseModeOption(" + Hex.formatBin(mode, CaseMode.values().length) + ')';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CaseModeOption)) return false;
		CaseModeOption that = (CaseModeOption) o;
		return getMode() == that.getMode();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getMode());
	}
}
