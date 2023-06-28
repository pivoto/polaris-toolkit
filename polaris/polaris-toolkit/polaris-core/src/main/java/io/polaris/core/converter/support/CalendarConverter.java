package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.date.Dates;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Qt
 * @since 1.8
 */
public class CalendarConverter extends AbstractConverter<Calendar> {
	@Setter
	@Getter
	private String format;

	public CalendarConverter() {
	}

	public CalendarConverter(String format) {
		this.format = format;
	}

	@Override
	protected Calendar convertInternal(Object value, Class<? extends Calendar> targetType) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		if (value instanceof Date) {
			cal.setTimeInMillis(((Date) value).getTime());
		} else if (value instanceof Long) {
			cal.setTimeInMillis(((Long) value));
		} else {
			String valueStr = convertToStr(value);
			Date date = Dates.parseDate(valueStr);
			cal.setTime(date);
		}
		return cal;
	}
}
