package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.ConversionException;
import io.polaris.core.io.IO;
import io.polaris.core.lang.JavaType;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.util.TimeZone;

/**
 * @author Qt
 * @since 1.8
 */
public class StringConverter extends AbstractSimpleConverter<String> {

	private final JavaType<String> targetType = JavaType.of(String.class);
	@Override
	public JavaType<String> getTargetType() {
		return targetType;
	}

	@Override
	protected String doConvert(Object value, JavaType<String> targetType) {
		if (value instanceof TimeZone) {
			return ((TimeZone) value).getID();
		} else if (value instanceof Clob) {
			return clobToStr((Clob) value);
		} else if (value instanceof Blob) {
			return blobToStr((Blob) value);
		} else if (value instanceof Enum) {
			return ((Enum<?>) value).name();
		} else if (value instanceof Class) {
			return ((Class<?>) value).getCanonicalName();
		} else if (value instanceof Type) {
			return ((Type) value).getTypeName();
		}
		return asComplexString(value);
	}

	private static String clobToStr(Clob clob) {
		Reader reader = null;
		try {
			reader = clob.getCharacterStream();
			return IO.toString(reader);
		} catch (Exception e) {
			throw new ConversionException(e);
		} finally {
			IO.close(reader);
		}
	}

	private static String blobToStr(Blob blob) {
		InputStream in = null;
		try {
			in = blob.getBinaryStream();
			return IO.toString(in, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new ConversionException(e);
		} finally {
			IO.close(in);
		}
	}

}
