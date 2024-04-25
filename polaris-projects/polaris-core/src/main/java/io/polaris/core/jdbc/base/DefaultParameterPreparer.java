package io.polaris.core.jdbc.base;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

import io.polaris.core.date.Dates;

/**
 * @author Qt
 * @since 1.8,  Apr 25, 2024
 */
public class DefaultParameterPreparer implements ParameterPreparer {

	public static final DefaultParameterPreparer INSTANCE = new DefaultParameterPreparer();

	public static ParameterPreparer orDefault(ParameterPreparer preparer) {
		return preparer == null ? DefaultParameterPreparer.INSTANCE : preparer;
	}

	@Override
	public void set(PreparedStatement st, int index, Object value) throws SQLException {
		if (value == null) {
			st.setNull(index, Types.VARCHAR);// Types.NULL
		} else if (value instanceof String) {
			st.setString(index, (String) value);
		} else if (value instanceof Character) {
			st.setString(index, String.valueOf((Character) value));
		} else if (value instanceof Integer) {
			st.setInt(index, (Integer) value);
		} else if (value instanceof Long) {
			st.setLong(index, (Long) value);
		} else if (value instanceof Double) {
			st.setDouble(index, (Double) value);
		} else if (value instanceof Float) {
			st.setFloat(index, (Float) value);
		} else if (value instanceof Boolean) {
			st.setBoolean(index, (Boolean) value);
		} else if (value instanceof Byte) {
			st.setByte(index, (Byte) value);
		} else if (value instanceof Short) {
			st.setShort(index, (Short) value);
		} else if (value instanceof Enum) {
			st.setString(index, ((Enum<?>) value).name());
		} else if (value instanceof BigDecimal) {
			st.setBigDecimal(index, (BigDecimal) value);
		} else if (value instanceof java.sql.Timestamp) {
			st.setTimestamp(index, (java.sql.Timestamp) value);
		} else if (value instanceof java.sql.Date) {
			st.setDate(index, (java.sql.Date) value);
		} else if (value instanceof java.sql.Time) {
			st.setTime(index, (java.sql.Time) value);
		} else if (value instanceof java.util.Date) {
			st.setTimestamp(index, new java.sql.Timestamp(((java.util.Date) value).getTime()));
		} else if (value instanceof LocalDateTime) {
			st.setTimestamp(index, new java.sql.Timestamp(Dates.toMills((LocalDateTime) value)));
		} else if (value instanceof LocalDate) {
			st.setTimestamp(index, new java.sql.Timestamp(Dates.toInstant((LocalDate) value).toEpochMilli()));
		} else if (value instanceof Instant) {
			st.setTimestamp(index, new java.sql.Timestamp(((Instant) value).toEpochMilli()));
		} else if (value instanceof TemporalAccessor) {
			st.setTimestamp(index, new java.sql.Timestamp(Dates.toMills(Dates.toLocalDateTime((TemporalAccessor) value))));
		} else if (value instanceof byte[]) {
			st.setBytes(index, (byte[]) value);
		} else if (value instanceof char[]) {
			st.setCharacterStream(index, new StringReader(new String((char[]) value)));
		} else if (value instanceof Reader) {
			st.setCharacterStream(index, (Reader) value);
		} else if (value instanceof InputStream) {
			st.setBinaryStream(index, (InputStream) value);
		} else if (value instanceof Array) {
			st.setArray(index, (Array) value);
		} else if (value instanceof Blob) {
			st.setBlob(index, (Blob) value);
		} else if (value instanceof Clob) {
			st.setClob(index, (Clob) value);
		} else {
			// 可能出现不支持的类型，如报错：无效的列类型
			st.setObject(index, value);
		}
	}

}
