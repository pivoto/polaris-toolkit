package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.string.Strings;

import java.util.Locale;

/**
 * @author Qt
 * @since 1.8
 */
public class LocaleConverter extends AbstractConverter<Locale> {
	@Override
	protected Locale convertInternal(Object value, Class<? extends Locale> targetType) {
		try {
			String str = convertToStr(value);
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
