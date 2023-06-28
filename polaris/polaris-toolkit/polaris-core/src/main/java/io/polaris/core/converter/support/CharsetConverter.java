package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.string.Strings;

import java.nio.charset.Charset;

/**
 * @author Qt
 * @since 1.8
 */
public class CharsetConverter extends AbstractConverter<Charset> {
	@Override
	protected Charset convertInternal(Object value, Class<? extends Charset> targetType) {
		String str = convertToStr(value);
		return Strings.isBlank(str) ? Charset.defaultCharset() : Charset.forName(str);
	}
}
