package io.polaris.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

import io.polaris.core.converter.Converters;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @see org.apache.ibatis.type.DateTypeHandler
 * @see org.apache.ibatis.type.LocalDateTypeHandler
 * @since Aug 28, 2023
 */
@Slf4j
@MappedTypes(Date.class)
@MappedJdbcTypes(value = {JdbcType.DATE, JdbcType.TIMESTAMP, JdbcType.TIME}, includeNullJdbcType = true)
@Alias("dynamicDateTypeHandler")
public class DynamicDateTypeHandler extends BaseTypeHandler<Object> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
		throws SQLException {
		if (parameter == null) {
			ps.setNull(i, Types.TIMESTAMP);
			return;
		}
		if (parameter instanceof Date) {
			ps.setDate(i, new java.sql.Date(((Date) parameter).getTime()));
		} else if (parameter instanceof Date) {
			ps.setDate(i, new java.sql.Date(((Date) parameter).getTime()));
		} else if (parameter instanceof Number) {
			ps.setDate(i, new java.sql.Date(((Number) parameter).longValue()));
		} else {
			Date date = Converters.convert(Date.class, parameter);
			ps.setDate(i, new java.sql.Date(date.getTime()));
		}
	}

	@Override
	public Date getNullableResult(ResultSet rs, String columnName)
		throws SQLException {
		java.sql.Date date = rs.getDate(columnName);
		return date;
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex)
		throws SQLException {
		return rs.getDate(columnIndex);
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex)
		throws SQLException {
		return cs.getDate(columnIndex);
	}
}
