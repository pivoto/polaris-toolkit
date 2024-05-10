package io.polaris.mybatis.type;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

/**
 * @author Qt
 * @see BlankableEnumOrdinalTypeHandler
 * @since  Aug 28, 2023
 */
@Slf4j
@Alias("dynamicEnumOrdinalTypeHandler" )
public class DynamicEnumOrdinalTypeHandler<E extends Enum<E>> extends BaseTypeHandler<Object> {

	private final Class<E> type;
	private final E[] enums;

	public DynamicEnumOrdinalTypeHandler(Class<E> type) {
		this.type = type;
		this.enums = type.getEnumConstants();
		if (this.enums == null) {
			throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type." );
		}
	}

	private E toEnum(int i) {
		try {
			return enums[i];
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
		throws SQLException {
		int ordinal = -1;
		if (parameter != null) {
			if (parameter.getClass().isEnum()) {
				ordinal = ((Enum) parameter).ordinal();
			}
		}

		if (ordinal >= 0) {
			ps.setInt(i, ordinal);
		} else {
			if (jdbcType == null) {
				ps.setNull(i, Types.VARCHAR);
			} else {
				ps.setNull(i, jdbcType.TYPE_CODE);
			}
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		int i = rs.getInt(columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return toEnum(i);
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		int i = rs.getInt(columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return toEnum(i);
		}
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		int i = cs.getInt(columnIndex);
		if (cs.wasNull()) {
			return null;
		} else {
			return toEnum(i);
		}
	}
}
