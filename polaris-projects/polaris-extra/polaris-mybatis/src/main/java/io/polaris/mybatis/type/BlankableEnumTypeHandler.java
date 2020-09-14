package io.polaris.mybatis.type;

import io.polaris.core.string.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.EnumTypeHandler;
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
@Alias("blankableEnumTypeHandler")
public class BlankableEnumTypeHandler<E extends Enum<E>> extends EnumTypeHandler<E> {

	private final Class<E> type;

	public BlankableEnumTypeHandler(Class<E> type) {
		super(type);
		this.type = type;
	}

	private E toEnum(String s) {
		try {
			s = Strings.trimToNull(s);
			return s == null ? null : Enum.valueOf(type, s);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		String name;
		if (parameter.getClass().isEnum()) {
			name = parameter.name();
		} else {
			name = parameter.toString();
		}
		if (jdbcType == null) {
			ps.setString(i, name);
		} else {
			ps.setObject(i, name, jdbcType.TYPE_CODE);
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);
		return toEnum(s);
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String s = rs.getString(columnIndex);
		return toEnum(s);
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String s = cs.getString(columnIndex);
		return toEnum(s);
	}
}
