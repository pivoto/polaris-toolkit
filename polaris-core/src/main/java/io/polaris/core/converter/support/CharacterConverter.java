package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
public class CharacterConverter extends AbstractSimpleConverter<Character> {
	private final JavaType<Character> targetType = JavaType.of(Character.class);

	@Override
	public JavaType<Character> getTargetType() {
		return targetType;
	}

	@Override
	protected Character doConvert(Object value, JavaType<Character> targetType) {
		if (value instanceof Boolean) {
			return Boolean.TRUE.equals(value) ? (char) 1 : (char) 0;
		}
		String valueStr = asSimpleString(value);
		return Strings.isBlank(valueStr) ? null : valueStr.charAt(0);
	}
}
