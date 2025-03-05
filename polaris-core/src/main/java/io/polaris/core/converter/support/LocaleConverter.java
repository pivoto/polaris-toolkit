package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;
import io.polaris.core.string.Strings;

import java.util.Locale;

/**
 * @author Qt
 * @since 1.8
 */
public class LocaleConverter extends AbstractSimpleConverter<Locale> {

	private final JavaType<Locale> targetType = JavaType.of(Locale.class);

	@Override
	public JavaType<Locale> getTargetType() {
		return targetType;
	}

	@Override
	protected Locale doConvert(Object value, JavaType<Locale> targetType) {
		try {
			String str = asSimpleString(value);
			if (Strings.isBlank(str)) {
				return null;
			}
			final String[] items = str.split("_");
			if (items.length == 1) {
				return new Locale(items[0]);
			}
			if (items.length == 2) {
				return new Locale(items[0], items[1]);
			}
			return new Locale(items[0], items[1], items[2]);
		} catch (Exception ignore) {
		}
		return null;
	}
}
