package io.polaris.core.time;

import java.util.Calendar;

/**
 * @author Qt
 * @since Oct 14, 2025
 */
public enum CalStdField {
	YEAR(Calendar.YEAR),
	MONTH(Calendar.MONTH),
	DAY(Calendar.DAY_OF_MONTH),
	HOUR(Calendar.HOUR_OF_DAY),
	HOUR_12(Calendar.HOUR),
	MINUTE(Calendar.MINUTE),
	SECOND(Calendar.SECOND),
	MILLISECOND(Calendar.MILLISECOND),
	;
	private final int value;

	CalStdField(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}


}
