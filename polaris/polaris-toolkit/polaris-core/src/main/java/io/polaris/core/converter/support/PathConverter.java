package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Qt
 * @since 1.8
 */
public class PathConverter extends AbstractConverter<Path> {
	@Override
	protected Path convertInternal(Object value, Class<? extends Path> targetType) {
		try {
			if(value instanceof URI){
				return Paths.get((URI)value);
			}

			if(value instanceof URL){
				return Paths.get(((URL)value).toURI());
			}

			if(value instanceof File){
				return ((File)value).toPath();
			}

			return Paths.get(convertToStr(value));
		} catch (Exception ignore) {
		}
		return null;
	}
}
