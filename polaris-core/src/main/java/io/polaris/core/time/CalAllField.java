package io.polaris.core.time;

import java.util.Calendar;

/**
 * @author Qt
 * @since Oct 14, 2025
 */
public enum CalAllField {
	ERA(Calendar.ERA),
	YEAR(Calendar.YEAR),
	MONTH(Calendar.MONTH),
	WEEK_OF_YEAR(Calendar.WEEK_OF_YEAR),
	WEEK_OF_MONTH(Calendar.WEEK_OF_MONTH),
	DAY_OF_MONTH(Calendar.DAY_OF_MONTH),
	DAY_OF_YEAR(Calendar.DAY_OF_YEAR),
	DAY_OF_WEEK(Calendar.DAY_OF_WEEK),
	DAY_OF_WEEK_IN_MONTH(Calendar.DAY_OF_WEEK_IN_MONTH),
	AM_PM(Calendar.AM_PM),
	HOUR(Calendar.HOUR),
	HOUR_OF_DAY(Calendar.HOUR_OF_DAY),
	MINUTE(Calendar.MINUTE),
	SECOND(Calendar.SECOND),
	MILLISECOND(Calendar.MILLISECOND),
	ZONE_OFFSET(Calendar.ZONE_OFFSET),
	DST_OFFSET(Calendar.DST_OFFSET),
	;
	private final int value;

	CalAllField(int value) {
		this.value = value;
	}

	public int value() {
		return value;
	}


}
