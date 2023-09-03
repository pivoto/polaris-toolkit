package io.polaris.mybatis.type;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
@Slf4j
@Alias("blankableEnumOrdinalTypeHandler")
public class BlankableEnumOrdinalTypeHandler<E extends Enum<E>> extends EnumOrdinalTypeHandler<E> {

	private final Class<E> type;
	private final E[] enums;

	public BlankableEnumOrdinalTypeHandler(Class<E> type) {
		super(type);
		this.type = type;
		this.enums = type.getEnumConstants();
		if (this.enums == null) {
			throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
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
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(i, parameter.ordinal());
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
