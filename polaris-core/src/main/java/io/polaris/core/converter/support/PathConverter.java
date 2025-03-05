package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.lang.JavaType;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Qt
 * @since 1.8
 */
public class PathConverter extends AbstractSimpleConverter<Path> {
	private final JavaType<Path> targetType = JavaType.of(Path.class);

	@Override
	public JavaType<Path> getTargetType() {
		return targetType;
	}

	@Override
	protected Path doConvert(Object value, JavaType<Path> targetType) {
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

			return Paths.get(asSimpleString(value));
		} catch (Exception ignore) {
		}
		return null;
	}
}
