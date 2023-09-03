package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;

import java.util.Currency;

/**
 * @author Qt
 * @since 1.8
 */
public class CurrencyConverter extends AbstractSimpleConverter<Currency> {
	private final JavaType<Currency> targetType = JavaType.of(Currency.class);

	@Override
	public JavaType<Currency> getTargetType() {
		return targetType;
	}

	@Override
	protected Currency doConvert(Object value, JavaType<Currency> targetType) {
		return Currency.getInstance(asString(value));
	}
}
