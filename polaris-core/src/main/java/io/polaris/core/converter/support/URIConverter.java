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
public class URIConverter extends AbstractSimpleConverter<URI> {
	private final JavaType<URI> targetType = JavaType.of(URI.class);

	@Override
	public JavaType<URI> getTargetType() {
		return targetType;
	}

	@Override
	protected URI doConvert(Object value, JavaType<URI> targetType) {
		try {
			if(value instanceof File){
				return ((File)value).toURI();
			}
			if(value instanceof URL){
				return ((URL)value).toURI();
			}
			return new URI(asSimpleString(value));
		} catch (Exception ignore) {
		}
		return null;
	}
}
