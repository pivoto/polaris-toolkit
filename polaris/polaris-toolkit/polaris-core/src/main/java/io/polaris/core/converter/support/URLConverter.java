package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * @author Qt
 * @since 1.8
 */
public class URLConverter extends AbstractConverter<URL> {
	@Override
	protected URL convertInternal(Object value, Class<? extends URL> targetType) {
		try {
			if (value instanceof File) {
				return ((File) value).toURI().toURL();
			}

			if (value instanceof URI) {
				return ((URI) value).toURL();
			}
			return new URL(convertToStr(value));
		} catch (Exception ignore) {
		}
		return null;
	}
}
