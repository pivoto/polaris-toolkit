package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * @author Qt
 * @since 1.8
 */
public class URIConverter extends AbstractConverter<URI> {
	@Override
	protected URI convertInternal(Object value, Class<? extends URI> targetType) {
		try {
			if(value instanceof File){
				return ((File)value).toURI();
			}
			if(value instanceof URL){
				return ((URL)value).toURI();
			}
			return new URI(convertToStr(value));
		} catch (Exception ignore) {
		}
		return null;
	}
}
