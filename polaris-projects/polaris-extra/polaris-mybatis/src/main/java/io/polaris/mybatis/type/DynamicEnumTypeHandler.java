package io.polaris.mybatis.type;

import io.polaris.core.string.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
@Slf4j
@Alias("dynamicEnumTypeHandler")
public class DynamicEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<Object> {

	private final Class<E> type;

	public DynamicEnumTypeHandler(Class<E> type) {
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
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
		throws SQLException {
		String name = null;
		if (parameter != null) {
			if (parameter.getClass().isEnum()) {
				name = ((Enum) parameter).name();
			} else {
				name = parameter.toString();
			}
		}
		if (name == null) {
			if (jdbcType == null) {
				ps.setNull(i, Types.VARCHAR);
			} else {
				ps.setNull(i, jdbcType.TYPE_CODE);
			}
		} else {
			if (jdbcType == null) {
				ps.setString(i, name);
			} else {
				ps.setObject(i, name, jdbcType.TYPE_CODE);
			}
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
