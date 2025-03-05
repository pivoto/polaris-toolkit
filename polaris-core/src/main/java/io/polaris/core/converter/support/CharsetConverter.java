package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;
import io.polaris.core.string.Strings;

import java.nio.charset.Charset;

/**
 * @author Qt
 * @since 1.8
 */
public class CharsetConverter extends AbstractSimpleConverter<Charset> {
	private final JavaType<Charset> targetType = JavaType.of(Charset.class);

	@Override
	public JavaType<Charset> getTargetType() {
		return targetType;
	}
	@Override
	protected Charset doConvert(Object value, JavaType<Charset> targetType) {
		String str = asSimpleString(value);
		return Strings.isBlank(str) ? Charset.defaultCharset() : Charset.forName(str);
	}
}
