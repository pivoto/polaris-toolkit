package io.polaris.mybatis.type;

import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @see BooleanTypeHandler
 * @since Aug 28, 2023
 */
@Slf4j
@Alias("dynamicBooleanTypeHandler")
public class DynamicBooleanTypeHandler extends BaseTypeHandler<Object> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
		throws SQLException {
		Boolean val = null;
		if (parameter != null) {
			if (parameter instanceof Boolean) {
				val = (Boolean) parameter;
			} else if (parameter instanceof String) {
				String s = (String) parameter;
				val = "true".equalsIgnoreCase(s) || "1".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s);
			} else if (parameter instanceof Number) {
				val = ((Number) parameter).intValue() != 0;
			} else if (parameter instanceof Collection) {
				val = !((Collection<?>) parameter).isEmpty();
			} else if (parameter instanceof Map) {
				val = !((Map<?, ?>) parameter).isEmpty();
			} else if (parameter.getClass().isArray()) {
				val = Array.getLength(parameter) > 0;
			}
		}
		if (val == null) {
			///ps.setNull(i, Types.INTEGER);
			ps.setNull(i, Types.VARCHAR);
		} else {
			ps.setBoolean(i, val);
		}
	}

	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType)
		throws SQLException {
		ps.setBoolean(i, parameter);
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName)
		throws SQLException {
		return rs.getBoolean(columnName);
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex)
		throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex)
		throws SQLException {
		return cs.getBoolean(columnIndex);
	}

}
