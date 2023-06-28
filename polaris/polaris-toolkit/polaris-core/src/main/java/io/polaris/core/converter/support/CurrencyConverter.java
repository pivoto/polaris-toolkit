package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.util.Currency;

/**
 * @author Qt
 * @since 1.8
 */
public class CurrencyConverter extends AbstractConverter<Currency> {
	@Override
	protected Currency convertInternal(Object value, Class<? extends Currency> targetType) {
		return Currency.getInstance(convertToStr(value));
	}
}
