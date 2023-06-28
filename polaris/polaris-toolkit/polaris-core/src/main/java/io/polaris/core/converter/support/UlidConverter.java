package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.ulid.Ulid;

import java.util.UUID;

/**
 * @author Qt
 * @since 1.8
 */
public class UlidConverter extends AbstractConverter<Ulid> {
	@Override
	protected Ulid convertInternal(Object value, Class<? extends Ulid> targetType) {
		return Ulid.from(convertToStr(value));
	}
}
