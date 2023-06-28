package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class CharacterConverter extends AbstractConverter<Character> {
	@Override
	protected Character convertInternal(Object value, Class<? extends Character> targetType) {
		if (value instanceof Boolean) {
			return Boolean.TRUE.equals(value) ? (char) 1 : (char) 0;
		}
		String valueStr = convertToStr(value);
		return Strings.isBlank(valueStr) ? null : valueStr.charAt(0);
	}
}
