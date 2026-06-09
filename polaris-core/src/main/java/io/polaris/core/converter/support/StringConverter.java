package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.ConversionException;
import io.polaris.core.io.IO;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.time.Dates;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Qt
 */
public class StringConverter extends AbstractSimpleConverter<String> {

	private final JavaType<String> targetType = JavaType.of(String.class);

	@Override
	public JavaType<String> getTargetType() {
		return targetType;
	}

	@Override
	protected String doConvert(Object value, JavaType<String> targetType) {
		if (value == null) {
			return null;
		}
		if (Types.isPrimitiveOrWrapper(value.getClass())) {
			return value.toString();
		}
		if (value instanceof CharSequence) {
			return value.toString();
		}
		if (value instanceof Character) {
			return String.valueOf((char) value);
		}
		if (value instanceof Enum) {
			return ((Enum<?>) value).name();
		}
		if (value instanceof Class) {
			return Types.getSafeCanonicalName(((Class<?>) value));
		}
		if (value instanceof Type) {
			return ((Type) value).getTypeName();
		}
		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).toPlainString();
		}
		if (value instanceof Number) {
			return ((Number) value).toString();
		}
		if (value instanceof Date) {
			return Dates.formatDefault((Date) value);
		}
		if (value instanceof TemporalAccessor) {
			return Dates.formatDefault((TemporalAccessor) value);
		}
		if (value instanceof TimeZone) {
			return ((TimeZone) value).getID();
		}
		if (value instanceof Clob) {
			return clobToStr((Clob) value);
		}
		if (value instanceof Blob) {
			return blobToStr((Blob) value);
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
