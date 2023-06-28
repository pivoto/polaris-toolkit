package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractConverter;
import io.polaris.core.io.IO;

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
public class StringConverter extends AbstractConverter<String> {
	@Override
	protected String convertInternal(Object value, Class<? extends String> targetType) {
		if (value instanceof TimeZone) {
			return ((TimeZone) value).getID();
		} else if (value instanceof Clob) {
			return clobToStr((Clob) value);
		} else if (value instanceof Blob) {
			return blobToStr((Blob) value);
		} else if (value instanceof Type) {
			return ((Type) value).getTypeName();
		}
		return convertToStr(value);
	}

	private static String clobToStr(Clob clob) {
		Reader reader = null;
		try {
			reader = clob.getCharacterStream();
			return IO.toString(reader);
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
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
			throw new UnsupportedOperationException(e);
		} finally {
			IO.close(in);
		}
	}

}
