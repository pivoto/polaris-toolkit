package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.time.Dates;
import io.polaris.core.lang.JavaType;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Qt
 * @since 1.8
 */
public class CalendarConverter extends AbstractSimpleConverter<Calendar> {
	@Setter
	@Getter
	private String format;

	public CalendarConverter() {
	}

	public CalendarConverter(String format) {
		this.format = format;
	}

	@Override
	public JavaType<Calendar> getTargetType() {
		return JavaType.of(Calendar.class);
	}

	@Override
	protected Calendar doConvert(Object value, JavaType<Calendar> targetType) {
		Calendar cal = Calendar.getInstance(TimeZone.getDefault());
		if (value instanceof Date) {
			cal.setTimeInMillis(((Date) value).getTime());
		} else if (value instanceof Long) {
			cal.setTimeInMillis(((Long) value));
		} else {
			String valueStr = asString(value);
			Date date = Dates.parseDate(valueStr);
			cal.setTime(date);
		}
		return cal;
	}
}
