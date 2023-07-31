package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * @author Qt
 * @since 1.8
 */
public class URLConverter extends AbstractSimpleConverter<URL> {
	private final JavaType<URL> targetType = JavaType.of(URL.class);

	@Override
	public JavaType<URL> getTargetType() {
		return targetType;
	}

	@Override
	protected URL doConvert(Object value, JavaType<URL> targetType) {
		try {
			if (value instanceof File) {
				return ((File) value).toURI().toURL();
			}

			if (value instanceof URI) {
				return ((URI) value).toURL();
			}
			return new URL(asString(value));
		} catch (Exception ignore) {
		}
		return null;
	}
}
